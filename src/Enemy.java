import processing.core.PApplet;

public class Enemy extends Entity {

    Entity player;

    // Cooldown variables
    private int attackCooldown = 1000; // cooldown in milliseconds (1 second)
    private int lastAttackTime = 0;    // stores the time of the last attack


    public Enemy(PApplet p, Entity player) {
        super(p);
        this.player = player;
        setEnemySprite();
        enemyStats();
    }

    public Enemy(PApplet p, Vector2 position, Vector2 playerPosition) {
        super(p, position);
        setEnemySprite();
        enemyStats();
    }

    public Enemy(PApplet p, Vector2 position, Entity player) {
        super(p, position);
        this.player = player;
        setEnemySprite();
        enemyStats();
    }

    private void setEnemySprite() {
        sprite.getChild("body").setVisible(true);
        sprite.getChild("hat").setVisible(false);
    }

    private void enemyStats() {
        super.damage = 5;
        super.speed = 3;
    }

    @Override
    public void _update() {
        super.direction = player.transform.position.minus(transform.position).normalized();
        isCollidingWithPlayer();
        super._update();
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

}
