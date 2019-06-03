var dialTime = 3; // time in seconds

var callInfo = document.getElementById("callInfo");

var sendLocation = localStorage.getItem("willSendLocation");
var sendFile = localStorage.getItem("willSendFile");

var options = document.getElementById("Options")

var cancelButton = document.getElementById("cancelButton");
var LeftArrow = document.getElementById("leftArrow");

var locationDiv = '\
<div id="location" class="gps">\
  <div id="location_img" class="gps">\
    <img id="gpsIMG" src="../../images/location.svg" class="gps">\
  </div>\
  <b>Enviada!</b>\
</div>'

var fileDiv = '\
<div id="file" class="ficha">\
  <div id="file_img" class="ficha">\
    <img id="docIMG" src="../../images/file.svg" class="ficha">\
  </div>\
  <b>Enviada!</b>\
</div> '

function returnToSOS(){
  document.location.href = "sos.html"
}

cancelButton.onclick = returnToSOS;
LeftArrow.onclick = returnToSOS;

if(sendFile == 'true') {
    options.insertAdjacentHTML('beforeend', fileDiv)
} else {
    if(document.getElementById("file"))
      options.removeChild(document.getElementById("file"))
}

if(sendLocation == 'true') {
    options.insertAdjacentHTML('beforeend', locationDiv);
} else {
    if(document.getElementById("location"))
        options.removeChild(document.getElementById("location"))
}


setTimeout(() => {
      document.location.href = "call.html";
  }, dialTime * 1000);
