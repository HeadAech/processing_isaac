import processing.core.PApplet;
import processing.core.PShape;

public class Projectile {

    PApplet p;

    Transform transform = new Transform();

    Vector2 direction = new Vector2(0, 0);

    private float damage = 10;
    private float shotSpeed = 5.0f;

    int spawnTime = 0;
    float lifeSpan = 2000;

    int destroyStart = 0;
    float destroyEnd = 300;

    private PShape sprite;

    public Projectile(PApplet p, Vector2 startPosition, Vector2 direction) {
        this.p = p;
        this.transform.position = startPosition;
        this.direction = direction;
        loadSprite();
    }

    public Projectile(PApplet p, Vector2 startPosition, Vector2 direction, float damage, float shotSpeed) {
        this.p = p;
        this.transform.position = startPosition;
        this.direction = direction;
        this.damage = damage;
        this.shotSpeed = shotSpeed;
        loadSprite();
    }

    private void loadSprite() {
        sprite = p.loadShape("data/bullet.svg");
        sprite.getChild("splash").setVisible(false);
        sprite.getChild("body").setVisible(true);
        this.spawnTime = p.millis();
    }

    public void _update() {
        if (!sprite.getChild("splash").isVisible())
            this.transform.position = this.transform.position.plus(this.direction.multiply(shotSpeed));

        _display();
    }

    public void _display() {
        p.pushMatrix();

        p.translate(transform.position.x, transform.position.y);
        p.scale(transform.scale.x, transform.scale.y);
        p.translate(-sprite.getWidth() / 2, -sprite.getHeight() / 2);
        p.shape(sprite);

        p.popMatrix();
//        drawCollider();

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

    public void stopMoving() {
        shotSpeed = 0;
    }

    public boolean checkIfValid() {
        // Check if the projectile has exceeded its lifespan
        return p.millis() - spawnTime < lifeSpan;
    }

    public void onDestroy() {
        sprite.getChild("body").setVisible(false);
        sprite.getChild("splash").setVisible(true);
    }

}
