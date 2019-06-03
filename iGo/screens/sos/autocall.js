var LeftArrow = document.getElementById("leftArrow");

var situation = document.getElementById("warningMessage");
var cancelButton = document.getElementById("falseAlarmButton")


let init = function(){
    var warning = Math.random();
    if (warning <= 0.16){
        situation.innerHTML = "\<b>A sua tensão está acima do normal!\</b>\
      \<span>\</br>A chamar SOS!\</span>"
    } else if (warning <= 0.33){
        situation.innerHTML = "\<b>A sua tensão está abaixo do normal!\</b>\
        \<span>\</br>A chamar SOS!\</span>"
    } else if (warning <= 0.5){
        situation.innerHTML = "\<b>Os seus níveis de hidratação estão baixos!\</b>\
        \<span>\</br>A chamar SOS!\</span>"
    } else if (warning <= 0.66){
        situation.innerHTML = "\<b>Está a apresentar sintomas de enfarte!\</b>\
        \<span>\</br>A chamar SOS!\</span>"
    } else if (warning <= 0.83){
        situation.innerHTML = "\<b>Está a apresentar sintomas de ataqiue cardíaco!\</b>\
        \<span>\</br>A chamar SOS!\</span>"
    } else{
        situation.innerHTML = "\<b>Está a apresentar níveis de stress elevados!\</b>\
        \<span>\</br>A chamar SOS!\</span>"
    }

}

let cancel = function(){
    window.history.back(); //is this ok?
}

let goBack = function(){
    window.history.back();
}


LeftArrow.onclick = goBack;
cancelButton.onclick = cancel;
init();

setTimeout(() => {
    document.location.href = "help.html"
}, 4000)


