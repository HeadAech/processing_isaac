import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

public class Entity {

    PApplet p;

    public Transform transform = new Transform();

    public float health = 100;
    public float speed = 200;


    public float damage = 10;
    public float shotSpeed = 2;

    Vector2 acceleration = new Vector2(0, 0);
    float deAcceleration = 0.53f;

    Vector2 targetPosition;

    Vector2 velocity = new Vector2(0, 0);

    float smoothing = 0.88f;

    int facing = 1;

    String spritePath;

    PImage sprite;

    boolean alive = true;

    boolean canGoOutOfBounds = true;

    // Tint variables
    boolean isTinted = false;
    int tintDuration = 200;  // Duration in milliseconds
    int tintStartTime;
    int originalFillColor;

    boolean showCollider = true;

    CollisionShape collisionShape;

    Input input;

    public Entity(PApplet p, String spritePath) {
        this.p = p;
        this.targetPosition = transform.position;
        this.spritePath = spritePath;
        this.sprite = p.loadImage(spritePath);
//        loadSprite();
    }

    public Entity(PApplet p, Vector2 position, String spritePath) {
        this.p = p;
        this.transform.position = position;
        this.targetPosition = position;
        this.spritePath = spritePath;
        sprite = p.loadImage(spritePath);
//        loadSprite();
    }

    public void setInput(Input input) {
        this.input = input;
    }

    private void loadSprite() {
        sprite = p.loadImage(spritePath);
    }

    public void setScale(float scale) {
        this.transform.scale.x = scale;
        this.transform.scale.y = scale;
    }

    public void createCollisionShape() {
        Vector2 size = new Vector2(sprite.width * transform.scale.x, sprite.height * transform.scale.y);
//        Vector2 size = new Vector2(300, 300);
        Vector2 pos = new Vector2(transform.position.x - size.x/2, transform.position.y - size.y/2);
        this.collisionShape = new CollisionShape(pos, size);

    }

    public void setDrawCollider(boolean drawCollider) {
        this.showCollider = drawCollider;
    }

    public void _update(float deltaTime) {
        input();
        velocity.x += acceleration.x * speed;
        velocity.y += acceleration.y * speed;

        velocity.x *= smoothing;
        velocity.y *= smoothing;

//        p.println(velocity.x, velocity.y);
        // Smoothly move towards the target position
//        transform.position = transform.position.plus((targetPosition.minus(transform.position)).multiply(smoothing));

        this.transform.position.x += velocity.x * deltaTime + 0.5f * acceleration.x * deltaTime * deltaTime;
        this.transform.position.y += velocity.y * deltaTime + 0.5f * acceleration.y * deltaTime * deltaTime;

        if (!canGoOutOfBounds) {
            // Boundary checks
            float halfWidth = sprite.width * transform.scale.x / 2;
            float halfHeight = sprite.height * transform.scale.y / 2;

            // Restrict player's position within the window boundaries
            transform.position.x = p.constrain(transform.position.x, halfWidth, p.width - halfWidth);
            transform.position.y = p.constrain(transform.position.y, halfHeight, p.height - halfHeight);

        }
        Vector2 size = new Vector2(sprite.width * transform.scale.x, sprite.height * transform.scale.y);
        collisionShape.setPosition(new Vector2(transform.position.x - size.x/2, transform.position.y - size.y/2));

        _display();
        acceleration.x = 0;
        acceleration.y = 0;
    }

    public void input() {
        if (input == null) return;

        if (input.getPressed('w')) {
            this.acceleration.y -= deAcceleration;
        }

        if (input.getPressed('s')) {
            this.acceleration.y += deAcceleration;
        }

        if (input.getPressed('a')) {
            this.acceleration.x -= deAcceleration;
        }

        if (input.getPressed('d')) {
            this.acceleration.x += deAcceleration;
        }


    }

    public void move() {
        // Calculate the new target position based on direction and speed
//        targetPosition.x += direction.x * speed;
//        targetPosition.y += direction.y * speed;

//        p.println("pos: ", transform.position.x, transform.position.y,  " target: ", targetPosition.x, targetPosition.y,  " speed: ", speed);
    }

    public void _display() {
        p.pushMatrix();

        if (velocity.x > 0) facing = -1;
        else if (velocity.x < 0) facing = 1;

//        p.translate(transform.position.x, transform.position.y);
//        p.scale(transform.scale.x * facing, transform.scale.y);

        // Get the bounding box dimensions of the sprite
        float spriteWidth = sprite.width;
        float spriteHeight = sprite.height;

        // Move the origin to the center of the sprite
//        p.translate(-spriteWidth / 2, -spriteHeight / 2);

//        p.imageMode(PApplet.CENTER);
        p.image(sprite, transform.position.x, transform.position.y, spriteWidth * transform.scale.x, spriteHeight * transform.scale.y);
        p.popMatrix();

    }

    public void applyTintOverlay() {
        p.fill(255, 0, 0, 100);  // Semi-transparent red
        p.noStroke();
        float overlayWidth = sprite.width * transform.scale.x;
        float overlayHeight = sprite.height * transform.scale.y;
        p.rect(-overlayWidth / 2, -overlayHeight / 2, overlayWidth, overlayHeight);
    }


    public void drawCollider() {
        p.pushMatrix();

        p.noFill();
        p.stroke(255,0,0);
        p.rect(collisionShape.left, collisionShape.top,collisionShape.size.x, collisionShape.size.y);
        p.noStroke();
        p.popMatrix();
    }

    public boolean isColliding(Entity other) {
        // Calculate the collider's dimensions based on the scaled sprite of this entity
        float colliderWidth = this.sprite.width * this.transform.scale.x;
        float colliderHeight = this.sprite.height * this.transform.scale.y;

        // Calculate the edges of this entity's collider
        float thisLeft = this.transform.position.x - colliderWidth / 2;
        float thisRight = this.transform.position.x + colliderWidth / 2;
        float thisTop = this.transform.position.y - colliderHeight / 2;
        float thisBottom = this.transform.position.y + colliderHeight / 2;

        // Calculate the collider's dimensions for the other entity
        float otherColliderWidth = other.sprite.width * other.transform.scale.x;
        float otherColliderHeight = other.sprite.height * other.transform.scale.y;

        // Calculate the edges of the other entity's collider
        float otherLeft = other.transform.position.x - otherColliderWidth / 2;
        float otherRight = other.transform.position.x + otherColliderWidth / 2;
        float otherTop = other.transform.position.y - otherColliderHeight / 2;
        float otherBottom = other.transform.position.y + otherColliderHeight / 2;

        // Check for overlap on both the x and y axes
        boolean xOverlap = thisLeft < otherRight && thisRight > otherLeft;
        boolean yOverlap = thisTop < otherBottom && thisBottom > otherTop;

        // If both x and y axes overlap, then there's a collision
        if (xOverlap && yOverlap) {
            return true;
        } else {
            return false;
        }
    }

    public void damage(float damage) {
        isTinted = true;
        tintStartTime = p.millis();
        health -= damage;
        p.println("DAMAGED:", health);
        if (health <= 0) {
            die();
        }
    }

    public void die() {
        health = 0;
        alive = false;
        p.println("DEAD");
    }
}
