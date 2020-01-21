class ChessBoard extends Body {
    constructor(width, height, depth) {
        super();
        this.mesh = new THREE.Object3D();

        this.chessGeometry = new THREE.BoxGeometry(width, height, depth, 10, 10, 10);

        this.chessTexture = new THREE.TextureLoader().load("../img/14515.jpg");
        this.chessBumpTexture = new THREE.TextureLoader().load("../img/14515-bump.jpg");

        this.frameTexture = new THREE.TextureLoader().load("../img/10750.jpg");
        this.frameBumpTexture = new THREE.TextureLoader().load("../img/10750-bump.jpg");

        this.chessBasicMaterial = new THREE.MeshBasicMaterial({color: 0xffffff});
        this.chessBasicMaterial.map = this.chessTexture;

        this.chessPhongMaterial = new THREE.MeshPhongMaterial();
        this.chessPhongMaterial.map = this.chessTexture;
        this.chessPhongMaterial.bumpMap = this.chessBumpTexture;


        this.chessBoard = new THREE.Mesh(this.chessGeometry, this.chessBasicMaterial);
        this.mesh.add(this.chessBoard);
        this.frame = {};

        let basic = new THREE.MeshBasicMaterial()
        this.frame['left'] = {
            basicMaterial: basic,
            object: createLRFrame(width/2 + 10, height , depth, basic),
            phongMaterial: new THREE.MeshPhongMaterial()
        }
        
        this.frame['left'].basicMaterial.map = this.frameTexture;
        this.frame['left'].phongMaterial.map = this.frameTexture;
        this.frame['left'].phongMaterial.bumpMap = this.frameBumpTexture;
        
        this.mesh.add(this.frame['left'].object);
        
        basic = new THREE.MeshBasicMaterial();
        this.frame['right'] = {
            basicMaterial: basic,
            object: createLRFrame(-width/2 - 10, height, depth, basic),
            phongMaterial: new THREE.MeshPhongMaterial()
        }
        
        this.frame['right'].basicMaterial.map = this.frameTexture;
        this.frame['right'].phongMaterial.map = this.frameTexture;
        this.frame['right'].phongMaterial.bumpMap = this.frameBumpTexture;
        
        this.mesh.add(this.frame['right'].object);
        
        basic = new THREE.MeshBasicMaterial();
        this.frame['top'] = {
            basicMaterial: basic,
            object: createTBFrame(depth/2 + 10, height, width, basic),
            phongMaterial: new THREE.MeshPhongMaterial()
        }
        
        this.frame['top'].basicMaterial.map = this.frameTexture;
        this.frame['top'].phongMaterial.map = this.frameTexture;
        this.frame['top'].phongMaterial.bumpMap = this.frameBumpTexture;
        
        this.mesh.add(this.frame['top'].object);

        basic = new THREE.MeshBasicMaterial();

        this.frame['bottom'] = {
            basicMaterial: basic,
            object: createTBFrame(-depth/2 - 10, height, width, basic),
            phongMaterial: new THREE.MeshPhongMaterial()
        }
        
        this.frame['bottom'].basicMaterial.map = this.frameTexture;
        this.frame['bottom'].phongMaterial.map = this.frameTexture;
        this.frame['bottom'].phongMaterial.bumpMap = this.frameBumpTexture;
        
        this.mesh.add(this.frame['bottom'].object);
        

        this.calculateIlum = 0;
    }

    setPosition(x, y, z) {
        this.mesh.position.x = x;
        this.mesh.position.y = y;
        this.mesh.position.z = z;
    }

    reset() {
    }

    getPosition() {
        return this.mesh.position;
    }

    getMesh() {
        return this.mesh;
    }

    showWireFrame() {
        this.chessBasicMaterial.wireframe = !this.chessBasicMaterial.wireframe;
        this.chessPhongMaterial.wireframe = !this.chessPhongMaterial.wireframe;
        
        let parts = Object.values(this.frame);
        for (const part of parts) {
            part.basicMaterial.wireframe = !part.basicMaterial.wireframe;
            part.phongMaterial.wireframe = !part.phongMaterial.wireframe;
        }
    }

    startStopCalc() {
        if(!this.calculateIlum) {
            this.chessBoard.material = this.chessPhongMaterial;
            this.chessBoard.geometry.uvsNeedsUpdate = true;
            this.chessBoard.needsUpdate = true;

            let parts = Object.values(this.frame) 

            for (const framePart of parts) {
                framePart.object.material = framePart.phongMaterial;
                framePart.object.geometry.uvsNeedsUpdate = true;
                framePart.object.needsUpdate = true
            }
            this.calculateIlum = 1;
        } else {
            this.chessBoard.material = this.chessBasicMaterial;
            this.chessBoard.geometry.uvsNeedsUpdate = true;
            this.chessBoard.needsUpdate = true;

            let parts = Object.values(this.frame) 

            for (const framePart of parts) {
                framePart.object.material = framePart.basicMaterial;
                framePart.object.geometry.uvsNeedsUpdate = true;
                framePart.object.needsUpdate = true
            }

            this.calculateIlum = 0;
        }
    }
}


function createLRFrame(delta, height, depth, material) {
    let frameGeometry = new THREE.BoxGeometry(20, height, depth + 40);

    let frame = new THREE.Mesh(frameGeometry, material);
    frame.position.set(delta, 0, 0);
    return frame;
}

function createTBFrame(delta, height, width, material) {
    let frameGeometry = new THREE.BoxGeometry(width + 40, height, 20);
    let frame = new THREE.Mesh(frameGeometry, material);

    frame.position.set(0, 0, delta);
    return frame;
}
