const DECELERATION = -30;

class Cannonball extends Body {
    constructor(x, y, z, velocity, radius, color = 0x2B2D2F) {
        super();
        this.basePivotPoint = createPivotPoint();
        this.rotationPivotPoint = createPivotPoint()
        this.ball = createCannonBall(color);
        this.rotationPivotPoint.add(this.ball)
        this.basePivotPoint.add(this.rotationPivotPoint);

        this.radius = radius;

        this.basePivotPoint.add(this.ball);
        this.mesh = new THREE.Object3D();
        this.mesh.position.set(x, y + this.radius, z);

        this.mesh.add(this.basePivotPoint)
        
        this.velocityMagnitude = velocity.length() * (1 + Math.random());
        this.velocityDirection = velocity.normalize();
        
        this.axis = new THREE.AxisHelper(1);
        if(showAxis)
            this.axis.scale.set(10,10,10)

        this.ball.add(this.axis);

        this.basePivotPoint.lookAt(this.velocityDirection);
    }

    update(timeDelta) {
        let velocity = this.velocityDirection.clone();
        velocity.multiplyScalar(this.velocityMagnitude * timeDelta);
        this.mesh.position.add(velocity);

        this.ball.rotation.x += (this.velocityMagnitude/this.radius)*timeDelta;

        this.velocityMagnitude += DECELERATION * timeDelta;
        this.velocityMagnitude = Math.max(this.velocityMagnitude, 0);

        if(this.mesh.position.z < walls[WALL_LEFT]['limit_z_back'] - RADIUS) {
            isBallOffLimits = 1;
      }


        this.checkwalls(timeDelta);
    }

    setVelocityVector(v) {
        let vec = v.clone();
        this.velocityMagnitude = vec.length();
        this.velocityDirection = vec.normalize();
        

        //Axis are not updated if we dont do this
        this.basePivotPoint.lookAt(this.velocityDirection);
    }

    getVelocityVector() {
        let vec = this.velocityDirection.clone();
        vec.multiplyScalar(this.velocityMagnitude);

        return vec;
    }

    getVelocityMagnitude() {
        return this.velocityMagnitude;
    }

    getPosition() {
        return this.mesh.position.clone();
    }

    getDistance(ball) {
        return this.getPosition().distanceTo(ball.getPosition());
    }

    isWhite(_r, _g, _b) {
        let b = 0.8156862745098039
        let g = 0.9921568627450981
        let r = 1
        return r == _r && g == _g && b == _b
    }

    collide(ball) {
        // https://en.wikipedia.org/wiki/Elastic_collision

        let x1 = this.getPosition();
        let x2 = ball.getPosition();
        let v1 = this.getVelocityVector();
        let v2 = ball.getVelocityVector();

        let x1_minus_x2 = x1.clone().sub(x2);
        let x2_minus_x1 = x2.clone().sub(x1);
        let v1_minus_v2 = v1.clone().sub(v2);
        let v2_minus_v1 = v2.clone().sub(v1);

        let v1_new = x1_minus_x2.clone();
        v1_new.multiplyScalar(v1_minus_v2.clone().dot(x1_minus_x2));
        v1_new.multiplyScalar(1/x1_minus_x2.lengthSq());
        v1_new.negate();
        v1_new.add(v1);

        let v2_new = x2_minus_x1.clone();
        v2_new.multiplyScalar(v2_minus_v1.clone().dot(x2_minus_x1));
        v2_new.multiplyScalar(1/x2_minus_x1.lengthSq());
        v2_new.negate();
        v2_new.add(v2);

        this.setVelocityVector(v1_new);
        ball.setVelocityVector(v2_new);

        this.separate(ball);
        console.log(this.ball.material.color)
        if(this.isWhite(this.ball.material.color.r, this.ball.material.color.g, this.ball.material.color.b)) {
            console.log("yay")
            this.ball.material.color = ball.ball.material.color;
            // this.ball.material.color = new THREE.Color(0x000000)
        }
        if(ball.isWhite(ball.ball.material.color.r, ball.ball.material.color.g, ball.ball.material.color.b)) {
            console.log("yay")
            ball.ball.material.color = this.ball.material.color;
        }
    }

    checkwalls(timeDelta) {
        let position = this.getPosition();

        if ((position.x + this.radius) >= walls[WALL_LEFT].limit_x) {
            if ((position.z + this.radius) <= walls[WALL_LEFT].limit_z_front
            && (position.z - this.radius) >= walls[WALL_LEFT].limit_z_back) {
                position.x = walls[WALL_LEFT].mesh.position.x - 7/2 - this.radius; 
                this.setPosition(position);
                this.wallReflectX();
            }
        }

        if ((position.x - this.radius) <= walls[WALL_RIGHT].limit_x) {
            if ((position.z + this.radius) <= walls[WALL_RIGHT].limit_z_front
            && (position.z - this.radius) >= walls[WALL_RIGHT].limit_z_back) {
                position.x = walls[WALL_RIGHT].mesh.position.x + 7/2 + this.radius;
                this.setPosition(position);
                this.wallReflectX();
            }
        }

        if ((position.z + this.radius) >= walls[WALL_FRONT].limit_z) {
            position.z = walls[WALL_FRONT].mesh.position.z - 10/2 - this.radius;
            this.setPosition(position);
            this.wallReflectZ();
        }
    }

    wallReflectZ() {
        let v = this.getVelocityVector();
        v.z = -v.z;
        this.setVelocityVector(v);
    }

    wallReflectX() {
        let v = this.getVelocityVector();
        v.x = -v.x;
        this.setVelocityVector(v);
    }

    separate(ball) {
        let x1 = this.getPosition();
        let x2 = ball.getPosition();

        let distance = x1.clone().sub(x2);
        distance.normalize();
        distance.multiplyScalar(this.radius*2);

        this.setPosition(x2.add(distance));
    }

    setPosition(pos) {
        this.mesh.position.x = pos.x;
        this.mesh.position.y = pos.y;
        this.mesh.position.z = pos.z;
    }

    getMesh() {
        return this.mesh;
    }

    scale(flag) {
        if(flag) {
            this.axis.scale.set(10,10,10)
        } else {
            this.axis.scale.set(0.1,0.1,0.1)
        }
    }

    remove() {
        this.ball.geometry.dispose()
        this.ball.material.dispose()
    }
}

function createCannonBall(color) {
    let ballGeometry = new THREE.SphereGeometry(5, 20, 20);
    let material = new THREE.MeshBasicMaterial( {color: color})
    return new THREE.Mesh(ballGeometry, material);
}

function createPivotPoint() {
    let pivotPoint = new THREE.Object3D();
    return pivotPoint;
}

