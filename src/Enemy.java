import processing.core.PApplet;
import processing.core.PImage;

enum EnemyType {
    DIP, SPIDER, POOTER
}

public class Enemy extends Entity implements Cloneable {

    Entity player;

    // Cooldown variables
    private float attackCooldown = 2; // cooldown in milliseconds (1 second)
    private float lastAttackTime = 0;    // stores the time of the last attack

    private float projectileCooldown = 2;
    private float lastProjectileTime = 0;

    private float moveCooldown = 1.7f;
    private float lastMoveTime = 0;

    EnemyType type = EnemyType.DIP;
    float launchSpeed = 10f;

    float minMoveCooldown = 1.4f;
    float maxMoveCooldown = 2.2f;



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

    public Enemy(PApplet p, Vector2 position, Entity player, String spritePath, EnemyType type) {
        super(p, position, spritePath);
        this.player = player;
        this.type = type;
        setEnemySprite();
        enemyStats();
        createCollisionShape();
    }

    public Enemy(Enemy enemy, EnemyType type) {
        super(enemy.p, new Vector2(enemy.transform.position.x, enemy.transform.position.y), enemy.spritePath);
        this.player = enemy.player;
        this.type = type;
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
        switch (type) {
            case DIP:
                super.smoothing = 0.95f;
                launchSpeed = 9f;
                minMoveCooldown = 1.4f;
                maxMoveCooldown = 2.2f;
                break;
            case SPIDER:
                super.smoothing = 0.70f;
                minMoveCooldown= 1.4f;
                maxMoveCooldown = 1.8f;
                break;
            case POOTER:
                super.smoothing = 0.75f;
                super.speed = 0.2f;
                super.flying = true;
                break;
            default:
                break;
        }
        moveCooldown = maxMoveCooldown;
    }

    private void move() {
        lastMoveTime = 0;
        moveCooldown = p.random(minMoveCooldown, maxMoveCooldown);
        super.acceleration.x =  p.random(-1, 1) * launchSpeed;
        super.acceleration.y =  p.random(-1, 1) * launchSpeed;
    }

    private void shootProjectile() {
        Vector2 direction = new Vector2(0, 0);
        direction.x = player.transform.position.x - super.transform.position.x;
        direction.y = player.transform.position.y - super.transform.position.y;
        direction = direction.normalized();

        System.out.println("SHOOT ");

        Projectile p = new Projectile(super.p, new Vector2(super.transform.position.x - (float) super.spriteBottom.width /2, super.transform.position.y - (float) super.spriteBottom.height /2), direction);
        p.damage = 1;
        p.canDamagePlayer = true;
        Signals.PlaySound.emit("tear_fire");
        Signals.CreateProjectile.emit(p);
    }

    @Override
    public void _update(float deltaTime) {
        if (type == EnemyType.DIP || type == EnemyType.SPIDER) {
            lastMoveTime += deltaTime;
            if (lastMoveTime >= moveCooldown) {
                move();
            }
        }

        if (type == EnemyType.POOTER) {
            super.acceleration.x = (super.transform.position.x - player.transform.position.x);
            super.acceleration.y = (super.transform.position.y - player.transform.position.y);
            super.acceleration = super.acceleration.normalized().multiply(-1);

            lastProjectileTime += deltaTime;

            if (projectileCooldown - lastProjectileTime < 0.2f){
                animatorBottom.playAnimationIfNotPlaying("fire");
            }
            if (lastProjectileTime >= projectileCooldown) {
                lastProjectileTime = 0;
                shootProjectile();
            }
            if (lastProjectileTime < projectileCooldown) {

                if (!animatorBottom.getAnimation("fire").isPlaying())
                    animatorBottom.playAnimationIfNotPlaying("idle");
            }
        }


        if (player.alive) {
            isCollidingWithPlayer();
            super._update(deltaTime);
        }
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
