import processing.core.PApplet;
import processing.core.PImage;
import processing.sound.SoundFile;

import java.util.ArrayList;
import java.util.UUID;

public class Entity {

    PApplet p;

    ArrayList<SoundFile> hurtSounds = new ArrayList<>();
    ArrayList<SoundFile> deathSounds = new ArrayList<>();

    public Transform transform = new Transform();


    Vector2 acceleration = new Vector2(0, 0);
    float deAcceleration = 0.53f;

    Vector2 targetPosition;

    Vector2 velocity = new Vector2(0, 0);

    float smoothing = 0.88f;

    int facing = 1;

    String spritePath;

    PImage spriteTop;
    PImage spriteBottom;

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

    Animator animatorBottom = new Animator(p);
    Animator animatorTop = new Animator(p);

    //Stats

    public float health = 100;
    public float maxHealth = 100;

    public float speed = 1;
    public float firerate = 2.73f;
    public float damage = 1;
    public float range = 7.50f;
    public float shotSpeed = 2;
    public float luck = 0.0f;

    public Entity(PApplet p, String spritePath) {
        this.p = p;
        this.targetPosition = transform.position;
        this.spritePath = spritePath;
        this.spriteBottom = p.loadImage(spritePath).get(0, 1 * 32, 32, 32);
        this.maxHealth = health;
//        loadSprite();
        onProjectileHit();
    }

    public Entity(PApplet p, Vector2 position, String spritePath) {
        this.p = p;
        this.transform.position = position;
        this.targetPosition = position;
        this.spritePath = spritePath;
        this.maxHealth = health;
        this.spriteBottom = p.loadImage(spritePath).get(0, 1 * 32, 32, 32);
        this.spriteTop = p.loadImage(spritePath).get(0, 0 * 32, 32, 32);

        hurtSounds.add(new SoundFile(p, "data/sfx/isaac_hurt_1.wav"));
        hurtSounds.add(new SoundFile(p, "data/sfx/isaac_hurt_2.wav"));
        hurtSounds.add(new SoundFile(p, "data/sfx/isaac_hurt_3.wav"));

        deathSounds.add(new SoundFile(p, "data/sfx/isaac_death_1.wav"));
        deathSounds.add(new SoundFile(p, "data/sfx/isaac_death_2.wav"));
        deathSounds.add(new SoundFile(p, "data/sfx/isaac_death_3.wav"));
//        loadSprite();
        onProjectileHit();
    }

    public void onProjectileHit() {
        Signals.DamageUUID.connect(data -> {
            UUID uuid = data.uuid;
            float damage = data.damage;

            if (collisionShape == null) return;

            if (!collisionShape.uuid.equals(uuid)) return;

            this.damage(damage);
        });
    }

    public void setInput(Input input) {
        this.input = input;
    }

    private void loadSprite() {
        spriteBottom = p.loadImage(spritePath);
    }

    public void setScale(float scale) {
        this.transform.scale.x = scale;
        this.transform.scale.y = scale;
    }

    public void createCollisionShape() {
        Vector2 size = new Vector2(spriteBottom.width * transform.scale.x, spriteBottom.height * transform.scale.y);
//        Vector2 size = new Vector2(300, 300);
        Vector2 pos = new Vector2(transform.position.x - size.x/2, transform.position.y - size.y/2);
        this.collisionShape = new CollisionShape(pos, size);

    }

    public void setDrawCollider(boolean drawCollider) {
        this.showCollider = drawCollider;
    }

    public void updateAnimationSpriteTop(PImage frame) {
        this.spriteTop = frame;
    }

    public void updateAnimationSpriteBottom(PImage frame) {
        this.spriteBottom = frame;
    }

    public Vector2 getDirection() {
        // Calculate the magnitude of the velocity vector
        float magnitude = (float) Math.sqrt(velocity.x * velocity.x + velocity.y * velocity.y);

        // If the magnitude is zero, the entity is not moving
        if (magnitude == 0) {
            return new Vector2(0, 0); // No movement
        }

        // Normalize the velocity vector
        float dirX = velocity.x / magnitude;
        float dirY = velocity.y / magnitude;

        return new Vector2(dirX, dirY); // Return the direction as a unit vector
    }

    public void _update(float deltaTime) {
        animatorBottom.update(deltaTime);
        animatorTop.update(deltaTime);

        //animation for bottom sprite (legs)
        if (animatorBottom != null) {
            if (animatorBottom.getCurrentAnimation().frame != null) {
                updateAnimationSpriteBottom(animatorBottom.getCurrentAnimation().frame);
            }
        }



        //animation for top sprite (head)
        if (animatorTop != null) {
            if (animatorTop.getCurrentAnimation() != null) {
                if (animatorTop.getCurrentAnimation().frame != null) {
                    updateAnimationSpriteTop(animatorTop.getCurrentAnimation().frame);
                }
            }
        }

        input();
        velocity.x += acceleration.x * (speed * 80);
        velocity.y += acceleration.y * (speed * 80);

        velocity.x *= smoothing;
        velocity.y *= smoothing;

        if (Math.abs(velocity.x) > 0 && Math.abs(velocity.x) < 5)
            velocity.x = 0;
        if (Math.abs(velocity.y) > 0 && Math.abs(velocity.y) < 5)
            velocity.y = 0;


//        p.println(velocity.x, velocity.y);
        // Smoothly move towards the target position
//        transform.position = transform.position.plus((targetPosition.minus(transform.position)).multiply(smoothing));

        this.transform.position.x += velocity.x * deltaTime + 0.5f * acceleration.x * deltaTime * deltaTime;
        this.transform.position.y += velocity.y * deltaTime + 0.5f * acceleration.y * deltaTime * deltaTime;

        if (!canGoOutOfBounds) {
            // Boundary checks
            float halfWidth = spriteBottom.width * transform.scale.x / 2;
            float halfHeight = spriteBottom.height * transform.scale.y / 2;

            // Restrict player's position within the window boundaries
            transform.position.x = p.constrain(transform.position.x, halfWidth, p.width - halfWidth);
            transform.position.y = p.constrain(transform.position.y, halfHeight, p.height - halfHeight);

        }
        Vector2 size = new Vector2(spriteBottom.width * transform.scale.x, spriteBottom.height * transform.scale.y);
        collisionShape.setPosition(new Vector2(transform.position.x - size.x/2, transform.position.y - size.y/2));

        acceleration.x = 0;
        acceleration.y = 0;
    }

    public void input() {
        if (input == null) return;
        if (!alive) return;

        if (input.getPressed("w")) {
            this.acceleration.y -= deAcceleration;
        }

        if (input.getPressed("s")) {
            this.acceleration.y += deAcceleration;
        }

        if (input.getPressed("a")) {
            this.acceleration.x -= deAcceleration;

        }

        if (input.getPressed("d")) {
            this.acceleration.x += deAcceleration;
        }

        if (!animatorTop.getAnimation("hurt").isPlaying()) {
            if (input.getPressed("up")) {
                animatorTop.playAnimationIfNotPlaying("shootUp");
            } else if (input.getPressed("down")) {
                animatorTop.playAnimationIfNotPlaying("shootDown");
            } else if (input.getPressed("left")) {
                animatorTop.playAnimationIfNotPlaying("shootLeft");
            } else if (input.getPressed("right")) {
                animatorTop.playAnimationIfNotPlaying("shootRight");
            } else {
                animatorTop.playAnimationIfNotPlaying("idle");
            }
        }


    }

    public void move() {
        // Calculate the new target position based on direction and speed
//        targetPosition.x += direction.x * speed;
//        targetPosition.y += direction.y * speed;

//        p.println("pos: ", transform.position.x, transform.position.y,  " target: ", targetPosition.x, targetPosition.y,  " speed: ", speed);
    }

    public void _display() {


    }

    public void applyTintOverlay() {
        p.fill(255, 0, 0, 100);  // Semi-transparent red
        p.noStroke();
        float overlayWidth = spriteBottom.width * transform.scale.x;
        float overlayHeight = spriteBottom.height * transform.scale.y;
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
        float colliderWidth = this.spriteBottom.width * this.transform.scale.x;
        float colliderHeight = this.spriteBottom.height * this.transform.scale.y;

        // Calculate the edges of this entity's collider
        float thisLeft = this.transform.position.x - colliderWidth / 2;
        float thisRight = this.transform.position.x + colliderWidth / 2;
        float thisTop = this.transform.position.y - colliderHeight / 2;
        float thisBottom = this.transform.position.y + colliderHeight / 2;

        // Calculate the collider's dimensions for the other entity
        float otherColliderWidth = other.spriteBottom.width * other.transform.scale.x;
        float otherColliderHeight = other.spriteBottom.height * other.transform.scale.y;

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

    public void resetVelocity() {
        velocity.x = 0;
        velocity.y = 0;
    }

}
