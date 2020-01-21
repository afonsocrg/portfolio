class Dice extends Body {
    constructor(size) {
        super();
        const loader = new THREE.TextureLoader();
        this.geometry = new THREE.BoxBufferGeometry(size, size, size, 1, 1, 1);

        this.mesh = new THREE.Object3D()

        this.pivotPoint = new THREE.Object3D()

        this.mesh.add(this.pivotPoint)
        

        this.textures = [
            loader.load('../img/1Face.jpg'),
            loader.load('../img/2Face.jpg'),
            loader.load('../img/3Face.jpg'),
            loader.load('../img/4Face.jpg'),
            loader.load('../img/5Face.jpg'),
            loader.load('../img/6Face.jpg')
        ]

        this.bumpMaps = [
            loader.load('../img/1FaceBM.jpg'),
            loader.load('../img/2FaceBM.jpg'),
            loader.load('../img/3FaceBM.jpg'),
            loader.load('../img/4FaceBM.jpg'),
            loader.load('../img/5FaceBM.jpg'),
            loader.load('../img/6FaceBM.jpg')
        ]
        
        this.basicMaterials = [
            new THREE.MeshBasicMaterial({map: this.textures[0]}),
            new THREE.MeshBasicMaterial({map: this.textures[1]}),
            new THREE.MeshBasicMaterial({map: this.textures[2]}),
            new THREE.MeshBasicMaterial({map: this.textures[3]}),
            new THREE.MeshBasicMaterial({map: this.textures[4]}),
            new THREE.MeshBasicMaterial({map: this.textures[5]})
        ]



        this.phongMaterials = [
            new THREE.MeshPhongMaterial({map: this.textures[0], bumpMap: this.bumpMaps[0]}),
            new THREE.MeshPhongMaterial({map: this.textures[1], bumpMap: this.bumpMaps[1]}),
            new THREE.MeshPhongMaterial({map: this.textures[2], bumpMap: this.bumpMaps[2]}),
            new THREE.MeshPhongMaterial({map: this.textures[3], bumpMap: this.bumpMaps[3]}),
            new THREE.MeshPhongMaterial({map: this.textures[4], bumpMap: this.bumpMaps[4]}),
            new THREE.MeshPhongMaterial({map: this.textures[5], bumpMap: this.bumpMaps[5]})
        ]

        this.geometry.clearGroups();
        this.geometry.addGroup(0, 6, 0);
        this.geometry.addGroup(6, 6, 5);
        this.geometry.addGroup(12, 6, 1);
        this.geometry.addGroup(18, 6, 4);
        this.geometry.addGroup(24, 6, 3);
        this.geometry.addGroup(30, Infinity, 2);

        this.basicFaces = new THREE.MeshFaceMaterial(this.basicMaterials);
        this.phongFaces = new THREE.MeshFaceMaterial(this.phongMaterials);

        this.dice = new THREE.Mesh(this.geometry, this.basicFaces);
        this.dice.rotation.x = Math.atan(Math.sqrt(1/2))
        this.dice.rotation.z = Math.PI/4;

        this.pivotPoint.add(this.dice);

        this.calculateIlum = 0;
        this.mesh.rotation.y = 0;
    }

    getMesh() {
        return this.mesh;
    }

    setPosition(x, y, z) {
        this.mesh.position.x = x
        this.mesh.position.y = y
        this.mesh.position.z = z
        
    }

    reset() {
        this.mesh.rotation.y = 0;
    }

    showWireFrame() {
        this.basicMaterials.forEach(m => {
            m.wireframe = !m.wireframe
        })
        this.phongMaterials.forEach(m => {
            m.wireframe = !m.wireframe
        })
        return;
    }

    update(dt) {
        this.mesh.rotation.y = (this.mesh.rotation.y + 1*dt)%(Math.PI*2);
    }

    startStopCalc() {
        if(!this.calculateIlum) {
            this.dice.material = this.phongFaces;
            this.dice.geometry.uvsNeedsUpdate = true;
            this.dice.needsUpdate = true;

            this.calculateIlum = 1;
        } else {
            this.dice.material = this.basicFaces;
            this.dice.geometry.uvsNeedsUpdate = true;
            this.dice.needsUpdate = true;

            this.calculateIlum = 0;

        }
    }
}
