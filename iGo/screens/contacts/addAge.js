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


function showPopUp(){
    confirmPopUp.style.display= "block"
    description.style.filter = "blur(8px)"
    ok.style.filter = "blur(8px)"

    userInput.innerHTML = formatDate(insertText.value)
}

function validDate(date) {
    let dateArr = date.split("-");
    let td = new Date();
    console.log(!(dateArr[2] > td.getFullYear() || dateArr[1] > td.getMonth() || dateArr[0] > td.getDay()) ? "valid" : "invalid");
    return !(Number(dateArr[2]) > td.getFullYear() || Number(dateArr[1]) > td.getMonth() || Number(dateArr[0]) > td.getDay());
}

function confirmField() {
    hidePopUp();

    localStorage.setItem("contact-age", formatDate(insertText.value));
    localStorage.setItem('clean', false)
    document.location.href = "addContact.html"; // just going back in history
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
    
    showPopUp();
}

LeftArrow   .onclick = goBack;
ok.onclick = updateVal;

confirmButton.onclick = confirmField;
tryAgainButton.onclick = tryAgain;


insertText.setAttribute("type", "date");
