const ROTATE_CANNON_DELTA = 0.5 * Math.PI

class Cannon extends Body {
    constructor(x, y, z) {
        super();
        this.basePivotPoint = createPivotPoint();
        this.cannon = createCannonObj();

        this.basePivotPoint.add(this.cannon);
        this.mesh = new THREE.Object3D();
        this.mesh.add(this.basePivotPoint);
        this.mesh.position.set(x, y, z);
        this.selected = false;
    }

    setRotatingLeft() {
        this.rotateLeft = true;
    }

    setRotatingRight() {
        this.rotateRight = true;
    }

    unsetRotatingLeft() {
        this.rotateLeft = false;
    }

    unsetRotatingRight() {
        this.rotateRight = false;
    }

    toUnselected() {
        this.mesh.traverse((node) => {
            if (node instanceof THREE.Mesh) {
                if(node.material.color.getHex() === 0x505950)
                    node.material.color.setHex(0x616A6B);
                else if(node.material.color.getHex() === 0x5424FE)
                    node.material.color.setHex(0x65350F);
            }
        });
        this.selected = false;
    }

    toSelected() {
        this.mesh.traverse((node) => {
            if (node instanceof THREE.Mesh) {
                if(node.material.color.getHex() === 0x616A6B)
                    node.material.color.setHex(0x505950);
                else if(node.material.color.getHex() === 0x65350F)
                    node.material.color.setHex(0x5424FE);
            }
        });
        this.selected = true;
    }

    update(timeDelta) { 
        if(this.selected && this.rotateRight) {
            this.mesh.rotation.y -= ROTATE_CANNON_DELTA*timeDelta;
        }

        if(this.selected && this.rotateLeft) {
            this.mesh.rotation.y += ROTATE_CANNON_DELTA*timeDelta;
        }
    }

    getMesh() {
        return this.mesh;
    }

    shoot() {
        let magnitude = 100;
        let angle = this.mesh.rotation.y;
        let start_x = this.mesh.position.x - 20*Math.sin(-angle);
        let start_y = this.mesh.position.y;
        let start_z = this.mesh.position.z + 20*Math.cos(angle);
        let velocityVector = new THREE.Vector3(Math.sin(angle), 0, Math.cos(angle));
        velocityVector.multiplyScalar(magnitude);
        return new Cannonball(start_x, start_y, start_z, velocityVector, RADIUS, 0xfffdd0);
    }   
}

function createCannonObj() {
    let cannon = new THREE.Object3D();

    cannon.add(createCannon());
    cannon.add(createWheel(-5, 0, 0));
    cannon.add(createWheel(5, 0, 0));
    cannon.add(createWheel(5, 0, -15));
    cannon.add(createWheel(-5, 0, -15));


    return cannon;
}


function createCannon() {
    let cannonGeometry = new THREE.CylinderGeometry(5, 5, 40, 32);
    let cannonMaterial = new THREE.MeshBasicMaterial({color: 0x616A6B});

    let cannonMesh = new THREE.Mesh(cannonGeometry, cannonMaterial);

    cannonMesh.rotation.x = -0.5*Math.PI;

    cannonMesh.position.set(0, 0 + 5, 0);
    return cannonMesh;
}

function createWheel(x, y, z) {
    'use strict';

    let sphere = new THREE.SphereGeometry(2.5, 20, 20);
    let material = new THREE.MeshBasicMaterial({color: 0x65350F});
    let wheel = new THREE.Mesh(sphere, material);

    wheel.position.set(x, y, z);
    return wheel;
}


function createPivotPoint() {
    let pivotPoint = new THREE.Object3D();
    return pivotPoint;
}
