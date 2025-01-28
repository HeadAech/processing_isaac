import jogamp.graph.font.typecast.ot.table.ID;
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;

enum EnemyType {
    DIP, SPIDER, POOTER,
    //bosses
    DINGLE
}

public class Enemy extends Entity implements Cloneable {

    Entity player;

    // Cooldown variables
    private float attackCooldown = 2; // cooldown in milliseconds (1 second)
    private float lastAttackTime = 0;    // stores the time of the last attack

    private float projectileCooldown = 2;
    private float lastProjectileTime = 0;

    private float moveCooldown = 0.4f;
    private float lastMoveTime = 0;

    EnemyType type = EnemyType.DIP;
    float launchSpeed = 10f;

    float minMoveCooldown = 1.4f;
    float maxMoveCooldown = 2.2f;

    private float actionCooldown = 1.6f;
    private float lastActionTime = 0;

    private float deathCooldown = 1.4f;
    private float deathTime = 0;

    boolean isBoss = false;

    enum DinglePhase {
        IDLE, CHARGING, EXHAUSTED, SHOOT, SPAWN_DIPS, DYING
    }

    private DinglePhase dinglePhase = DinglePhase.IDLE;

    ArrayList<DinglePhase> actions = new ArrayList<>() {{
       add(DinglePhase.CHARGING);
       add(DinglePhase.SHOOT);
       add(DinglePhase.SPAWN_DIPS);
    }};

    private int dingleMaxCharges = 3;
    private int dingleCurrentCharges = 0;

    private float exhaustedCooldown = 3;
    private float lastExhaustedTime = 0;

    private float dingleSpawnCooldown = 0.8f;
    private float lastDingleSpawnTime = 0;

    private float dingleShootCooldown = 1f;
    private float lastDingleShootTime = 0;


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
            animation.frameSize = enemy.animatorBottom.getAnimation(key).frameSize;
            for (Vector2 frame: enemy.animatorBottom.getAnimation(key).framePositions) {
                animation.addFrame(new Vector2(frame.x, frame.y));
            }
            animation.setDuration(enemy.animatorBottom.getAnimation(key).duration);
            super.animatorBottom.addAnimation(key, animation);
        }
        super.animatorBottom.playAnimation("idle");
        this.health = enemy.health;
        this.isBoss = enemy.isBoss;
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
                minMoveCooldown = 1.4f;
                maxMoveCooldown = 1.8f;
                break;
            case POOTER:
                super.smoothing = 0.75f;
                super.speed = 0.2f;
                super.flying = true;
                break;
            case DINGLE:
                super.smoothing = 0.95f;
                launchSpeed = 15f;
                minMoveCooldown = 1f;
                maxMoveCooldown = 1f;
            default:
                break;
        }
        super.maxHealth = health;
        moveCooldown = maxMoveCooldown;
    }

    private void move() {
        lastMoveTime = 0;
        moveCooldown = p.random(minMoveCooldown, maxMoveCooldown);
        super.acceleration.x =  p.random(-1, 1) * launchSpeed;
        super.acceleration.y =  p.random(-1, 1) * launchSpeed;
    }

    private void chargeAtPlayer() {
        Vector2 direction = new Vector2(0, 0);
        direction.x = player.transform.position.x - super.transform.position.x;
        direction.y = player.transform.position.y - super.transform.position.y;
        direction = direction.normalized();

        super.acceleration.x = direction.x * launchSpeed;
        super.acceleration.y = direction.y * launchSpeed;
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
        p.shotSpeed = 1.9f;
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

        if (type == EnemyType.DINGLE) {
            if (dinglePhase == DinglePhase.IDLE)
                lastActionTime += deltaTime;
            if (dinglePhase == DinglePhase.EXHAUSTED)
                lastExhaustedTime += deltaTime;

            if (dinglePhase == DinglePhase.EXHAUSTED) {
                if (lastExhaustedTime >= exhaustedCooldown) {
                    lastExhaustedTime = 0;
                    dinglePhase = DinglePhase.IDLE;
                    super.animatorBottom.playAnimation("idle");
                }
            }

            if (dinglePhase == DinglePhase.SHOOT) {
                lastDingleShootTime += deltaTime;
                super.animatorBottom.playAnimationIfNotPlaying("shoot");
                if (lastDingleShootTime >= dingleShootCooldown) {
                    lastDingleShootTime = 0;
                    shootProjectile();
                    super.animatorBottom.playAnimation("idle");
                    dinglePhase = DinglePhase.IDLE;
                }
            }

            if (dinglePhase == DinglePhase.SPAWN_DIPS) {
                if (lastDingleSpawnTime == 0) {
                    Signals.PlaySound.emit("whistle");

                }
                lastDingleSpawnTime += deltaTime;
                super.animatorBottom.playAnimationIfNotPlaying("spawn");
                if (lastDingleSpawnTime >= dingleSpawnCooldown) {
                    lastDingleSpawnTime = 0;
                    Signals.SpawnEnemy.emit(new SpawnEnemy(EnemyType.DIP, this.transform.position));
                    dinglePhase = DinglePhase.IDLE;
                    super.animatorBottom.playAnimation("idle");
                }
            }


            if (dinglePhase == DinglePhase.CHARGING) {
                lastMoveTime += deltaTime;
                if (lastMoveTime >= moveCooldown) {
                    if (dingleCurrentCharges == dingleMaxCharges) {
                        dinglePhase = DinglePhase.EXHAUSTED;
                        super.animatorBottom.playAnimation("exhausted");
                        dingleCurrentCharges = 0;
                    } else {
                        dingleCurrentCharges++;
                        super.animatorBottom.playAnimation("charge");
                        Signals.PlaySound.emit("fart");
                        chargeAtPlayer();
                        lastMoveTime = 0;
                        System.out.println("CHARGE");
                    }
                }
            }

            if (lastActionTime >= actionCooldown && dinglePhase != DinglePhase.DYING) {
                int idx = (int) p.random(actions.size());
                dinglePhase = actions.get(idx);
                lastActionTime = 0;
            }

            if (health <= 0) {
                deathTime += deltaTime;
                if (deathTime >= deathCooldown) {
                    if (this.isBoss) {
                        Signals.PlaySound.emit("boss_defeat");
                        Signals.StopMusic.emit(null);
                        Signals.PlayMusic.emit("boss_beaten_theme");
                    }
                    super.die();
                }
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

//        p.pushMatrix();

        if (velocity.x > 0) facing = -1;
        else if (velocity.x < 0) facing = 1;

        p.imageMode(PApplet.CENTER);

        p.image(spriteBottom, transform.position.x, transform.position.y, spriteBottom.width * transform.scale.x, spriteBottom.height * transform.scale.y);
//        p.popMatrix();
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
    public void die() {
        if (type == EnemyType.DINGLE) {
            super.animatorBottom.playAnimation("death");
            dinglePhase = DinglePhase.DYING;
        } else {
            super.die();
        }
//        super.die();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
