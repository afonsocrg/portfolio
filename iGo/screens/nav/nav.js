var LeftArrow = document.getElementById('leftArrow');
var map = document.getElementById('map');
var screen = document.getElementById('screen');
var buttonZoomIn = document.getElementById('zoom-in');
var buttonZoomOut = document.getElementById('zoom-out');
var target = document.getElementById('target');
var clear = document.getElementById('clearPath');

var scrollX = 0;
var scrollY = 0;

var map_first_load = true;

const MAPDELTA = 50;
const PINSIZE = 0.1;
const BUSTINYSIZE = 0.05;
const TRANSPORT_TYPES = ['foot', 'bus', 'car'];

var mapMoving = false;

var pins;

if(localStorage.getItem('pinsToShow') === null){
    localStorage.setItem('pinsToShow', JSON.stringify(pinsToShow))
    pins = pinsToShow
} else {
    pins = JSON.parse(localStorage.getItem('pinsToShow'))
}

function goBack() {
    document.location.href = '../../index.html';
}

function clearAllPaths() {
    for (var i = 0; i < localStorage.length; i++) {
        var name = localStorage.key(i);
        if (name.endsWith('_path')) {
            localStorage.removeItem(name);
        }
    }
    map.src = "../../images/map.svg";
    clear.style.display = 'none'

}

function parsePixels(pixels) {
    return Number(pixels.slice(0, -2));
}

function initMap() {

    var mapHeight = Number(localStorage.getItem('map-height'));
    scrollX = Number(localStorage.getItem('map-scroll-x'));
    scrollY = Number(localStorage.getItem('map-scroll-y'));

    if (!isNaN(mapHeight) && mapHeight > 0) {
        map.setAttribute('height', mapHeight);
    } else {
        map.setAttribute('height', 600); // reasonable default
    }

    if (scrollX == NaN || scrollY == NaN) {
        scrollX = 0;
        scrollY = 0;
    }
    setScroll();

    pins.forEach((pin) => {
        var id = pin.id;
        var type = pin.type;
        var x = pin.x;
        var y = pin.y;

        console.log(pin.name)

        if (type === 'self' || localStorage.getItem(type + '_visible') === 'true') {
            var img = new Image();
            img.src = pinImg(type);
            img.style.left = map.width * x + 'px';
            img.style.top = map.height * y + 'px';

            screen.prepend(img);

            TRANSPORT_TYPES.forEach((type) => {
                if (localStorage.getItem(id + '_' + type + '_path') === 'true') {
                    map.src = '../../images/' + id + '_' + type + '_path.svg';
                    if (type === 'bus') {
                        var busImg = new Image();
                        busImg.src = '../../images/bus_tiny.svg';
                        busImg.style.left = map.width * pin.bus.x + 'px';
                        busImg.style.top = map.height * pin.bus.y + 'px';
                        busImg.setAttribute('class', 'pin');
                        busImg.setAttribute('height', map.height * BUSTINYSIZE);
                        busImg.onclick = () => {
                            localStorage.setItem('Pin', 'Paragem de autocarro');
                            localStorage.setItem('type', "transport"+'s');
                            localStorage.setItem('returnScreen', '../nav/nav.html');
                            document.location.href = 'pinInfo.html'
                        }

                        screen.prepend(busImg);
                        pin.bus.img = busImg;
                    }
                }
            })

            img.setAttribute('height', map.height * PINSIZE);
            img.setAttribute('class', 'pin');
            img.onclick = () => {
                if(type != 'self'){
                    localStorage.setItem('Pin', pin.name);
                    localStorage.setItem('type', type+'s');
                    localStorage.setItem('returnScreen', '../nav/nav.html');
                    document.location.href = 'pinInfo.html'
                }
            }
            img.ondragstart = () => { return false; };
            pin.img = img;
        }
    });

    updatePins();

    console.log(map.src)
    if (map.src.endsWith('images/map.svg')) {
        console.log('cenas')
        clear.style.display = 'none';
    }
}

function pinImg(type) {
    types = {
        'self': 'location.svg',
        'friend': 'Friend_Pin.svg',
        'restaurant': 'Restaurant_Pin.svg',
        'hotel': 'Hotel_Pin.svg',
        'museum': 'Museum_Pin.svg',
        'transport': 'Transport_Pin.svg',
    }

    return '../../images/' + types[type];
}

function setScroll() {
    screen.scrollTo(scrollX, scrollY);
    updateVals();
}

function updateVals() {
    scrollX = screen.scrollLeft;
    scrollY = screen.scrollTop;
    localStorage.setItem('map-scroll-x', scrollX);
    localStorage.setItem('map-scroll-y', scrollY);
    localStorage.setItem('map-height', map.height);
}

function updatePins() {
    pins.forEach((pin) => {
        if (pin.type === 'self' || localStorage.getItem(pin.type + '_visible') === 'true') {
            pin.img.style.top = map.height * pin.y + 'px';
            pin.img.style.left = map.width * pin.x + 'px';
            pin.img.setAttribute('height', map.height * PINSIZE);

            if (pin.bus !== undefined && pin.bus.img !== undefined) {
                pin.bus.img.style.top = map.height * pin.bus.y + 'px';
                pin.bus.img.style.left = map.width * pin.bus.x + 'px';
                pin.bus.img.setAttribute('height', map.height * BUSTINYSIZE);
            }
        }
    });
}

function moveMap(e) {
    if (mapMoving) {
        screen.scrollBy(-e.movementX, -e.movementY);
        updateVals();
    }
}

function zoomIn() {
    var height = map.height;
    var scrollXRatio = screen.scrollLeft / map.width;
    var scrollYRatio = screen.scrollTop / map.height;

    height += MAPDELTA;

    map.setAttribute('height', height);

    screen.scrollTo(map.width * scrollXRatio, map.height * scrollYRatio);
    updatePins();
    updateVals();
}

function zoomOut() {
    var height = map.height;
    var scrollXRatio = screen.scrollLeft / map.width;
    var scrollYRatio = screen.scrollTop / map.height;

    height -= MAPDELTA;

    if (height < screen.clientHeight) {
        return;
    }

    map.setAttribute('height', height);

    screen.scrollTo(map.width * scrollXRatio, map.height * scrollYRatio);
    updatePins();
    updateVals();
}

LeftArrow.onclick = goBack;

map.onload = () => {
    if (map_first_load) {
        map_first_load = false;
    } else {
        return;
    }
    initMap();
    screen.addEventListener('scroll', () => {
        // disable scrolling the map manually
        setScroll();
    });
};
map.ondragstart = () => {
    return false;
};
screen.addEventListener('mousedown', () => {
    mapMoving = true;
});
document.addEventListener('mouseup', () => {
    mapMoving = false;
});
document.addEventListener('mousemove', moveMap);

buttonZoomIn.onclick = zoomIn;
buttonZoomOut.onclick = zoomOut;
target.onclick = () => { document.location.href = "filters.html"; }
clear.onclick = clearAllPaths;
