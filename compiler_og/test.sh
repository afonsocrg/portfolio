#!/bin/bash
#---------------------------------------------------------------
#             CONFIGURE THESE VARIABLES IF NEEDED
#---------------------------------------------------------------
# CVS repository directory
OGPATH=/home/lulatheboo/documents/compiladores/projeto/og

# lib directory (where you have librts, libcdk... installed)
LIBPATH=/home/lulatheboo/compiladores/root/usr/lib

# directory where you have the .og programs
TESTDIR=/home/lulatheboo/documents/compiladores/projeto/auto-tests

# directory where you have the expected result
RESLDIR=/home/lulatheboo/documents/compiladores/projeto/expected

# directory where you want your log to be written
LOGDIR=/home/lulatheboo/documents/compiladores/projeto


#---------------------------------------------------------------
#                DO NOT CHANGE AFTER THIS LINE
#---------------------------------------------------------------
# output
PASSED="\033[0;32mPASSED\033[0m"
# set og compiler path
OG="${OGPATH}/og"

# empty out log file
LOGFILE=${LOGDIR}/log.txt
echo '' > ${LOGFILE}

# set statistic variables
numPassed=0
numTests=0


# compile compiler if needed
if [[ ! -f ${OG} ]]
then
    echo "Compiling target"
    cd ${OGPATH}
    make clean && make
    cd ${OLDPWD}
fi

rm -f ${TESTDIR}/*.pf
rm -f ${TESTDIR}/*.asm
rm -f ${TESTDIR}/*.o
find -f ${TESTDIR} -maxdepth 1 -type f -executable -exec rm {} +


# run tests
for file in $(ls ${TESTDIR}/*.og); do
# for file in $(ls ${TESTDIR}/S*.og); do
    # get test name
    base=$(basename -- "${file}")
    testname="${base%.*}"
    outname=${TESTDIR}/${testname}

    numTests=$(( numTests+1 ))

    echo -en "\nTesting ${testname}" # standard out

    echo ================================================================= >> ${LOGFILE};
    eval "echo ${base} >> ${LOGFILE};"

    # compile og (go to next test if failed)
    echo og compilation: >> log.txt
    # eval "${OG} -g ${file} &>> ${LOGFILE}"
    if [[ $(${OG} -g ${file} &>> ${LOGFILE} | wc -c) -ne 0 ]]; then
    # if [[ $? -ne 0 ]]; then
        echo "Failed compilation"
        continue
    fi

    eval "sed -n '/; /s/; //p' ${outname}.asm > ${outname}.pf"

    # compile asm (go to next test if failed)
    eval "echo yasm compilation: >> log.txt"
    eval "yasm -felf32 ${outname}.asm -o ${outname}.o &>> ${LOGFILE};"
    if [[ $? -ne 0 ]]; then
        continue
    fi

    # link asm (go to next test if failed)
    eval "echo linker: >> ${LOGFILE}"
    eval "ld -melf_i386 -o ${outname} ${outname}.o -L${LIBPATH} -lrts"
    if [[ $? -ne 0 ]]; then
        continue
    fi

    # check result
    eval "${outname} > ${outname}.out"
    sed -i '$a\'  ${outname}.out # add newline at end of file, just in case
    eval "diff -s -y --width=80 ${RESLDIR}/${testname}.out ${outname}.out > ${outname}.diff"
    if [[ $? -eq 0 ]]
    then
        numPassed=$(( numPassed+1 ))
        # echo "PASSED" >> ${LOGFILE}
        echo -e "${PASSED}" >> ${LOGFILE}
        printf ": ${PASSED}"
    else
        echo "FAILED. Showing diff:" >> ${LOGFILE}
        cat ${outname}.diff >> ${LOGFILE}
    fi
done;
echo
echo Done
printf "Passed %d/%d (%.2f%%) tests\n" ${numPassed} ${numTests} `echo "scale=2; ${numPassed}*100/${numTests}" | bc`
