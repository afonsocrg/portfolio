var LeftArrow = document.getElementById("leftArrow");
var fingerprint = document.getElementById("fingerprint");
var okButton = document.getElementById("okButton");

let goBack = function(){
    window.history.back();
}

let registerFinger = function(){
        setInterval( () => {
        fingerprint.getElementsByClassName("image")[0].src = "../../images/checked.svg"
        okButton.style.display = "block"
        }, 500)
        localStorage.setItem('fingerprint', "true")
}

let returnAfterRegister = function(){
    document.location.href = "setup.html";
}

LeftArrow.onclick = goBack;
fingerprint.onmousedown = registerFinger;
okButton.onclick = returnAfterRegister;
