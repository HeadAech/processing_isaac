import processing.core.PApplet;
import processing.core.PShape;

public class Rock {

    PApplet p;

    PShape sprite;

    public Transform transform = new Transform();

    // Cooldown variables
    public int attackCooldown = 1000; // cooldown in milliseconds (1 second)
    public int lastAttackTime = 0;    // stores the time of the last attack


    public Rock(PApplet p, Vector2 position, Vector2 scale) {
        this.p = p;
        transform.position = position;
        transform.scale = scale;
        this.loadSprite();
    }

    public void loadSprite() {
        sprite = p.loadShape("data/rock.svg");
        int r = (int) p.random(0, 1);
        if (r == 0) {
            sprite.getChild("full").setVisible(false);
            sprite.getChild("broken").setVisible(true);
        } else {
            sprite.getChild("full").setVisible(true);
            sprite.getChild("broken").setVisible(false);
        }

    }

    public void _display() {
        p.pushMatrix();
        p.translate(transform.position.x, transform.position.y);
        p.scale(transform.scale.x, transform.scale.y);
        p.shape(sprite);

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
}
