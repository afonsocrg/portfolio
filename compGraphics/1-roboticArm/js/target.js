class Target extends Body {
    constructor(x, y, z) {
        super();
        let targetBaseGeometry = new THREE.CylinderGeometry(5, 5, 40, 20, 20);
        let targetBaseMaterial = new THREE.MeshBasicMaterial({color: 0xffffff});
        
        this.targetBase = new THREE.Mesh(targetBaseGeometry, targetBaseMaterial);
        this.targetBase.position.x = x;
        this.targetBase.position.y = y;
        this.targetBase.position.z = z;

        let torusGeometry = new THREE.TorusGeometry(10, 5, 20, 20);
        let torusMaterial = new THREE.MeshBasicMaterial({color: 0xFFCA28});

        this.targetTop = new THREE.Mesh(torusGeometry, torusMaterial);
        this.targetTop.position.x = x;
        this.targetTop.position.y = y + 35;
        this.targetTop.position.z = z;

        this.mesh = new THREE.Object3D();
        this.mesh.add(this.targetBase);
        this.mesh.add(this.targetTop);
    }

    get basePosition() {
        return [this.targetBase.position.x, this.targetBase.position.y, 
            this.targetBase.position.z];
    }

    get topPosition() {
        return [this.targetTop.position.x, this.targetTop.position.y, 
            this.targetTop.position.z];
    }

    toggleWireframe() {
        this.targetBase.material.wireframe = !this.targetBase.material.wireframe;
        this.targetTop.material.wireframe = !this.targetTop.material.wireframe;
    }
}

function targetSwitchWireframe() {
    newTarget.switchWireframe();
}
