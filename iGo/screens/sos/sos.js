const green = "#00b503";

const fileImgPath = "../../images/file.svg";
const fileChkImgPath = "../../images/file_checked.svg";

const gpsImgPath = "../../images/location.svg";
const gpsChkImgPath = "../../images/location_checked.svg";

const key_loc = "willSendLocation";
const key_file = "willSendFile";


var sendLocation = document.getElementById("location");
var sendFile = document.getElementById("file");
var callButton = document.getElementById("callButton");
var LeftArrow = document.getElementById("leftArrow");

var divFileIMG = document.getElementById("file_img");
var divGpsIMG = document.getElementById("location_img");

var fileIMG = document.getElementById("docIMG");
var gpsIMG = document.getElementById("gpsIMG");

var willSendLocation = localStorage.getItem(key_loc);
var willSendFile = localStorage.getItem(key_file);


function update() {
    if (willSendLocation === 'true'){
        gpsIMG.src = gpsChkImgPath;
    }  else {
        gpsIMG.src = gpsImgPath;
    }  
    if (willSendFile === 'true'){
        fileIMG.src = fileChkImgPath;
    } else {
        fileIMG.src = fileImgPath;
    }
}

function send(event) {
    if(event.target.className === 'gps'){
        if (willSendLocation === 'true'){
            willSendLocation = 'false';
        } else {
            willSendLocation = 'true';
        }
    } else if (event.target.className === 'ficha'){
        if(willSendFile === 'true'){
            willSendFile = 'false';
        } else {
            willSendFile = 'true';
        }

    }
    update();
}

function save() {
    localStorage.setItem(key_loc, willSendLocation);
    localStorage.setItem(key_file, willSendFile);
}

function call() {
    save();
    document.location.href = "calling.html";
}

function goBack() {
    save();
    document.location.href = "../../index.html";
}

update();

sendLocation.onclick = send;
sendFile.onclick = send;
LeftArrow.onclick = goBack;
callButton.onclick = call;
