const TOP_C = 'topCamera';
const TOP_PC = 'topPerspectiveCamera';
const SIDE_C = 'sideCamera';
const SIDE_PC = 'sidePerspectiveCamera';
const FRONT_C = 'frontCamera';
const FRONT_PC = 'frontPerspectiveCamera';

const WALL_LEFT = 'leftWall';
const WALL_FRONT = 'frontWall';
const WALL_RIGHT = 'rightWall';

const RADIUS = 5

const AXIS = false;
const snooker = true;

 
let renderer, scene, camera;
let cameras = {};

let cannons = [3]
let balls = [];
let selectedCannon;
let selectedBall;
let collisionHandler = new CollisionHandler();

let colors = [
    0xFDBC02,
    0x62120B,
    0xFB2A16,
    0xFDBC02,
    0x15100D,
    0x291E63,
    0x131F85,
    0x131F85,
    0x1D4928,
    0xFF7F23,
    0xFB2A16,
    0x291E63,
    0xFF7F23,
    0x1D4928,
    0x62120
];


let showAxis = 0;

let walls = {};

let clock = new THREE.Clock();

let spacedown = false;

let fps = 0;

/* If currently processed ball is beyond world limits */
let isBallOffLimits = 0;

function createCamera() {
    'use scrict';
    let c;

    // add top camera
    c = new THREE.OrthographicCamera(-200, 200, 80, -80, 1, 250);
    c.position.x = 0;
    c.position.y = 100;
    c.position.z = 0;
    c.lookAt(scene.position)
    cameras[TOP_C] = c;
    cameras[TOP_C].rotation.z = 0.5*Math.PI


    c = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
    

    console.log(selectedBall)
    console.log(balls)

    c.position.x = selectedBall.mesh.position.x;
    c.position.y = selectedBall.mesh.position.y + 10;
    c.position.z = selectedBall.mesh.position.z - 10;

    c.lookAt(selectedBall.mesh.position)
    cameras[TOP_PC] = c;


    // add Side Camera
    c = new THREE.OrthographicCamera(-120, 120, 130, -30, 1, 250);
    c.position.x = -150;
    c.position.y = 0;
    c.position.z = 0;
    c.lookAt(scene.position)
    cameras[SIDE_C] = c;

    c = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 1, 1000);
    c.position.x = 200;
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
    c.position.y = 100;
    c.position.z = -200;
    c.lookAt(scene.position)
    cameras[FRONT_PC] = c;
    // var helper = new THREE.CameraHelper(c);
    // scene.add(helper);

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
    
    if(AXIS) {
        let axes = new THREE.AxisHelper(50);
        scene.add(axes);
    }

    createFloor();
    createWalls();

    cannons[0] = new Cannon(-30, 0, -50);
    scene.add(cannons[0].mesh);
    cannons[1] = new Cannon(0, 0, -50);
    scene.add(cannons[1].mesh);
    cannons[2] = new Cannon(30, 0, -50);
    scene.add(cannons[2].mesh);

    selectCannon(0);

    //Create random balls

    // snooker
    if(snooker) {
        for(let i = 0; i < 15; i++) {
            let x = Math.random()*(37*2) - 37
            let y = 0
            let z = Math.random()*(85) + 50
            let velocity = new THREE.Vector3(0,0,0);
            let b = new Cannonball(x, y, z, velocity, RADIUS, colors[i]);
            
            balls.push(b);
            scene.add(b.mesh);
        }
    } else {
        for(let i = 0; i < 100; i++) {

            if (Math.random() <= 0.1) {

                let x = Math.random()*(37*2) - 37
                let y = 0
                let z = Math.random()*(85) + 50
                let velocity = new THREE.Vector3(0,0,0);
                let b = new Cannonball(x, y, z, velocity, RADIUS);
                
                balls.push(b);
                scene.add(b.mesh);
            }

        }
    }


    selectedBall = balls[Math.ceil(Math.random()*balls.length) - 1];


    createCamera();
    setTopCamera();

    window.addEventListener("resize", onResize);
    window.addEventListener("keydown", onKeyDown);
    window.addEventListener("keyup", onKeyUp);

    render();
}

function createFloor() {
    let planeGeometry = new THREE.BoxGeometry(100, 300, 6);
    let planeMaterial = new THREE.MeshBasicMaterial({color: 0x00760D});
    
    let plane = new THREE.Mesh(planeGeometry, planeMaterial);
    
    plane.rotation.x = -0.5 * Math.PI + Math.PI;
    plane.position.x = 0;
    plane.position.y = -3;
    plane.position.z = 0;

    scene.add(plane);
}

function createWalls() {
    let leftWall = new Wall(7, 50, 280);
    leftWall.setPosition(46.5, 25, 0);

    let mesh = leftWall.getMesh();
    walls[WALL_LEFT] = {
        'mesh': mesh,
        'limit_x': mesh.position.x - 7/2,
        'limit_z_front': mesh.position.z + 280/2,
        'limit_z_back': mesh.position.z - 280/2
    }
    scene.add(mesh);


    let topWall = new Wall(100, 50, 10);
    topWall.setPosition(0, 25, 145);

    mesh = topWall.getMesh();
    walls[WALL_FRONT] = {
        'mesh': mesh,
        'limit_z': mesh.position.z - 10/2
    }
    scene.add(mesh);


    let rightWall = new Wall(7, 50, 280);
    rightWall.setPosition(-46.5, 25, 0);

    mesh = rightWall.getMesh();
    walls[WALL_RIGHT] = {
        'mesh': mesh,
        'limit_x': mesh.position.x + 7/2,
        'limit_z_front': mesh.position.z + 280/2,
        'limit_z_back': mesh.position.z - 280/2
    }
    scene.add(mesh);
}

function animate() {
    'use strict';

    let timeDelta = clock.getDelta();

    // update
    cannons.forEach(c => c.update(timeDelta));
    
    for(let i = 0; i < balls.length; i ++) {
        balls[i].update(timeDelta);
        if(isBallOffLimits) {
            if(balls[i] === selectedBall) {
                selectedBall = balls[0];
            }
            balls[i].remove();
            
            scene.remove(balls[i].mesh)
            balls.splice(i,1);
            
            
            isBallOffLimits = 0
        }
    }

    let lastVecDirection = selectedBall.velocityDirection.clone();

    for (let i = 0; i < balls.length; i++) {
        for (let j = i+1; j < balls.length; j++) {
            collisionHandler.handleCollision(balls[i], balls[j]);
        }
    }
    


   
    cameras[TOP_PC].position.x = selectedBall.mesh.position.x - 10*selectedBall.velocityDirection.getComponent(0);
    cameras[TOP_PC].position.y = selectedBall.mesh.position.y + 10;
    cameras[TOP_PC].position.z = selectedBall.mesh.position.z - 10*selectedBall.velocityDirection.getComponent(2);
    
    

    cameras[TOP_PC].lookAt(selectedBall.mesh.position)
    

    // display
    render();

    // show framerate
    let newfps = Math.floor(1/timeDelta);
    if(Math.abs(fps-newfps) > 5) fps = newfps;
    document.getElementById("fps").innerHTML = Math.floor(fps) + " FPS";

    requestAnimationFrame(animate);
}

function onKeyDown(e) {
    switch (e.key) {
        case '1':
            setTopCamera()
            break;
        case '2':
            setFrontPCamera();
            
            break;
        case '3':
            setTopPCamera();
            break;
        case '8':
            setFrontCamera()
            break;
        case '9':
            camera = cameras[SIDE_C];
            break;
        case '0':
            setSidePCamera();
            break;
        case 'ArrowLeft':
            cannons[0].setRotatingLeft();
            cannons[1].setRotatingLeft();
            cannons[2].setRotatingLeft();
            break;
        case 'ArrowRight':
            cannons[0].setRotatingRight();
            cannons[1].setRotatingRight();
            cannons[2].setRotatingRight();
            break;
        case 'q':
            selectCannon(0);
            break;
        case 'w':
            selectCannon(1);
            break;
        case 'e':
            selectCannon(2);
            break;
        case ' ':
            if(spacedown) break;
            spacedown = true;
            console.log("BANG!");
            let b = selectedCannon.shoot();
            console.log(b);
            balls.push(b);
            scene.add(b.mesh);
            selectedBall = balls[balls.length - 1]
            break;
        case 'r':
            showAxis = !showAxis;
            balls.forEach(b => b.scale(showAxis));
    }
}

function selectCannon(index) {
    cannons.forEach((c) => c.toUnselected());
    if(index < cannons.length) {
        cannons[index].toSelected();
        selectedCannon = cannons[index];
    }
}

function onKeyUp(e) {
    switch (e.key) {
        case 'ArrowLeft':
            cannons[0].unsetRotatingLeft();
            cannons[1].unsetRotatingLeft();
            cannons[2].unsetRotatingLeft();
            break;
        case 'ArrowRight':
            cannons[0].unsetRotatingRight();
            cannons[1].unsetRotatingRight();
            cannons[2].unsetRotatingRight();
            break;
        case ' ':
            spacedown = false;
            break;
    }
}
