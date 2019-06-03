var left = document.getElementById('left-slider');
var right = document.getElementById('right-slider');
var container = document.getElementsByClassName('slider-container')[0];
var sliderSpace = document.getElementsByClassName('slider-space')[0];

var leftValue = document.getElementById('left-value');
var rightValue = document.getElementById('right-value');

var leftMoving = false;
var rightMoving = false;

var baseValue = 0;
var topValue = 100;

const sliderName = localStorage.getItem("slider-name");

var LeftArrow = document.getElementById("leftArrow");


function init() {
    var minValue = parseInt(localStorage.getItem(sliderName + "-min"));
    var maxValue = parseInt(localStorage.getItem(sliderName + "-max"));

    var baseValue_ = parseInt(localStorage.getItem(sliderName + "-base"));
    var topValue_ = parseInt(localStorage.getItem(sliderName + "-top"));

    if (!(isNaN(baseValue_) || isNaN(topValue_))) {
        baseValue = baseValue_;
        topValue = topValue_;
    }

    if (isNaN(minValue) || isNaN(maxValue)) {
        sliderSet(baseValue, topValue);
    } else {
        sliderSet(minValue, maxValue);
    }

    updateValues();
    updateSpace();
}

function sliderSelected(event) {
    var id = event.target.id;

    if (id == 'left-slider') {
        leftMoving = true;
    } else {
        rightMoving = true;
    }
    event.target.style.backgroundColor = 'orange';
}

function sliderUnselected(event) {
    leftMoving = false;
    rightMoving = false;

    left.style.backgroundColor = 'grey';
    right.style.backgroundColor = 'grey';
}

function sliderMove(event) {
    var id = event.target.id;

    var slider;

    if (leftMoving) {
        slider = left;
    } else if (rightMoving) {
        slider = right;
    } else {
        return; 
    }

    var containerLeft = container.getBoundingClientRect().left;
    var containerWidth = container.clientWidth;
    var sliderWidth = slider.clientWidth;
    var mousePos = event.clientX;
    var position = mousePos - containerLeft - sliderWidth/2;

    var leftPos = left.getBoundingClientRect().left - containerLeft;
    var rightPos = right.getBoundingClientRect().left - containerLeft;

    if (leftMoving && position + sliderWidth > rightPos) {
        position = rightPos - sliderWidth;
    } else if (rightMoving && position < leftPos + sliderWidth) {
        position = leftPos + sliderWidth;
    } else if (position < 0) { // limit left
        position = 0;
    } else if (position > containerWidth - sliderWidth) { // limit right
        position = containerWidth - sliderWidth;
    } 

    slider.style.left = position + 'px';

    updateValues();
    updateSpace();
}

function updateValues() {
    var vals = sliderGet();

    localStorage.setItem(sliderName + '-min', vals[0]);
    localStorage.setItem(sliderName + '-max', vals[1]);

    leftValue.innerHTML = vals[0];
    rightValue.innerHTML = vals[1];
}

function sliderGet() {
    var containerStart = container.getBoundingClientRect().left;
    var sliderTotal = left.clientWidth;
    var containerTotal = container.clientWidth - sliderTotal;
    var leftStart = left.getBoundingClientRect().left;
    var rightStart = right.getBoundingClientRect().left;

    var leftDelta = leftStart - containerStart;
    var rightDelta = rightStart - containerStart;

    return [baseValue + Math.round(leftDelta/containerTotal*(topValue-baseValue)),
            baseValue + Math.round(rightDelta/containerTotal*(topValue-baseValue))]
}

function sliderSet(min, max) {
    var containerWidth = container.clientWidth;
    var sliderWidth = left.clientWidth;
    var containerTotal = containerWidth - sliderWidth;

    var leftDelta = (min-baseValue)/(topValue-baseValue)*containerTotal
    var rightDelta = (max-baseValue)/(topValue-baseValue)*containerTotal

    left.style.left = leftDelta + 'px';
    right.style.left = rightDelta + 'px';
}

function updateSpace() {
    var containerStart = container.getBoundingClientRect().left;
    var sliderWidth = left.clientWidth;
    var leftEnd = left.getBoundingClientRect().left + sliderWidth;
    var rightStart = right.getBoundingClientRect().left;

    sliderSpace.style.left = leftEnd - containerStart - sliderWidth/2 + 'px';
    sliderSpace.style.width = rightStart - leftEnd + sliderWidth/2 + 'px';
}

let goBack = function(){
    window.location.href = "setup.html"
}

left.onmousedown = sliderSelected;
left.onmousemove = sliderMove;
right.onmousedown = sliderSelected;
right.onmousemove = sliderMove;

document.onmouseup = sliderUnselected;
document.onmousemove = sliderMove;

LeftArrow.onclick = goBack

window.onload = init
