var LeftArrow = document.getElementById("leftArrow");
var inputZone = document.getElementById("input-zone");
var textBox = document.getElementById("textBox");
var insertText = document.getElementById("insertText");

var translateButton = document.getElementById("doTranslate");

var mic = document.getElementById("translate-mic-container");
var micImg = document.getElementById("micImg");

var backSpace = document.getElementById("backspace-container");
var backSpaceImg = document.getElementById("backSpace");

var text = [];
var reading = true;

let newW = []

var changePopUp = document.getElementById("changePopUp")

var message = document.getElementById("messageTitle")

var userInput = document.getElementById("word")
var changeButton = document.getElementById("changeButton")
var tryAgainButton = document.getElementById("tryButton")
var eliminateButton = document.getElementById("eliminateButton")

var word;

function goBack() {
    document.location.href = "addContact.html"
}

function submitName() {
    localStorage.setItem('clean', false)
    localStorage.setItem("contact-name", text.join(" "));
    document.location.href = "addContact.html";
}

function handleInput(e) {
    let content = e.target.value;
    text = content.split(" ");

    if(content == "") {
        text = [];
        displayInput();
    } else if (content[content.length-1] == " ") { // new word)
        text.pop();
        displayInput();
    }
}

function handleChange(e) {
    let content = e.target.value;
    if(content == "")
        text = []
    else
        text = content.split(" ");

    displayInput();
}

function loadPlaceholder() {
    console.log("loading placeholder");
    textBox.innerHTML = "";
    var s = document.createElement("span");
    s.className = "input-placeholder";
    s.innerHTML = "Diga o seu nome... Carregue no microfone quando acabar"

    textBox.appendChild(s);
}

function displayInput() {
    textBox.innerHTML = "";

    if(text.length == 0) return loadPlaceholder();
    
    for(let i = 0; i < text.length && text[i] != ""; i++) {
        var s = document.createElement("span");
        s.className = "input-word";
        s.id = i;
        s.innerHTML = text[i];
        s.onclick = changeWord;
    
        textBox.appendChild(s);
        inputZone.scrollTop = textBox.scrollHeight;
    }
}

function changeWord(e) {
    if(reading) return;
    word = e.target;

    changePopUp.style.display = "block"
    textBox.style.filter = "blur(8px)";

    userInput.innerHTML = word.innerHTML;
}

function tryAgain() {
    message.innerHTML = "<b>Não foi isto que quis dizer?</b>"

    changeButton.innerHTML = "Alterar"
    eliminateButton.innerHTML = "Eliminar";
    eliminateButton.style.backgroundColor = "grey";

    tryAgainButton.onclick = tryAgain;
    eliminateButton.onclick = eliminate;
    changeButton.onclick = change;

    insertText.disabled = !reading;

    insertText.onchange = handleChange;
    insertText.oninput = handleInput;

    changePopUp.style.display = "none";
    textBox.style.filter = "";
}

function eliminate() {
    text.splice(parseInt(word.id),1);
    insertText.value = text.join(" ");
    displayInput();
    changePopUp.style.display = "none"
    textBox.style.filter = "";
}

function change(){
    message.innerHTML = "<b>Corrija a palavra</b>"
    changeButton.innerHTML = "Repetir"
    eliminateButton.innerHTML = "OK";
    eliminateButton.style.backgroundColor = "green";

    userInput.innerHTML = "";

    changeButton.onclick = repeat;
    eliminateButton.onclick = confirm;

    insertText.disabled = false;
    insertText.value = "";

    insertText.onchange = wordChange;
    insertText.oninput = wordInput;
}

function confirm(){
    text[parseInt(word.id)] = userInput.innerHTML;
    insertText.value = text.join(" ");
    displayInput();

    tryAgain();
}

function repeat(){
    if(userInput.innerHTML != ""){
        change()
    } 
}

function wordChange(e){
    let content = e.target.value;
    if(content == "")
        newW = []
    else {
        newW = content.split(" ");
        userInput.innerHTML = newW[0]
        insertText.disabled = true;
    }
}

function wordInput(e){
    let content = e.target.value;
    newW = content.split(" ");

    if(content == "") {
        newW = [];
        
    } else if (content[content.length-1] == " ") { //word
        insertText.disabled = true;
        userInput.innerHTML = newW[0]
    }
}





function toggleMic() {
    insertText.disabled = reading;
    micImg.src = "../../images/microphone" + (reading ? "_disabled" : "") + ".svg";
    translateButton.style.display = (reading && text.length > 0) ? "block" : "none";
    inputZone.scrollTop = textBox.scrollHeight;
    reading = !reading;
}

function backspacing() {
    text.pop();
    insertText.value = text.join(" ");
    displayInput();
}

function init() {
    insertText.value = "";
    textBox.innerHTML = "";
    displayInput();
}


LeftArrow.onclick = goBack;

insertText.oninput = handleInput;
insertText.onchange = handleChange;

mic.onclick = toggleMic;
backSpace.onclick = backspacing;

translateButton.onclick = submitName;

tryAgainButton.onclick = tryAgain;
eliminateButton.onclick = eliminate;
changeButton.onclick = change;

init();

