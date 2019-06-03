var NotImOkButton = document.getElementById("okButton");
var screen = document.getElementById('screen');

function getMenu() {
    window.history.back();
}

NotImOkButton.onclick = getMenu;
