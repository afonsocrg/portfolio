const PHI = (1 + Math.sqrt(5))/2
const sizeFactor = 12
class Icosahedron extends Body {
    constructor() {
        super();
        this.basicMaterial =  new THREE.MeshBasicMaterial({ vertexColors: THREE.FaceColors });
        this.lambertMaterial = new THREE.MeshLambertMaterial({ vertexColors: THREE.FaceColors });
        this.phongMaterial = new THREE.MeshPhongMaterial({ vertexColors: THREE.FaceColors });


        let geometry = new THREE.Geometry();
        // 0,1,2,3
        geometry.vertices.push( new THREE.Vector3(-1*sizeFactor,  PHI*sizeFactor, 0));
        geometry.vertices.push( new THREE.Vector3( 1*sizeFactor,  PHI*sizeFactor, 0));
        geometry.vertices.push( new THREE.Vector3(-1*sizeFactor, -PHI*sizeFactor, 0));
        geometry.vertices.push( new THREE.Vector3( 1*sizeFactor, -PHI*sizeFactor, 0));

        // 4,5,6,7
        geometry.vertices.push( new THREE.Vector3(-PHI*sizeFactor, 0,  1*sizeFactor));
        geometry.vertices.push( new THREE.Vector3(-PHI*sizeFactor, 0, -1*sizeFactor));
        geometry.vertices.push( new THREE.Vector3( PHI*sizeFactor, 0,  1*sizeFactor));
        geometry.vertices.push( new THREE.Vector3( PHI*sizeFactor, 0, -1*sizeFactor));

        // 8,9,10,11
        geometry.vertices.push( new THREE.Vector3(0,  1*sizeFactor, -PHI*sizeFactor));
        geometry.vertices.push( new THREE.Vector3(0,  1*sizeFactor,  PHI*sizeFactor));
        geometry.vertices.push( new THREE.Vector3(0, -1*sizeFactor, -PHI*sizeFactor));
        geometry.vertices.push( new THREE.Vector3(0, -1*sizeFactor,  PHI*sizeFactor));

        geometry.faces.push(new THREE.Face3(0, 5, 4));
        geometry.faces.push(new THREE.Face3(2, 4, 5));
        geometry.faces.push(new THREE.Face3(5, 8, 10));
        geometry.faces.push(new THREE.Face3(9, 4, 11));
        geometry.faces.push(new THREE.Face3(8, 7, 10));
        geometry.faces.push(new THREE.Face3(6, 9, 11));
        geometry.faces.push(new THREE.Face3(1, 6, 7));
        geometry.faces.push(new THREE.Face3(3, 7, 6));
        geometry.faces.push(new THREE.Face3(0, 4, 9));
        geometry.faces.push(new THREE.Face3(0, 8, 5));
        geometry.faces.push(new THREE.Face3(1, 9, 6));
        geometry.faces.push(new THREE.Face3(1, 7, 8));
        geometry.faces.push(new THREE.Face3(1, 0, 9));
        geometry.faces.push(new THREE.Face3(0, 1, 8));
        geometry.faces.push(new THREE.Face3(2, 10, 3));
        geometry.faces.push(new THREE.Face3(3, 11, 2));
        geometry.faces.push(new THREE.Face3(2, 11, 4));
        geometry.faces.push(new THREE.Face3(5, 10, 2));
        geometry.faces.push(new THREE.Face3(3, 6, 11));
        geometry.faces.push(new THREE.Face3(3, 10, 7));

        for (var i = 0; i < 20; i++) {
            geometry.faces[i].color = new THREE.Color(Math.random() * 0xffffff);
        }

        geometry.computeFaceNormals();
        geometry.computeVertexNormals();

        // this.mesh = new THREE.Mesh( geometry, material);
        this.mesh = new THREE.Mesh( geometry, this.basicMaterial);

        this.thetaVel = Math.PI;
        this.theta = 0;
        this.moveAmp = 3;
        this.baseY = 90;

        this.calculateIlum = 0;
        this.diffuse = 1;

        this.mesh.position.x = 30;
        this.mesh.position.y = 80;
        this.mesh.position.z = 60;

        this.mesh.rotation.z -= Math.atan(1/PHI);

        this.gregate = true;
        this.vertAngleSpeed = Math.PI;
        this.vertAngle = []
        this.vertAmp = []
        this.vertMinAmp = sizeFactor / 8;
        this.vertMaxAmp = sizeFactor * 1.5;
        for(let i = 0; i < 12; i++) {
            this.vertAngle.push(Math.random() * Math.PI * 2)
            this.vertAmp.push(this.vertMinAmp + Math.random()*(this.vertMaxAmp - this.vertMinAmp))
        }
        // console.log(this.vertAngle)

        this.tipLength = this.mesh.geometry.vertices[0].length();
        this.step = 0;
    }

    getMesh() {
        return this.mesh;
    }

    toggleGregate() {
        this.gregate = !this.gregate;
    }

    update(dt) {
        this.mesh.geometry.verticesNeedUpdate = true;
        this.step += 1;
        this.theta = (this.theta + dt*this.thetaVel)%(Math.PI * 2);
        this.mesh.position.y = this.baseY + Math.sin(this.theta) * this.moveAmp;

        this.mesh.rotation.y = (this.mesh.rotation.y + 0.01)%(Math.PI*2);

        if(!this.gregate) return;
        for(let i = 0; i < 12; i++) {
            this.vertAngle[i] += (this.vertAngleSpeed * dt)%(Math.PI * 2);
            this.mesh.geometry.vertices[i].setLength(this.tipLength + this.vertAmp[i] * Math.cos(this.vertAngle[i]));
        }
        // console.log(this.vertAngle)
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
