class Pedestal extends Body {
    constructor() {
        super();
        let geometry = new THREE.CylinderGeometry( 20, 17, 55, 15);
        this.basicMaterial =  new THREE.MeshBasicMaterial({color: 0xaaaaaa});
        this.lambertMaterial = new THREE.MeshLambertMaterial({color: 0xaaaaaa});
        this.phongMaterial = new THREE.MeshPhongMaterial({color: 0xaaaaaa});

        this.mesh = new THREE.Mesh(geometry, this.basicMaterial);
        this.mesh.position.x = 31;
        this.mesh.position.y = 25;
        this.mesh.position.z = 59;
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
