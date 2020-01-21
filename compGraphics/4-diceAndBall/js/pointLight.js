const spotColor = 0xf6fa7f;

class PointLight extends Body {
    constructor() {
        super();
        this.light = new THREE.PointLight(spotColor, 10, 305);
        this.light.position.set(0, 300, 0);
        scene.add(this.light);
    }

    toggle() {
       this.light.visible = !this.light.visible; 
    }
}
