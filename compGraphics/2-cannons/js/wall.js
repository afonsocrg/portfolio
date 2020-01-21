class Wall extends Body {
    constructor(width, height, depth) {
        super();
        let geometry = new THREE.BoxGeometry(width, height, depth);
        let material = new THREE.MeshBasicMaterial({color: 0x7C0A02});

        this.mesh = new THREE.Mesh(geometry, material);
    }

    setPosition(x, y, z) {
        this.mesh.position.x = x;
        this.mesh.position.y = y;
        this.mesh.position.z = z;
    }

    getPosition() {
        return this.mesh.position;
    }

    getMesh() {
        return this.mesh;
    }
}
