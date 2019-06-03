const waitingMaxTime = 5 
const waitingBaseTime = 5 // minutes

var waitingTime = document.getElementById("waitingTime");
var LeftArrow = document.getElementById("leftArrow");
var cancel = document.getElementById("cancelHelp");

function goBack() {
    document.location.href = "../../index.html"
}

function cancelHelp() {
    localStorage.removeItem("waitingForHelp");
    goBack();
}

function clock(){
    setTimeout('getETA()', 1000);
}

function init() {
    if (localStorage.getItem("waitingForHelp") == null) {
        var eta = Math.floor(Math.random() * 60 * waitingMaxTime) + 60 * waitingBaseTime; // max waiting time: 2 mins
        localStorage.setItem("waitingForHelp", true);
        localStorage.setItem("limitTime", Date.now() + eta*1000);
    } else {
        var limit = localStorage.getItem("limitTime");
        var eta = Math.floor((limit - Date.now())/1000);

        if (eta <= 0) {
            localStorage.removeItem("waitingForHelp");
            document.location.href = "sos.html";
            return 0;
        }
    }
}

function getETA(){
    var limit = localStorage.getItem("limitTime");
    var eta = Math.round((limit - Date.now())/1000);
    if(eta <= 0 && localStorage.getItem("waitingForHelp") == "true") {
        localStorage.removeItem("waitingForHelp");
        document.location.href = "../../index.html"
        return 0;
    }

    waitingTime.innerHTML = Math.floor(eta/60).toString().padStart(2,'0') + ":" + (eta%60).toString().padStart(2, '0');
    clock();
}

init();

LeftArrow.onclick = goBack;
cancel.onclick = cancelHelp;
getETA();
