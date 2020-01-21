class CollisionHandler {
    constructor() {}

    handleCollision(obj1, obj2) {
        if (obj1 instanceof Cannonball &&
            obj2 instanceof Cannonball) {

            let distance = obj1.getDistance(obj2);

            if (distance <= 2*RADIUS) {
                obj1.collide(obj2);
            }
        }
    }
}
