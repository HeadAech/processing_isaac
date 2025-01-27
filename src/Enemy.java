import processing.core.PApplet;
import processing.core.PImage;



public class Enemy extends Entity implements Cloneable {

    Entity player;

    // Cooldown variables
    private int attackCooldown = 1000; // cooldown in milliseconds (1 second)
    private int lastAttackTime = 0;    // stores the time of the last attack

    private float moveCooldown = 1.7f;
    private float lastMoveTime = 0;


    public Enemy(PApplet p, Entity player, String spritePath) {
        super(p, spritePath);
        this.player = player;
        setEnemySprite();
        enemyStats();
    }

    public Enemy(PApplet p, Vector2 position, Vector2 playerPosition, String spritePath) {
        super(p, position, spritePath);
        setEnemySprite();
        enemyStats();
        createCollisionShape();
    }

    public Enemy(PApplet p, Vector2 position, Entity player, String spritePath) {
        super(p, position, spritePath);
        this.player = player;
        setEnemySprite();
        enemyStats();
        createCollisionShape();
    }

    public Enemy(Enemy enemy) {
        super(enemy.p, new Vector2(enemy.transform.position.x, enemy.transform.position.y), enemy.spritePath);
        this.player = enemy.player;
//        super.animatorTop = new Animator(p);
//        for (String key: enemy.animatorTop.animations.keySet()) {
//            Animation animation = new Animation(p, enemy.animatorTop.getAnimation(key).spritesheet, super.spriteTop);
//            animation.setPlayStyle(enemy.animatorTop.getAnimation(key).playStyle);
//            for (Vector2 frame: enemy.animatorTop.getAnimation(key).framePositions) {
//                animation.addFrame(new Vector2(frame.x, frame.y));
//            }
//            animation.setDuration(enemy.animatorTop.getAnimation(key).duration);
//            super.animatorTop.addAnimation(key, animation);
//        }
        super.animatorBottom = new Animator(p);
        for (String key: enemy.animatorBottom.animations.keySet()) {
            Animation animation = new Animation(p, enemy.animatorBottom.getAnimation(key).spritesheet, super.spriteBottom);
            animation.setPlayStyle(enemy.animatorBottom.getAnimation(key).playStyle);
            for (Vector2 frame: enemy.animatorBottom.getAnimation(key).framePositions) {
                animation.addFrame(new Vector2(frame.x, frame.y));
            }
            animation.setDuration(enemy.animatorBottom.getAnimation(key).duration);
            super.animatorBottom.addAnimation(key, animation);
        }
        super.animatorBottom.playAnimation("idle");
        this.health = enemy.health;
        setEnemySprite();
        enemyStats();
        createCollisionShape();
    }

    private void setEnemySprite() {
        PImage spritesheet = p.loadImage(this.spritePath);

    }

    private void enemyStats() {
//        super.damage = 5;
//        super.speed = 3;
        super.smoothing = 0.95f;
    }

    float launchSpeed = 10f;
    @Override
    public void _update(float deltaTime) {
        lastMoveTime += deltaTime;
        if (lastMoveTime >= moveCooldown) {
            lastMoveTime = 0;
            moveCooldown = p.random(1.4f, 2.2f);
            super.acceleration.x =  p.random(-1, 1) * launchSpeed;
            super.acceleration.y =  p.random(-1, 1) * launchSpeed;
        }
//        super.acceleration = player.transform.position.minus(transform.position).normalized();
        isCollidingWithPlayer();
        super._update(deltaTime);
    }

    @Override
    public void _display() {
        if (!alive) return;
        p.pushMatrix();

        if (velocity.x > 0) facing = -1;
        else if (velocity.x < 0) facing = 1;

        // Get the bounding box dimensions of the sprite
        float spriteWidth = spriteBottom.width;
        float spriteHeight = spriteBottom.height;

        p.imageMode(PApplet.CENTER);

        p.image(spriteBottom, transform.position.x, transform.position.y, spriteBottom.width * transform.scale.x, spriteBottom.height * transform.scale.y);
        p.popMatrix();
        super._display();
    }

    public void isCollidingWithPlayer() {
        if (isColliding(player)) {
            int currentTime = p.millis();

            // Check if enough time has passed since the last attack
            if (currentTime - lastAttackTime >= attackCooldown) {
                player.damage(this.damage);
                lastAttackTime = currentTime; // Update last attack time
            }
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
