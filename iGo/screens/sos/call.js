var hangUpButton = document.getElementById("hangUpButton");
var callTime = document.getElementById("callTime_");
var duration = 0;

hangUpButton.onclick = hangUp;

function hangUp() {
    document.location.href = "help.html"
}

function incTime() {
    callTime.innerHTML = Math.floor(duration/60).toString().padStart(2, '0') + ":" +
                        (duration%60).toString().padStart(2, '0');
    duration++;
}

incTime();
setInterval(incTime, 1000);