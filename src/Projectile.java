import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

import java.util.UUID;

public class Projectile {

    UUID uuid = UUID.randomUUID();

    PApplet p;

    Transform transform = new Transform();

    Vector2 direction = new Vector2(0, 0);

    private float damage = 10;
    private float shotSpeed = 5.0f;

    int spawnTime = 0;
    float lifeSpan = 2000;

    int destroyStart = 0;
    float destroyEnd = 300;

    PImage sprite;
    PImage spritesheet;

    Vector2 frameSize = new Vector2(32, 32);

    CollisionShape collisionShape;

    public Projectile(PApplet p, Vector2 startPosition, Vector2 direction) {
        this.p = p;
        this.transform.position = startPosition;
        this.direction = direction;
        loadSprite();
        createCollisionShape();

    }

    public Projectile(PApplet p, Vector2 startPosition, Vector2 direction, float damage, float shotSpeed) {
        this.p = p;
        this.transform.position = startPosition;
        this.direction = direction;
        this.damage = damage;
        this.shotSpeed = shotSpeed;
        loadSprite();
        createCollisionShape();
    }

    public void createCollisionShape() {
        Vector2 size = new Vector2(sprite.width * transform.scale.x, sprite.height * transform.scale.y);
//        Vector2 size = new Vector2(300, 300);
        Vector2 pos = new Vector2(transform.position.x - size.x/2, transform.position.y - size.y/2);
        this.collisionShape = new CollisionShape(pos, size);

    }

    private PImage getTile(int x, int y) {
        int sx = (int) (x * frameSize.x);
        int sy = (int) (y * frameSize.y);
        return spritesheet.get(sx, sy, (int) frameSize.x, (int) frameSize.y);
    }

    private void loadSprite() {
        p.imageMode(PApplet.CENTER);
        spritesheet = p.loadImage("data/sprites/spritesheet/tears_balloon.png");
        sprite = getTile(4, 1);
//        this.sprite = spritesheet.get();
        this.spawnTime = p.millis();
    }

    public void _update() {
        this.transform.position = this.transform.position.plus(this.direction.multiply(shotSpeed));
        this.collisionShape.setPosition(transform.position);
        _display();
    }

    public void _display() {
        p.pushMatrix();

        p.image(sprite, transform.position.x + sprite.width/2, transform.position.y + sprite.height/2);

        p.popMatrix();
//        drawCollider();
        drawCollider();

    }

    public void drawCollider() {
        p.pushMatrix();

        p.noFill();
        p.stroke(255,0,0);
        p.rect(collisionShape.left, collisionShape.top, collisionShape.size.x, collisionShape.size.y);
        p.noStroke();
        p.popMatrix();
    }



    public void stopMoving() {
        shotSpeed = 0;
    }

    public boolean checkIfValid() {
        // Check if the projectile has exceeded its lifespan
        return p.millis() - spawnTime < lifeSpan;
    }

    public void onDestroy() {
//        sprite.getChild("body").setVisible(false);
//        sprite.getChild("splash").setVisible(true);
    }

}
