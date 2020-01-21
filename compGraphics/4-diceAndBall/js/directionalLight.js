class DirectionalLight {
    constructor(x, y, z) {
        this.directionalLight = new THREE.DirectionalLight(0xffffff, 1);

        this.directionalLight.position.set(x, y, z)
        //this.directionalLight.target = 

        this.on = 1;
    }

    getObject() {
        return this.directionalLight
    }

    turnOnOff() {
        if (this.on) {
            this.directionalLight.visible = false;
            this.on = 0;
        } else {
            this.directionalLight.visible = true;
            this.on = 1;
        }
    }
}
