const TOP_C = 'topCamera';
const TOP_PC = 'topPerspectiveCamera';
const SIDE_C = 'sideCamera';
const SIDE_PC = 'sidePerspectiveCamera';
const FRONT_C = 'frontCamera';
const FRONT_PC = 'frontPerspectiveCamera';

let cameras = {};
let camera, scene, renderer;
let robot, target;

let lastCalledTime = Date.now();
let fps = 0;

function createCamera() {
    'use scrict';
    let c;

    // add top camera
    c = new THREE.OrthographicCamera(-120, 120, 120, -90, 1, 250);
    c.position.x = 0;
    c.position.y = 100;
    c.position.z = 0;
    c.lookAt(scene.position)
    cameras[TOP_C] = c;

    c = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
    c.position.x = 0;
    c.position.y = 200;
    c.position.z = 80;
    c.lookAt(scene.position)
    cameras[TOP_PC] = c;


    // add Side Camera
    c = new THREE.OrthographicCamera(-120, 120, 130, -30, 1, 250);
    c.position.x = -150;
    c.position.y = 0;
    c.position.z = 0;
    c.lookAt(scene.position)
    cameras[SIDE_C] = c;

    c = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
    c.position.x = 100;
    c.position.y = 80;
    c.position.z = 0;
    c.lookAt(scene.position)
    cameras[SIDE_PC] = c;

    // add Front camera
    c = new THREE.OrthographicCamera(-120, 120, 130, -30, 1, 500);
    c.position.x = 0;
    c.position.y = 0;
    c.position.z = -200;
    c.lookAt(scene.position)
    cameras[FRONT_C] = c;

    c = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
    c.position.x = 0;
    c.position.y = 50;
    c.position.z = 100;
    c.lookAt(scene.position)
    cameras[FRONT_PC] = c;
    // var helper = new THREE.CameraHelper(c);
    // scene.add(helper);


    setFrontCamera();

}

function setTopCamera() {
    'use strict';
    camera = cameras[TOP_C];
}

function setTopPCamera() {
    'use strict';
    camera = cameras[TOP_PC];
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
    renderer.setClearColor(new THREE.Color(0xEEEEEE));
    renderer.setSize(window.innerWidth, window.innerHeight);
    document.body.appendChild(renderer.domElement);
    
    createScene();
    createCamera();
    
    let axes = new THREE.AxisHelper(50);
    scene.add(axes);

    createFloor();

    robot = new Robot(0,0,0);
    scene.add(robot.mesh);

    target = new Target(0, 20, -100);
    scene.add(target.mesh);

    window.addEventListener("resize", onResize);

    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("keyup", onKeyUp);

    render();
}

function createFloor() {
    let planeGeometry = new THREE.BoxGeometry(100, 250, 6);
    let planeMaterial = new THREE.MeshBasicMaterial({color: 0x000000});
    
    let plane = new THREE.Mesh(planeGeometry, planeMaterial);
    
    plane.rotation.x = -0.5 * Math.PI + Math.PI;
    plane.position.x = 0;
    plane.position.y = -3;
    plane.position.z = 0;

    scene.add(plane);
}

function animate() {
    'use strict';

    // update values here
    robot.update();
    target.update();
    render();

    let delta = (Date.now() - lastCalledTime)/1000;
    lastCalledTime = Date.now();
    let newfps = Math.floor(1/delta);
    if(Math.abs(fps-newfps) > 5) fps = newfps;

    document.getElementById("fps").innerHTML = Math.floor(fps) + " FPS";

    requestAnimationFrame(animate);
}

function onKeyDown(e) {
    switch (e.key) {
        case '1':
            camera = cameras[TOP_C];
            break;
        case '2':
            camera = cameras[SIDE_C];
            break;
        case '3':
            camera = cameras[FRONT_C];
            break;
        case '4':
            target.toggleWireframe();
            robot.toggleWireframe();
            break;
        case '8':
            setTopPCamera();
            break;
        case '9':
            setSidePCamera();
            break;
        case '0':
            setFrontPCamera();
            break;
        case 'a':
            robot.setRotateBasePositive();
            break;
        case 's':
            robot.setRotateBaseNegative();
            break;
        case 'q':
            robot.setRotateArmForward();
            break;
        case 'w':
            robot.setRotateArmBack();
            break;
        case 'ArrowUp':
            robot.setMovingForward();
            break;
        case 'ArrowDown':
            robot.setMovingBack();
            break;
        case 'ArrowLeft':
            robot.setMovingLeft();
            break;
        case 'ArrowRight':
            robot.setMovingRight();
            break;
    }
}

function onKeyUp(e) {
    switch (e.key) {
        case 'a':
            robot.unsetRotateBasePositive();
            break;
        case 's':
            robot.unsetRotateBaseNegative();
            break;
        case 'q':
            robot.unsetRotateArmForward();
            break;
        case 'w':
            robot.unsetRotateArmBack();
            break;
        case 'ArrowUp':
            robot.unsetMovingForward();
            break;
        case 'ArrowDown':
            robot.unsetMovingBack();
            break;
        case 'ArrowLeft':
            robot.unsetMovingLeft();
            break;
        case 'ArrowRight':
            robot.unsetMovingRight();
            break;
    }
}
