const MAX_VELOCITY = 3;
const ACCEL = 1;
const ROTATION = 3;

class Ball extends Body {
    constructor(radius, distance) {
        super();

        this.texture = new THREE.TextureLoader().load("../img/monalisa.jpg");

        this.geometry = new THREE.SphereGeometry(radius, 30, 30);
        this.basicMaterial = new THREE.MeshBasicMaterial();
        this.basicMaterial.map = this.texture;

        this.phongMaterial = new THREE.MeshPhongMaterial();
        this.phongMaterial.map = this.texture

        this.mesh = new THREE.Mesh(this.geometry, this.basicMaterial);

        this.calculateIlum = 0;

        this.defX = distance;
        this.defY = radius;
        this.defZ = 0;
        this.defdistance = distance;
        this.defradius = radius;
        this.reset();
    }


    setPosition(x, y, z) {
        this.mesh.position.x = x;
        this.mesh.position.y = y;
        this.mesh.position.z = z;
    }

    reset() {
        this.mesh.position.x = this.defdistance;
        this.mesh.position.y = this.defradius;
        this.mesh.position.z = 0;

        this.time = 0;
        this.distance = this.defdistance;
        this.radius = this.defradius;
        this.moving = true;
        this.starting = true;
        this.stopping = false;
        this.velocity = 0;
        this.mesh.lookAt(new THREE.Vector3(0, this.radius, 0));
    }

    update(dt) {
        if (!this.moving) return;

        if (this.starting) {
            this.velocity += ACCEL*dt;

            if (this.velocity > MAX_VELOCITY) {
                this.starting = false;
                this.velocity = MAX_VELOCITY;
            }
        }

        if (this.stopping) {
            this.velocity -= ACCEL*dt;

            if (this.velocity < 0) {
                this.stopping = false;
                this.moving = false;
                this.velocity = 0;
            }
        }

        this.time += dt*this.velocity;
        this.mesh.position.x = this.distance * Math.cos(this.time);
        this.mesh.position.z = this.distance * Math.sin(this.time);

        this.mesh.lookAt(new THREE.Vector3(0, this.radius, 0));

        // Subtracting from dt wouldn't work because lookAt resets the rotation
        // Equaling time doesn't work because the rotation has spikes
        // Subtracting from time is the same as equaling time, but it works
        // somehow...
        this.mesh.rotation.z -= this.time*ROTATION;
    }

    toggleMovement() {
        if (this.moving) {
            if (this.starting) {
                this.starting = false;
                this.stopping = true;
            } else if (this.stopping) {
                this.stopping = false;
                this.starting = true;
            } else {
                this.stopping = true;
            }
        } else {
            this.moving = true;
            this.starting = true;
        }
    }

    showWireFrame() {
        this.basicMaterial.wireframe = !this.basicMaterial.wireframe
        this.phongMaterial.wireframe = !this.phongMaterial.wireframe
    }

    startStopCalc() {
        if (!this.calculateIlum) {
            this.mesh.material = this.phongMaterial;
            this.mesh.geometry.uvsNeedsUpdate = true;
            this.mesh.needsUpdate = true;

            this.calculateIlum = 1;
        } else {
            this.mesh.material = this.basicMaterial;
            this.mesh.geometry.uvsNeedsUpdate = true;
            this.mesh.needsUpdate = true;

            this.calculateIlum = 0;
        }
    }

    getMesh() {
        return this.mesh;
    }
}
