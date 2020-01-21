class Painting extends Body {
    constructor(x, y, z) {
        super()
        this.mesh = new THREE.Object3D();



        //Background
        this.backgroundBasic = createBackgroundBasicMaterial();
        this.backgroundLambert = createBackgroundLambertMaterial();
        this.backgroundPhong = createBackgroundPhongMaterial();
        this.background = createBackground(this.backgroundBasic);

        this.mesh.add(this.background)

        //Black Square
        this.blackSquareBasic = createSquareBasicMaterial();
        this.blackSquareLambert = createSquareLambertMaterial();
        this.blackSquarePhong = createSquarePhongMaterial();
        
        //for cycle to make squares
        this.blackSquareList = []
        let i = 0;

        for(let f = 0; f < 4; f++){
            for(let j = 0; j < 8; j++){
                this.blackSquareList[i] = createBlackSquare(1, +21-6 - 10*f, +41-6 - 10*j, this.blackSquareBasic);
                this.mesh.add(this.blackSquareList[i++])
            }
        }

        //White Dots
        this.whiteDotBasic = createDotBasicMaterial();
        this.whiteDotLambert = createDotLambertMaterial();
        this.whiteDotPhong = createDotPhongMaterial();

        //for cycle to make white dots
        this.whiteDotsList = []
        i = 0;
        for(let f = 0; f < 5; f++) {
            for(let j = 0; j < 9; j++) {
                this.whiteDotsList[i] = createWhiteDot(1, +21.5-1.5 - 10*f, 41.5-1.5 - 10*j, this.whiteDotBasic)
                this.mesh.add(this.whiteDotsList[i++]);
            }
        }

        //Create frame

        this.frameBasicMaterial = createFrameBasicMaterial();
        this.frameLambertMaterial = createFrameLambertMaterial();
        this.framePhongMaterial = createFramePhongMaterial();

        this.frame = {}
        this.frame['left'] = createLRFrame(2.5, 0,+41.5 + 2.5, this.frameBasicMaterial);
        this.mesh.add(this.frame['left'])
        this.frame['top'] = createTBFrame(2.5, +21.5 + 2.5,0, this.frameBasicMaterial);
        this.mesh.add(this.frame['top'])
        this.frame['right'] = createLRFrame(2.5, 0, -41.5 - 2.5, this.frameBasicMaterial);
        this.mesh.add(this.frame['right'])
        this.frame['bottom'] = createTBFrame(2.5 , -21.5 - 2.5, 0, this.frameBasicMaterial)
        this.mesh.add(this.frame['bottom'])


        this.mesh.position.set(x,y,z);

        this.calculateIlum = 0;
        this.diffuse = 1;



    }

    getMesh() {
        return this.mesh
    }

    startStopCalc() {
        if(this.calculateIlum == 1) {
            this.background.material = this.backgroundBasic
            this.background.geometry.uvsNeedsUpdate = true;
            this.background.needsUpdate = true;

            this.blackSquareList.forEach(square => {
                square.material = this.blackSquareBasic;
                square.geometry.uvsNeedsUpdate = true
                square.needsUpdate = true;
            })

            this.whiteDotsList.forEach(dot => {
                dot.material = this.whiteDotBasic;
                dot.geometry.uvsNeedsUpdate = true
                dot.needsUpdate = true; 
            })

            let parts = Object.values(this.frame)

            for(const framePart of parts) {
                framePart.material = this.frameBasicMaterial;
                framePart.geometry.uvsNeedsUpdate = true
                framePart.needsUpdate = true
            }   
                
            

            this.calculateIlum = 0;

        } else {
            if(this.diffuse == 1) {
                this.background.material = this.backgroundLambert
                this.background.geometry.uvsNeedsUpdate = true;
                this.background.needsUpdate = true;

                this.blackSquareList.forEach(square => {
                    square.material = this.blackSquareLambert;
                    square.geometry.uvsNeedsUpdate = true
                    square.needsUpdate = true;
                })

                this.whiteDotsList.forEach(dot => {
                    dot.material = this.whiteDotLambert;
                    dot.geometry.uvsNeedsUpdate = true
                    dot.needsUpdate = true; 
                })

                let parts = Object.values(this.frame)

                for(const framePart of parts) {
                    framePart.material = this.frameLambertMaterial;
                    framePart.geometry.uvsNeedsUpdate = true
                    framePart.needsUpdate = true
                }


                this.calculateIlum = 1;
            } else {
                this.background.material = this.backgroundPhong
                this.background.geometry.uvsNeedsUpdate = true;
                this.background.needsUpdate = true;

                this.blackSquareList.forEach(square => {
                    square.material = this.blackSquarePhong;
                    square.geometry.uvsNeedsUpdate = true
                    square.needsUpdate = true;
                })

                this.whiteDotsList.forEach(dot => {
                    dot.material = this.whiteDotPhong;
                    dot.geometry.uvsNeedsUpdate = true
                    dot.needsUpdate = true; 
                })

                let parts = Object.values(this.frame)

                for(const framePart of parts) {
                    framePart.material = this.framePhongMaterial;
                    framePart.geometry.uvsNeedsUpdate = true
                    framePart.needsUpdate = true
                }


                this.calculateIlum = 1;
            }
        }
    }

    toLambertPhong() {
        if(this.calculateIlum == 1) {
            if(this.diffuse == 1) {
                this.background.material = this.backgroundPhong
                this.background.geometry.uvsNeedsUpdate = true;
                this.background.needsUpdate = true;

                this.blackSquareList.forEach(square => {
                    square.material = this.blackSquarePhong;
                    square.geometry.uvsNeedsUpdate = true
                    square.needsUpdate = true;
                })

                this.whiteDotsList.forEach(dot => {
                    dot.material = this.whiteDotPhong;
                    dot.geometry.uvsNeedsUpdate = true
                    dot.needsUpdate = true; 
                })

                let parts = Object.values(this.frame)

                for(const framePart of parts) {
                    framePart.material = this.framePhongMaterial;
                    framePart.geometry.uvsNeedsUpdate = true
                    framePart.needsUpdate = true
                }


                this.diffuse = 0;
            } else {
                this.background.material = this.backgroundLambert
                this.background.geometry.uvsNeedsUpdate = true;
                this.background.needsUpdate = true;

                this.blackSquareList.forEach(square => {
                    square.material = this.blackSquareLambert;
                    square.geometry.uvsNeedsUpdate = true
                    square.needsUpdate = true;
                })

                this.whiteDotsList.forEach(dot => {
                    dot.material = this.whiteDotLambert;
                    dot.geometry.uvsNeedsUpdate = true
                    dot.needsUpdate = true; 
                })

                let parts = Object.values(this.frame)

                for(const framePart of parts) {
                    framePart.material = this.frameLambertMaterial;
                    framePart.geometry.uvsNeedsUpdate = true
                    framePart.needsUpdate = true
                }


                this.diffuse = 1;
            }
        }
    }
}

function createBackgroundBasicMaterial() {
    return new THREE.MeshBasicMaterial({color: 0x999999})
}

function createBackgroundLambertMaterial() {
    return new THREE.MeshLambertMaterial({color: 0x999999})
}

function createBackgroundPhongMaterial() {
    return new THREE.MeshPhongMaterial({color: 0x999999});
}

function createBackground(material) {
    'use strict'
    let backgroundGeometry = new THREE.BoxGeometry(3, 43, 83);


    return new THREE.Mesh(backgroundGeometry, material);
}

function createSquareBasicMaterial() {
    return new THREE.MeshBasicMaterial({color: 0x050706})
}

function createSquareLambertMaterial() {
    return new THREE.MeshLambertMaterial({color: 0x050706})
}

function createSquarePhongMaterial() {
    return new THREE.MeshPhongMaterial({color: 0x050706})
}

function createBlackSquare(x, y, z, material) {
    let blackSquareGeometry = new THREE.BoxGeometry(3,8,8);
    let blackSquare = new THREE.Mesh(blackSquareGeometry, material)

    blackSquare.position.set(x, y, z);
    return blackSquare
}

function createDotBasicMaterial() {
    return new THREE.MeshBasicMaterial({color: 0xffffff})
}

function createDotLambertMaterial() {
    return new THREE.MeshLambertMaterial({color: 0xffffff}) 
}

function createDotPhongMaterial() {
    return new THREE.MeshPhongMaterial({color: 0xffffff})
}

function createWhiteDot(x, y, z, material) {
    let whiteDotGeometry = new THREE.CylinderGeometry(1.5, 1.5, 1, 20, 20);
    let whiteDot = new THREE.Mesh(whiteDotGeometry, material);

    whiteDot.position.set(x+1, y, z);
    whiteDot.rotation.z = 0.5*Math.PI
    return whiteDot
}

function createFrameBasicMaterial() {
    return new THREE.MeshBasicMaterial({color: 0x855e42})
}

function createFrameLambertMaterial() {
    return new THREE.MeshLambertMaterial({color: 0x855e42})
}

function createFramePhongMaterial() {
    return new THREE.MeshPhongMaterial({color: 0x855e42})
}

function createLRFrame(x, y, z, material) {
    let frameGeometry = new THREE.BoxGeometry(5, 53, 5)

    let frame = new THREE.Mesh(frameGeometry, material);
    frame.position.set(x, y, z)
    return frame
}

function createTBFrame(x, y, z, material) {
    let frameGeometry = new THREE.BoxGeometry(5, 5, 93)

    let frame = new THREE.Mesh(frameGeometry, material);
    frame.position.set(x, y, z)
    return frame
}