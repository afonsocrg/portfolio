class Wall extends Body {
    constructor(width, height, depth, wColor) {
        super();
        let geometry = new THREE.BoxGeometry(width, height, depth, 10, 10, 10);
        this.basicMaterial =  new THREE.MeshBasicMaterial({color: wColor});
        this.lambertMaterial = new THREE.MeshLambertMaterial({color: wColor});
        this.phongMaterial = new THREE.MeshPhongMaterial({color: wColor});

        this.mesh = new THREE.Mesh(geometry, this.basicMaterial);
        this.calculateIlum = 0;
        this.diffuse = 1;
    }

    setPosition(x, y, z) {
        this.mesh.position.x = x;
        this.mesh.position.y = y;
        this.mesh.position.z = z
    }

    getPosition() {
        return this.mesh.position;
    }

    getMesh() {
        return this.mesh;
    }


    startStopCalc() {
        if(this.calculateIlum == 1) {
            this.mesh.material = this.basicMaterial;
            this.mesh.geometry.uvsNeedsUpdate = true
            this.mesh.needsUpdate = true;
            this.calculateIlum = 0;
        } else {
            if(this.diffuse == 1) {
                this.mesh.material = this.lambertMaterial;
                this.mesh.geometry.uvsNeedsUpdate = true
                this.mesh.needsUpdate = true;
                this.calculateIlum = 1;
            } else {
                this.mesh.material = this.phongMaterial;
                this.mesh.geometry.uvsNeedsUpdate = true
                this.mesh.needsUpdate = true;
                this.calculateIlum = 1;
            }
        }
         
    }

    toLambertPhong() {
        if(this.calculateIlum == 1) {
            if(this.diffuse == 1) {
                this.mesh.material = this.phongMaterial;
                this.mesh.geometry.uvsNeedsUpdate = true
                this.mesh.needsUpdate = true;
                this.diffuse = 0;
            } else {
                this.mesh.material = this.lambertMaterial;
                this.mesh.geometry.uvsNeedsUpdate = true
                this.mesh.needsUpdate = true;
                this.diffuse = 1;
            }
        }
        
    }
}
