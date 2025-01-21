import processing.core.PApplet;
import processing.core.PShape;

public class Entity {

    PApplet p;

    public Transform transform = new Transform();

    public float health = 100;
    public float speed = 9;


    public float damage = 10;
    public float shotSpeed = 2;

    Vector2 direction = new Vector2(0, 0);
    Vector2 targetPosition;
    float smoothing = 0.2f;

    int facing = 1;

    PShape sprite;

    boolean alive = true;

    boolean canGoOutOfBounds = true;

    // Tint variables
    boolean isTinted = false;
    int tintDuration = 200;  // Duration in milliseconds
    int tintStartTime;
    int originalFillColor;

    boolean showCollider = false;

    public Entity(PApplet p) {
        this.p = p;
        this.targetPosition = transform.position;
        loadSprite();
    }

    public Entity(PApplet p, Vector2 position) {
        this.p = p;
        this.transform.position = position;
        this.targetPosition = position;
        loadSprite();
    }

    private void loadSprite() {
        sprite = p.loadShape("data/human.svg");
    }

    public void setScale(float scale) {
        this.transform.scale.x = scale;
        this.transform.scale.y = scale;
    }

    public void setDrawCollider(boolean drawCollider) {
        this.showCollider = drawCollider;
    }

    public void _update() {

        // Smoothly move towards the target position
        transform.position = transform.position.plus((targetPosition.minus(transform.position)).multiply(smoothing));

        if (!canGoOutOfBounds) {
            // Boundary checks
            float halfWidth = sprite.getWidth() * transform.scale.x / 2;
            float halfHeight = sprite.getHeight() * transform.scale.y / 2;

            // Restrict player's position within the window boundaries
            transform.position.x = p.constrain(transform.position.x, halfWidth, p.width - halfWidth);
            transform.position.y = p.constrain(transform.position.y, halfHeight, p.height - halfHeight);

        }
        move();

        _display();
    }

    public void move() {
        // Calculate the new target position based on direction and speed
        targetPosition.x += direction.x * speed;
        targetPosition.y += direction.y * speed;

//        p.println("pos: ", transform.position.x, transform.position.y,  " target: ", targetPosition.x, targetPosition.y,  " speed: ", speed);
    }

    public void _display() {
        p.pushMatrix();

        if (direction.x == 1) facing = -1;
        else if (direction.x == -1) facing = 1;

        p.translate(transform.position.x, transform.position.y);
        p.scale(transform.scale.x * facing, transform.scale.y);

        // Get the bounding box dimensions of the sprite
        float spriteWidth = sprite.getWidth();
        float spriteHeight = sprite.getHeight();

        // Move the origin to the center of the sprite
        p.translate(-spriteWidth / 2, -spriteHeight / 2);

        // Apply tint overlay if damaged
        if (isTinted) {
            if (p.millis() - tintStartTime > tintDuration) {
                isTinted = false;  // Turn off the tint after duration
            } else {
                applyTintOverlay();
            }
        }


        p.shape(sprite);
//        p.tint(0);
        p.popMatrix();

        if (showCollider) {
            drawCollider();

        }
    }

    public void applyTintOverlay() {
        p.fill(255, 0, 0, 100);  // Semi-transparent red
        p.noStroke();
        float overlayWidth = sprite.getWidth() * transform.scale.x;
        float overlayHeight = sprite.getHeight() * transform.scale.y;
        p.rect(-overlayWidth / 2, -overlayHeight / 2, overlayWidth, overlayHeight);
    }


    private void drawCollider() {
        p.pushMatrix();

        // Move to the player's position
        p.translate(transform.position.x, transform.position.y);

        // Get sprite dimensions
        float colliderWidth = sprite.getWidth() * transform.scale.x;
        float colliderHeight = sprite.getHeight() * transform.scale.y;

        // Set the collider to be centered on the player
        p.translate(-colliderWidth / 2, -colliderHeight / 2);

        // Draw the rectangle representing the collider
        p.noFill();
        p.stroke(255, 0, 0); // Red color for visibility
        p.rect(0, 0, colliderWidth, colliderHeight);

        p.popMatrix();
    }

    public boolean isColliding(Entity other) {
        // Calculate the collider's dimensions based on the scaled sprite of this entity
        float colliderWidth = this.sprite.getWidth() * this.transform.scale.x;
        float colliderHeight = this.sprite.getHeight() * this.transform.scale.y;

        // Calculate the edges of this entity's collider
        float thisLeft = this.transform.position.x - colliderWidth / 2;
        float thisRight = this.transform.position.x + colliderWidth / 2;
        float thisTop = this.transform.position.y - colliderHeight / 2;
        float thisBottom = this.transform.position.y + colliderHeight / 2;

        // Calculate the collider's dimensions for the other entity
        float otherColliderWidth = other.sprite.getWidth() * other.transform.scale.x;
        float otherColliderHeight = other.sprite.getHeight() * other.transform.scale.y;

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
