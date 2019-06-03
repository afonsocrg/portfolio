var description = document.getElementById("description");
var insertText = document.getElementById("insertText");
var ok = document.getElementById("okButton");
var confirmPopUp = document.getElementById("confirmationPopUp")

var userInput = document.getElementById("userInput")
var confirmButton = document.getElementById("confirmButton")
var tryAgainButton = document.getElementById("tryButton")

var LeftArrow = document.getElementById("leftArrow");

var toWrite;

let goBack = function(){
    window.history.back();
}

function init(){
    var toWrite = localStorage.getItem("description")
    if(toWrite === "self") {
        description.innerHTML = "Por favor diga \<br> o seu nome";
        insertText.setAttribute("type", "text");
    } else if (toWrite === "age"){
        description.innerHTML = "Por favor diga a sua data de nascimento";
        insertText.setAttribute("type", "date");
    }
}

function showPopUp(){
    confirmPopUp.style.display= "block"
    description.style.filter = "blur(8px)"
    ok.style.filter = "blur(8px)"

    userInput.innerHTML = (toWrite === "age" ? formatDate(insertText.value) : insertText.value)
}

function validDate(date) {
    let dateArr = date.split("-");
    let td = new Date();
    console.log(!(dateArr[2] > td.getFullYear() || dateArr[1] > td.getMonth() || dateArr[0] > td.getDay()) ? "valid" : "invalid");
    return !(Number(dateArr[2]) > td.getFullYear() || Number(dateArr[1]) > td.getMonth() || Number(dateArr[0]) > td.getDay());
}

function confirmField() {
    hidePopUp();

    if(toWrite === "self") {
        localStorage.setItem("user-name", insertText.value);
    } else if (toWrite === "age" /*&& validDate(insertText.value)*/){
        localStorage.setItem("user-age", formatDate(insertText.value));
    }
    document.location.href = "setup.html"; // just going back in history
                                           // doesn't work.
}

function formatDate(date) {
    split = date.split("-");
    return split[2] + "/" + split[1] + "/" + split[0];
}

function tryAgain() {
    insertText.value = "";
    hidePopUp();
}

function hidePopUp() {
    confirmPopUp.style.display = "none";
    description.style.filter = ""
    ok.style.filter = ""
}

function updateVal() {
    toWrite = localStorage.getItem("description")
    
    showPopUp();
}

LeftArrow   .onclick = goBack;
ok.onclick = updateVal;

confirmButton.onclick = confirmField;
tryAgainButton.onclick = tryAgain;

init()
