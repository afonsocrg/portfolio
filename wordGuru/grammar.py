
#-------------------------------------------------------------------------------------------------------------#
#                                  TERMINAL SYMBOLS OF PORTUGUESE BNF GRAMMAR                                 #
#-------------------------------------------------------------------------------------------------------------#

artigo_definido = ('A', 'O')
diphthong = ('AE', 'AU', 'EI', 'OE', 'OI', 'IU')
diphthong_word = ('AI', 'AO', 'EU', 'OU')
consonant = ('B', 'C', 'D', 'F', 'G', 'H', 'J', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'X', 'Z')
final_consonant = ('N', 'P')
terminal_consonant = ('L', 'M', 'R', 'S', 'X', 'Z')
frequent_consonant = ('D', 'L', 'M', 'N', 'P', 'R', 'S', 'T', 'V')
syllable_3 = ('QUA', 'QUE', 'QUI', 'GUE', 'GUI')
pair_consonants = ('BR', 'CR', 'FR', 'GR', 'PR', 'TR', 'VR', 'BL', 'CL', 'FL', 'GL', 'PL')
monosyllable_2 = ('AR', 'IR', 'EM', 'UM')


#-------------------------------------------------------------------------------------------------------------#
#                                                     FUNCTIONS                                               #
#-------------------------------------------------------------------------------------------------------------#

def isWord(word):
    ''' STRING > BOOLEAN
    True if valid word. False otherwise
    '''
    def untill_last_n(word, n):
        ''' STRING, INT > STRING
        Removes last n letters from word
        '''
        return word[:len(word)-n]

    def get_last_n(word, n):
        ''' STRING, INT > STRING
        Gets last n letters from word
        '''
        return word[len(word)-n:]

    def from_first_n(word, n):
        ''' STRING, INT > STRING
        Removes first n letters from word
        '''
        return word[n:]

    def get_first_n(word, n):
        ''' STRING, INT > STRING
        Gets first n letters from word
        '''
        return word[:n]

    def madeof_syllable(word_ini):
        ''' STRING > BOOLEAN
        True if it's possible to divide word in valid syllables
        '''
        if word_ini == '':
            return True     #default case

        for n_letters in range(1, 6):
            first = get_first_n(word_ini, n_letters)    #split word (try every lenght possible)
            last = from_first_n(word_ini, n_letters)
            if is_syllable(first):                      #valid syllables?
                if madeof_syllable(last):
                    return True

        return False

    if not isinstance(word, str):
        raise ValueError('isWord:invalid argument')


    if is_monosyllable(word):
        return True
    else:
        lastSyllableLen = 2
        while lastSyllableLen <= 5:                             #for each valid final syllable
            lastSyllable = get_last_n(word, lastSyllableLen)    # test if beggining is valid
            if is_final_syllable(lastSyllable):
                word_ini = untill_last_n(word, lastSyllableLen)
                if madeof_syllable(word_ini):
                    return True
            lastSyllableLen = lastSyllableLen + 1
        return False

def is_syllable(semiPal):
    ''' STRING > BOOLEAN
    True if argument is valid syllable
    ''' 
    if not isinstance(semiPal, str):
        raise ValueError('is_syllable:invalid argument')    
    return (len(semiPal)<=5 and (
        is_syllable_5(semiPal) or 
        is_syllable_4(semiPal) or 
        is_syllable_3(semiPal) or 
        is_syllable_2(semiPal) or 
        is_vowel(semiPal)))

def is_final_syllable(semiPal):
    ''' STRING > BOOLEAN
    True if argument is final syllable according to grammar'''
    
    if not isinstance(semiPal, str):
        raise ValueError('is_final_syllable:invalid argument')
    return  (1 < len(semiPal) <= 5 and (
        is_monosyllable_2(semiPal) or 
        is_monosyllable_3(semiPal) or 
        is_syllable_4(semiPal) or 
        is_syllable_5(semiPal)))


def is_syllable_2(letters):
    ''' STRING > BOOLEAN
    True if valid 2 lettered syllable
    '''
    if len(letters) != 2:
        return False
    else:
        first_letter = letters[0]
        sec_letter = letters[1]
        return (is_pair_of_vowels(letters) or 
            (is_consonant(first_letter) and is_vowel(sec_letter)) or 
            (is_vowel(first_letter) and is_final_consonant(sec_letter)))

def is_syllable_3(letters):
    ''' STRING > BOOLEAN
    True if valid 3 lettered syllable'''
    if len(letters) != 3:
        return False
    else:
        first_letter = letters[0]
        sec_letter = letters[1]
        thrd_letter = letters[2]
        first_pair = first_letter + sec_letter
        second_pair = sec_letter + thrd_letter
        return (letters in syllable_3 or 
            (is_vowel(first_letter) and second_pair == 'NS') or 
            (is_consonant(first_letter) and is_pair_of_vowels(second_pair)) or 
            (is_consonant(first_letter) and is_vowel(sec_letter) and is_final_consonant(thrd_letter)) or 
            (is_pair_of_vowels(first_pair) and is_final_consonant(thrd_letter)) or 
            (is_pair_of_consonants(first_pair) and is_vowel(thrd_letter)))

def is_syllable_4(letters):
    ''' STRING > BOOLEAN
    True if valid 4 lettered syllable
    '''
    if len(letters)!=4:
        return False
    else:
        first_letter = letters[0]
        sec_letter = letters[1]
        thrd_letter = letters[2]
        fourth_letter = letters[3]
        first_pair = first_letter + sec_letter
        middle_pair = sec_letter + thrd_letter
        second_pair = thrd_letter + fourth_letter
        return ((is_pair_of_vowels(first_pair) and second_pair == 'NS') or 
            (is_consonant(first_letter) and is_vowel(sec_letter) and (second_pair in ('NS', 'IS'))) or 
            (is_pair_of_consonants(first_pair) and is_pair_of_vowels(second_pair)) or 
            (is_consonant(first_letter) and is_pair_of_vowels(middle_pair) and is_final_consonant(fourth_letter)))

def is_syllable_5(letters):
    ''' STRING > BOOLEAN
    True if valid 5 lettered syllable
    '''
    if len(letters) != 5:
        return False
    else:
        first_pair = letters[0] + letters[1]
        middle_letter = letters[2]
        second_pairal = letters[3] + letters[4]
        return is_pair_of_consonants(first_pair) and is_vowel(middle_letter) and second_pairal == 'NS'


def is_monosyllable(semiPal):
    ''' STRING > BOOLEAN
    True if valid monosyllable
    '''
    if not isinstance(semiPal, str):
        raise ValueError('is_monosyllable:invalid argument')     
    return (is_vowel_word(semiPal) or 
        is_monosyllable_2(semiPal) or 
        is_monosyllable_3(semiPal))

def is_monosyllable_2(letters):
    ''' STRING > BOOLEAN
    True if valid 2 lettered monosyllable
    '''
    if len(letters)!=2:
        return False
    else:
        first_letter = letters[0]
        sec_letter = letters[1]
        return (letters in monosyllable_2 or 
            is_diphthong_word(letters) or 
            (is_vowel_word(first_letter) and sec_letter=='S') or 
            (is_frequent_consonant(first_letter) and is_vowel(sec_letter)))

def is_monosyllable_3(letters):
    ''' STRING > BOOLEAN
    True if valid 3 lettered monosyllable
    '''
    if len(letters) != 3:
        return False
    else:
        first_letter = letters[0]
        sec_letter = letters[1]
        thrd_letter = letters[2]
        first_pair = first_letter + sec_letter
        second_pair = sec_letter + thrd_letter

        return ((is_consonant(first_letter) and is_vowel(sec_letter) and is_terminal_consonant(thrd_letter)) or 
            (is_consonant(first_letter) and is_diphthong(second_pair)) or 
            (is_pair_of_vowels(first_pair) and is_terminal_consonant(thrd_letter)))


def is_vowel(letter):
    ''' STRING > BOOLEAN
    True if argument is vowel
    '''
    return (is_vowel_word(letter) or 
        letter == 'I' or 
        letter == 'U')

def is_vowel_word(letter):
    ''' STRING > BOOLEAN
    True if argument is 'A', 'E' or 'O'
    ''' 
    return (e_artigo_def(letter) or letter == 'E')

def e_artigo_def(letter):
    ''' STRING > BOOLEAN
    True if letter is 'A' or 'O'
    ''' 
    return letter in artigo_definido

def is_pair_of_vowels(letters):
    ''' STRING > BOOLEAN
    True if valid pair of vowels
    '''
    return (is_diphthong(letters) or 
        letters=='IA' or 
        letters=='IO')

def is_diphthong(letters):
    ''' STRING > BOOLEAN
    True if valid diphthong
    '''
    return (letters in diphthong or 
        is_diphthong_word(letters))

def is_diphthong_word(letters):
    ''' STRING > BOOLEAN
    True if argument is a single diphthong word
    '''
    return letters in diphthong_word

def is_consonant(letter):
    ''' STRING > BOOLEAN
    True if argument is consonant
    '''
    return letter in consonant

def is_final_consonant(letter):
    ''' STRING > BOOLEAN
    True if valid final consonant
    '''
    return (letter in final_consonant or 
        is_terminal_consonant(letter))

def is_terminal_consonant(letter):
    ''' STRING > BOOLEAN
    True if valid terminal consonant
    '''
    return letter in terminal_consonant

def is_frequent_consonant(letter):
    ''' STRING > BOOLEAN
    True if frequent consonant
    '''
    return letter in frequent_consonant

def is_pair_of_consonants(letters):
    ''' STRING > BOOLEAN
    True if valid pair of consonants
    '''
    return letters in pair_consonants
