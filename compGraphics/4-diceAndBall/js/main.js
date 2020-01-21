const SIDE_C = 'sideCamera';
const SIDE_PC = 'sidePerspectiveCamera';
const FRONT_C = 'frontCamera';
const FRONT_PC = 'frontPerspectiveCamera';


let renderer, scene, camera;
let cameras = {};
let originalAspectRatio;
let ball;

let directionalLight;
let pointLight;

let objectList = [] //To change between materials

let fps = 0;


let clock = new THREE.Clock();
let paused = false;



function createCamera() {
    c = new THREE.OrthographicCamera(-150, 150, 170, -40, 1, 250);
    c.position.x = 150;
    c.position.y = 0;
    c.position.z = 0;
    c.lookAt(scene.position)
    cameras[SIDE_C] = c;

    c = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
    c.position.x = 200;
    c.position.y = 200;
    c.position.z = 200;
    c.lookAt(scene.position)
    cameras[SIDE_PC] = c;

    c = new THREE.OrthographicCamera(-200, 200, 220, -40, 1, 3500);
    c.position.x = 0;
    c.position.y = 0;
    c.position.z = -3000;
    c.lookAt(scene.position)
    cameras[FRONT_C] = c;

    c = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
    c.position.x = 0;
    c.position.y = 100;
    c.position.z = -200;
    c.lookAt(scene.position)
    cameras[FRONT_PC] = c;

    originalAspectRatio = window.innerWidth / window.innerHeight;
}

function toggleMotion() {
    paused = !paused;
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



function createLights() {
    // directionalLight = new DirectionalLight(100, 50, -20)    
    directionalLight = new DirectionalLight(100, 200, 0)
    directionalLight.getObject().target = objectList[0].getMesh()
    pointLight = new PointLight();
    scene.add(directionalLight.getObject())
}

function createChessBoard() {
    let chessBoard = new ChessBoard(200, 5, 200);
    chessBoard.setPosition(0, 0, 0);

    scene.add(chessBoard.getMesh());
    objectList.push(chessBoard);
}

function createBall() {
    let radius = 25;
    let distance = 100;
    ball = new Ball(radius, distance);

    scene.add(ball.getMesh());
    objectList.push(ball);
}

function createDice() {
    let size = 25
    let dice = new Dice(size);
    let diagonal = Math.sqrt(Math.pow(size, 2) + Math.pow(size, 2) + Math.pow(size, 2))
    dice.setPosition(0, diagonal / 2 + 2.5, 0)

    scene.add(dice.getMesh());
    objectList.push(dice);
function createPausedImage() {
    let backgroundColor = 0xAC8E8E;
    let geometry = new THREE.BoxGeometry(225, 225, 5);
    let texture = new THREE.TextureLoader().load("../img/pauseMessage.png")
    let material = new THREE.MeshBasicMaterial({color: backgroundColor});
    material.map = texture

    let mesh = new THREE.Mesh(geometry, material);
    mesh.position.set(0, 100, -2900)
    scene.add(mesh);

    // Fill the background
    geometry = new THREE.BoxGeometry(1000,1000,5);
    material = new THREE.MeshBasicMaterial({color: backgroundColor});
    mesh = new THREE.Mesh(geometry, material);
    mesh.position.set(0, 100, -2899);
    scene.add(mesh);
}


function onResize() {
    'use strict';

    let width = window.innerWidth;
    let height = window.innerHeight;
    if (width / height > originalAspectRatio) {
        width = originalAspectRatio * height
    } else {
        height = width / originalAspectRatio;
    }

    renderer.setSize(width, height);

}

function createScene() {
    'use strict';
    scene = new THREE.Scene();
}

// function render() {
//     'use strict';
//     renderer.render(scene, camera);
// }

function init() {
    'use strict';
    renderer = new THREE.WebGLRenderer({
        antialias: true
    });
    renderer.setClearColor(new THREE.Color(0x0));
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);

    createScene();

    let axes = new THREE.AxisHelper(100);
    scene.add(axes);

    createChessBoard();
    createDice();
    createBall();



    createLights();

    createPausedImage();

    createCamera();

    setSidePCamera();


    window.addEventListener("resize", onResize);
    window.addEventListener("keydown", onKeyDown);
    //window.addEventListener("keyup", onKeyUp);


    renderer.render(scene, camera);
}

function reset() {
    objectList.forEach((o => {
        o.reset();
    }))
    toggleMotion()
}


function animate() {
    /* Is it needed?? */

    //Rotate Dice
    //Rotate Ball around dice

    

    let timeDelta = clock.getDelta();
    if(!paused){
        for (let i = 0; i < objectList.length; i++) {
            objectList[i].update(timeDelta);
        }
    }

    if(paused) {
        renderer.autoClear = false
        renderer.clear()
        //renderer.setViewport(0, 0, window.innerWidth/2, window.innerHeight);
        //renderer.render(scene, camera);

        renderer.render(scene, cameras[FRONT_C]);
    } else {
        renderer.autoClear = true
        renderer.render(scene, camera)
    }

    

    requestAnimationFrame(animate);
}

function onKeyDown(e) {
    switch (e.key) {
        case 'd':
            directionalLight.turnOnOff();
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
        case 'p':
            if(paused) break;
            pointLight.toggle()
            break;
        case 'w':
            if(paused) break;
            for (let i = 0; i < objectList.length; i++) {
                objectList[i].showWireFrame();
            }
            break;
        case 'l':
            if(paused) break;
            for (let i = 0; i < objectList.length; i++) {
                objectList[i].startStopCalc();
            }
            break;
        case 'b':
            if(paused) break;
            ball.toggleMovement();
            break;
        case 's':
            toggleMotion() 
            break;
        case 'r':
            if(paused) reset()
            break;

    }
}
