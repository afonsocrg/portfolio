const MOVE_DELTA = 1;
const ROTATE_BASE_DELTA = 0.05;
const ROTATE_BODY_DELTA = 0.05;
const ROTATE_ARM_DELTA = 0.1;
const ARM_SIN_BACK_LIMIT = 0.9;
const ARM_SIN_FORWARD_LIMIT = -0.70;

class Robot extends Body {
    constructor(x, y, z) {
        super();
        this.lower = createLowerBody(0, 10, 0);
        this.baseJoint = createBaseJoint(0, 0, 0);
        this.upper = createUpperBody(0, 20, 0);

        this.armPivotPoint = createPivotPoint();
        this.armPivotPoint.add(this.upper);
        this.basePivotPoint = createPivotPoint();
        this.basePivotPoint.add(this.armPivotPoint);
        this.baseJoint.add(this.basePivotPoint);
        this.lower.add(this.baseJoint);

        this.mesh = new THREE.Object3D();
        this.mesh.add(this.lower);
        this.mesh.position.set(x, y, z);

        this.rotateBase = false;
    }

    toggleWireframe() {
        this.mesh.traverse((node) => {
            if (node instanceof THREE.Mesh) {
                node.material.wireframe = !node.material.wireframe;
            }
        });
    }

    setRotateBasePositive() {
        this.rotateBasePositive = true;
    }

    setRotateBaseNegative() {
        this.rotateBaseNegative = true;
    }

    unsetRotateBasePositive() {
        this.rotateBasePositive = false;
    }

    unsetRotateBaseNegative() {
        this.rotateBaseNegative = false;
    }

    setRotateArmForward() {
        this.rotateArmForward = true;
    }

    setRotateArmBack() {
        this.rotateArmBack = true;
    }

    unsetRotateArmForward() {
        this.rotateArmForward = false;
    }

    unsetRotateArmBack() {
        this.rotateArmBack = false;
    }

    setMovingForward() {
        this.movingForward = true;
    }

    setMovingBack() {
        this.movingBack = true;
    }

    setMovingLeft() {
        this.movingLeft = true;
    }

    setMovingRight() {
        this.movingRight = true;
    }

    unsetMovingForward() {
        this.movingForward = false;
    }

    unsetMovingBack() {
        this.movingBack = false;
    }

    unsetMovingLeft() {
        this.movingLeft = false;
    }

    unsetMovingRight() {
        this.movingRight = false;
    }

    update() {
        if (this.rotateBasePositive) {
            this.basePivotPoint.rotation.y += ROTATE_BASE_DELTA;
        }
        if (this.rotateBaseNegative){
            this.basePivotPoint.rotation.y -= ROTATE_BASE_DELTA;
        }

        if (this.rotateArmForward) {
            let newRotation = this.armPivotPoint.rotation.x - ROTATE_ARM_DELTA;
            if (Math.sin(newRotation) > ARM_SIN_FORWARD_LIMIT) {
                this.armPivotPoint.rotation.x = newRotation;
            }
        }

        if (this.rotateArmBack) {
            let newRotation = this.armPivotPoint.rotation.x + ROTATE_ARM_DELTA;
            if (Math.sin(newRotation) < ARM_SIN_BACK_LIMIT) {
                this.armPivotPoint.rotation.x = newRotation;
            }
        }
        
        if (this.movingLeft) {
            this.mesh.rotation.y += ROTATE_BODY_DELTA;
        }
        if (this.movingRight) {
            this.mesh.rotation.y -= ROTATE_BODY_DELTA;
        }
        if (this.movingForward) {
            this.mesh.position.z -= MOVE_DELTA*Math.cos(this.mesh.rotation.y);
            this.mesh.position.x -= MOVE_DELTA*Math.sin(this.mesh.rotation.y);
        }
        if (this.movingBack) {
            this.mesh.position.z += MOVE_DELTA*Math.cos(this.mesh.rotation.y);
            this.mesh.position.x += MOVE_DELTA*Math.sin(this.mesh.rotation.y);
        }
    }
}

function createLowerBody(x, y, z) {
    'use strict';

    let lowerBody = new THREE.Object3D();

    lowerBody.add(createBase(0, 0, 0));

    lowerBody.add(createWheel(10, -5, 30));
    lowerBody.add(createWheel(10, -5, -30));
    lowerBody.add(createWheel(-10, -5, 30));
    lowerBody.add(createWheel(-10, -5, -30));

    lowerBody.position.x = x;
    lowerBody.position.y = y;
    lowerBody.position.z = z;

    return lowerBody;
}


function createBase(x, y, z) {
    'use strict';

    let base = new THREE.BoxGeometry(30, 5, 75);
    let material = new THREE.MeshBasicMaterial({color: 0xE44235});
    let mesh = new THREE.Mesh(base, material);

    mesh.position.set(x, y, z);
    return mesh;
}

function createWheel(x, y, z) {
    'use strict';

    let sphere = new THREE.SphereGeometry(5, 20, 20);
    let material = new THREE.MeshBasicMaterial({color: 0xffffff});
    let wheel = new THREE.Mesh(sphere, material);

    wheel.position.set(x, y, z);
    return wheel;
}

function createBaseJoint(x, y, z) {
    'use strict';

    let sphericalCap = new THREE.SphereGeometry(10, 20, 20, 0,
        Math.PI* 2, 0, Math.PI*0.5);

    let material = new THREE.MeshBasicMaterial({color: 0xffffff});

    let baseJoint = new THREE.Mesh(sphericalCap, material);
    baseJoint.position.set(x, y, z);

    return baseJoint;
}

function createUpperBody(x, y, z) {
    'use strict';

    let upperBody = new THREE.Group();

    upperBody.add(createArm(0, 0, 0));
    upperBody.add(createForearm(0, 0, 0));

    upperBody.position.x = x;
    upperBody.position.y = y;
    upperBody.position.z = z;

    return upperBody;
}

function createArm(x, y, z) {
    'use strict';

    let armGeometry = new THREE.BoxGeometry(5, 40, 5);
    let armMaterial = new THREE.MeshBasicMaterial({color: 0x6AB429})

    let arm = new THREE.Mesh(armGeometry, armMaterial);

    arm.position.set(x, y, z);

    return arm;
}

function createForearm(x, y, z) {
    'use strict';

    let jointGeometry = new THREE.SphereGeometry(5, 20, 20);
    let jointMaterial = new THREE.MeshBasicMaterial({color: 0xffffff});

    let joint = new THREE.Mesh(jointGeometry, jointMaterial);
    joint.position.set(x, y + 20, z);

    let forearmGeometry = new THREE.BoxGeometry(5, 5, 30);
    let forearmMaterial = new THREE.MeshBasicMaterial({color: 0x6AB429});

    let forearm = new THREE.Mesh(forearmGeometry, forearmMaterial);
    forearm.position.set(x, y, z - 19);

    joint.add(forearm);

    let wristGeometry = new THREE.SphereGeometry(5, 20, 20);
    let wristMaterial = new THREE.MeshBasicMaterial({color: 0xffffff});

    let wrist = new THREE.Mesh(wristGeometry, wristMaterial);
    wrist.position.set(x, y, z - 15);

    wrist.add(createHand(0,0,0));
    forearm.add(wrist);

    return joint; // TODO: either this or the function needs a better name
}

function createHand(x, y, z) {
    'use strict';

    let hand = new THREE.Group();

    hand.add(createPalm(0, 0, 0));
    hand.add(createFinger(0, -5, 0));
    hand.add(createFinger(0, 5, 0));

    hand.position.set(x, y, z);

    return hand;
}

function createPalm(x, y, z) {
    'use strict';

    let palmGeometry = new THREE.BoxGeometry(15, 15, 3);
    let palmMaterial = new THREE.MeshBasicMaterial({color: 0x007ACC});

    let palm = new THREE.Mesh(palmGeometry, palmMaterial);

    palm.position.set(x, y, z - 4);

    return palm;
}

function createFinger(x, y, z) {
    'use strict';

    let fingerGeometry = new THREE.BoxGeometry(2, 2, 10);
    let fingerMaterial = new THREE.MeshBasicMaterial({color: 0xffffff});

    let finger = new THREE.Mesh(fingerGeometry, fingerMaterial);

    finger.position.set(x, y, z - 10);

    return finger;
}

function createPivotPoint() {
    let pivotPoint = new THREE.Object3D;
    return pivotPoint;
}


