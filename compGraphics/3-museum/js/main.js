const SIDE_C = 'sideCamera';
const SIDE_PC = 'sidePerspectiveCamera';
const FRONT_C = 'frontCamera';
const FRONT_PC = 'frontPerspectiveCamera';


let renderer, scene, camera;
let cameras = {};

let directionalLight;

let objectList = [] //To change between materials
let spotlights = [4] //Spotlight list

let fps = 0;


let clock = new THREE.Clock();


function createCamera() {
    c = new THREE.OrthographicCamera(-150, 150, 170, -40, 1, 250);
    c.position.x = 150;
    c.position.y = 0;
    c.position.z = 0;
    c.lookAt(scene.position)
    cameras[SIDE_C] = c;

    c = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
    c.position.x = 230;
    c.position.y = 150;
    c.position.z = 0;
    c.lookAt(scene.position)
    cameras[SIDE_PC] = c;

    c = new THREE.OrthographicCamera(-120, 120, 130, -30, 1, 500);
    c.position.x = 0;
    c.position.y = 0;
    c.position.z = -200;
    c.lookAt(scene.position)
    cameras[FRONT_C] = c;

    c = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
    c.position.x = 0;
    c.position.y = 100;
    c.position.z = -200;
    c.lookAt(scene.position)
    cameras[FRONT_PC] = c;
}


function setSideCamera() {
    'use strict';
    camera = cameras[SIDE_C];
}

function setSidePCamera() {
    'use strict';
    camera = cameras[SIDE_PC];
}

function setFrontCamera() {
    'use strict';
    camera = cameras[FRONT_C];
}

function setFrontPCamera() {
    'use strict';
    camera = cameras[FRONT_PC];
}


function CreateFloor() {
    //The floor is just a wall with high width and low height
    let floor = new Wall(150, 10, 300, 0x9b1a10);

    floor.setPosition(60,-5,0);
    scene.add(floor.getMesh());

    objectList.push(floor);
}

function CreateWall() {
    let wall = new Wall(10, 150, 300, 0xd5c8c0);
    wall.setPosition(-10, 75, 0);
    scene.add(wall.getMesh());

    objectList.push(wall);
}

function CreatePainting() {
    let painting = new Painting(-5, 75, -20);

    scene.add(painting.getMesh());
    objectList.push(painting)
}

var icosahedron


function CreateSculpture() {
    let pedestal = new Pedestal();
    icosahedron = new Icosahedron();

    scene.add(pedestal.getMesh());
    scene.add(icosahedron.getMesh());

    objectList.push(pedestal);
    objectList.push(icosahedron);
}

function CreateLights() {
    // directionalLight = new DirectionalLight(100, 50, -20)    
    directionalLight = new DirectionalLight(50, 100, 0)    
    scene.add(directionalLight.getObject())
}

function CreateSpotLights() {
    spotlights[0] = new SpotLight(75, 120, 70, objectList[1].getMesh());
    spotlights[1] = new SpotLight(75, 120, 30, objectList[1].getMesh());
    spotlights[2] = new SpotLight(75, 120, -10, objectList[2].getMesh());
    spotlights[3] = new SpotLight(75, 120, -50, objectList[2].getMesh());
}

function onResize() {
    'use strict';

    renderer.setSize(window.innerWidth, window.innerHeight);

    if (window.innerHeight > 0 && window.innerWidth > 0) {
        camera.aspect = window.innerWidth / window.innerHeight;
        camera.updateProjectionMatrix();
    }
}

function createScene() {
    'use strict';
    scene = new THREE.Scene();
}

function render() {
    'use strict';
    renderer.render(scene, camera);
}

function init() {
    'use strict';
    renderer = new THREE.WebGLRenderer({
        antialias: true
    });
    renderer.setClearColor(new THREE.Color(0x0));
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);
    
    createScene();

    let axes = new THREE.AxisHelper(50);
    scene.add(axes);

    CreateFloor()
    CreateWall()
    CreatePainting()
    CreateSculpture()

    CreateLights()
    CreateSpotLights()

    createCamera();

    setSidePCamera();


    window.addEventListener("resize", onResize);
    window.addEventListener("keydown", onKeyDown);
    //window.addEventListener("keyup", onKeyUp);

    render();
}


function animate() {
    /* Is it needed?? */


    let timeDelta = clock.getDelta();
    icosahedron.update(timeDelta)

    render();

    // show framerate
    let newfps = Math.floor(1/timeDelta);
    if(Math.abs(fps-newfps) > 5) fps = newfps;
    document.getElementById("fps").innerHTML = Math.floor(fps) + " FPS";

    requestAnimationFrame(animate);
}

function onKeyDown(e) {
    switch (e.key) {
        case 'q':
            directionalLight.turnOnOff();
            break;
        case 'w':
            for (let i = 0; i < objectList.length; i++) {
                objectList[i].startStopCalc();
            }
            break;
        case 'e':
            for(let i = 0; i < objectList.length; i++) {
                objectList[i].toLambertPhong();
            }
            break;
        case ' ':
            icosahedron.toggleGregate();
            break;
        case '1':
            spotlights[0].update()
            break;
        case '2':
            spotlights[1].update()
            break;
        case '3':
            spotlights[2].update()   
            break;
        case '4':
            spotlights[3].update()
            break;
        case '5':
            setSidePCamera()
            break;
        case '6':
            setSideCamera()
            break;
        case '7':
            setFrontPCamera()
            break;
        case '8':
            setFrontCamera()
            break;
        
    }
}
