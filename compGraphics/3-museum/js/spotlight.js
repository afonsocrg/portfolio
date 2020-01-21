const spotColor = 0xf6fa7f


class SpotLight extends Body {
    constructor(x, y, z, target) {
        super()
        this.spotlight = new THREE.Object3D();

        this.frontSpotLight = createFront()
        this.frontSpotLight.position.set(0, 10, 0)
        this.spotlight.add(this.frontSpotLight)

        this.backSpotLight = createBack()
        this.spotlight.add(this.backSpotLight)

        this.spotlight.position.set(x, y ,z)
        this.spotlight.rotation.z = -0.25*Math.PI

        this.light = new THREE.SpotLight(spotColor, 0.7, 0, 0.4, 0.2, 1);
        this.spotlight.add(this.light)

        scene.add(this.spotlight)


        this.light.target.position.x = target.position.x;
        this.light.target.position.z = z
        this.light.target.position.y = target.position.y; 
        
        
        scene.add(this.light.target)

        this.isVisible = true

    }

    update() {
        if(this.isVisible) {
            this.light.visible = false
            this.isVisible = false
            this.backSpotLight.material.color.setHex(0x0);
        } else {
            this.light.visible = true;
            this.isVisible = true
            this.backSpotLight.material.color.setHex(spotColor);
        }
    }
}

function createFront() {
    let frontGeometry = new THREE.ConeGeometry(5, 20, 32);
    let frontMaterial = new THREE.MeshBasicMaterial({color: 0x000000});

    return new THREE.Mesh(frontGeometry, frontMaterial);
}

function createBack() {
    let backGeometry = new THREE.SphereGeometry(5, 32, 32);
    let backMaterial = new THREE.MeshBasicMaterial({color: spotColor});

    return new THREE.Mesh(backGeometry, backMaterial);
}
