import processing.core.PApplet;
import processing.core.PImage;

public class Player extends Entity {

    public Player(PApplet p, String spritePath) {
        super(p, spritePath);
        setPlayerSprite();
        setPlayerStats();
    }

    public Player(PApplet p, Vector2 position, String spritePath) {
        super(p, position, spritePath);
        setPlayerSprite();
        setPlayerStats();
    }

    private void setPlayerStats() {
        this.damage = 3.50f;
        super.health = 6;
        super.maxHealth = 20;
        super.speed = 1;
        super.firerate = 2.75f;
        super.range = 7.50f;
        super.shotSpeed = 2;
        super.luck = 0.0f;
        canGoOutOfBounds = true;
        PImage spritesheet = p.loadImage("data/sprites/spritesheet/isaac_spritesheet.png");
        //walk top-bottom
        Animation walkTopBottom = new Animation(p, spritesheet, super.spriteBottom);
        walkTopBottom.setPlayStyle(PlayStyle.PS_LOOP);
        walkTopBottom.addFrame(new Vector2(0, 1));
        walkTopBottom.addFrame(new Vector2(1, 1));
        walkTopBottom.addFrame(new Vector2(2, 1));
        walkTopBottom.addFrame(new Vector2(3, 1));
        walkTopBottom.addFrame(new Vector2(4, 1));
        walkTopBottom.addFrame(new Vector2(5, 1));
        walkTopBottom.addFrame(new Vector2(6, 1));
        walkTopBottom.addFrame(new Vector2(7, 1));
        walkTopBottom.addFrame(new Vector2(8, 1));
        walkTopBottom.addFrame(new Vector2(9, 1));
        walkTopBottom.setDuration(0.055f);
        super.animatorBottom.addAnimation("walkTopBottom", walkTopBottom);
//        super.animator.playAnimation("walkTopBottom");

        Animation walkRight = new Animation(p, spritesheet, super.spriteBottom);
        walkRight.setPlayStyle(PlayStyle.PS_LOOP);
        walkRight.addFrame(new Vector2(0, 2));
        walkRight.addFrame(new Vector2(1, 2));
        walkRight.addFrame(new Vector2(2, 2));
        walkRight.addFrame(new Vector2(3, 2));
        walkRight.addFrame(new Vector2(4, 2));
        walkRight.addFrame(new Vector2(5, 2));
        walkRight.addFrame(new Vector2(6, 2));
        walkRight.addFrame(new Vector2(7, 2));
        walkRight.addFrame(new Vector2(8, 2));
        walkRight.addFrame(new Vector2(9, 2));
        walkRight.setDuration(0.055f);
        super.animatorBottom.addAnimation("walkRight", walkRight);

        Animation walkLeft = new Animation(p, spritesheet, super.spriteBottom);
        walkLeft.setPlayStyle(PlayStyle.PS_LOOP);
        walkLeft.addFrame(new Vector2(9, 3));
        walkLeft.addFrame(new Vector2(8, 3));
        walkLeft.addFrame(new Vector2(7, 3));
        walkLeft.addFrame(new Vector2(6, 3));
        walkLeft.addFrame(new Vector2(5, 3));
        walkLeft.addFrame(new Vector2(4, 3));
        walkLeft.addFrame(new Vector2(3, 3));
        walkLeft.addFrame(new Vector2(2, 3));
        walkLeft.addFrame(new Vector2(1, 3));
        walkLeft.addFrame(new Vector2(0, 3));
        walkLeft.setDuration(0.055f);
        super.animatorBottom.addAnimation("walkLeft", walkLeft);

        Animation idle = new Animation(p, spritesheet, super.spriteBottom);
        idle.setPlayStyle(PlayStyle.PS_LOOP);
        idle.addFrame(new Vector2(0, 1));
        idle.setDuration(0.2f);
        super.animatorBottom.addAnimation("idle", idle);
        super.animatorBottom.playAnimation("idle");

        Animation hurt = new Animation(p, spritesheet, super.spriteTop);
        hurt.setPlayStyle(PlayStyle.PS_NORMAL);
        hurt.addFrame(new Vector2(1, 4));
        hurt.setDuration(0.4f);
        super.animatorTop.addAnimation("hurt", hurt);

        Animation death = new Animation(p, spritesheet, super.spriteTop);
        death.setPlayStyle(PlayStyle.PS_NORMAL);
        death.addFrame(new Vector2(0, 4));
        death.addFrame(new Vector2(1, 4));
        death.addFrame(new Vector2(2, 4));
        death.setDuration(0.14f);
        super.animatorTop.addAnimation("death", death);

        Animation topIdle = new Animation(p, spritesheet, super.spriteTop);
        topIdle.setPlayStyle(PlayStyle.PS_LOOP);
        topIdle.addFrame(new Vector2(0, 0));
        topIdle.setDuration(1);
        super.animatorTop.addAnimation("idle", topIdle);

        Animation shootUp = new Animation(p, spritesheet, super.spriteTop);
        shootUp.setPlayStyle(PlayStyle.PS_LOOP);
        shootUp.addFrame(new Vector2(4, 0));
        shootUp.addFrame(new Vector2(5, 0));
        shootUp.setDuration(1/super.firerate);
        super.animatorTop.addAnimation("shootUp", shootUp);

        Animation shootDown = new Animation(p, spritesheet, super.spriteTop);
        shootDown.setPlayStyle(PlayStyle.PS_LOOP);
        shootDown.addFrame(new Vector2(0, 0));
        shootDown.addFrame(new Vector2(1, 0));
        shootDown.setDuration(1/super.firerate);
        super.animatorTop.addAnimation("shootDown", shootDown);
        super.animatorTop.playAnimation("idle");

        Animation shootRight = new Animation(p, spritesheet, super.spriteTop);
        shootRight.setPlayStyle(PlayStyle.PS_LOOP);
        shootRight.addFrame(new Vector2(2, 0));
        shootRight.addFrame(new Vector2(3, 0));
        shootRight.setDuration(1/super.firerate);
        super.animatorTop.addAnimation("shootRight", shootRight);

        Animation shootLeft = new Animation(p, spritesheet, super.spriteTop);
        shootLeft.setPlayStyle(PlayStyle.PS_LOOP);
        shootLeft.addFrame(new Vector2(6, 0));
        shootLeft.addFrame(new Vector2(7, 0));
        shootLeft.setDuration(1/super.firerate);
        super.animatorTop.addAnimation("shootLeft", shootLeft);
    }

    @Override
    public void _update(float deltaTime) {
        Vector2 direction = getDirection();

        if (direction.x == 1) {
            animatorBottom.playAnimationIfNotPlaying("walkRight");
        }
        else if (direction.x == -1) {
            animatorBottom.playAnimationIfNotPlaying("walkLeft");
        }

        if (direction.y == 1 || direction.y == -1) {
            animatorBottom.playAnimationIfNotPlaying("walkTopBottom");
        }

        if (direction.x == 0 && direction.y == 0) {
            animatorBottom.playAnimationIfNotPlaying("idle");
        }
        super._update(deltaTime);
    }

    @Override
    public void _display() {
        p.pushMatrix();

        if (velocity.x > 0) facing = -1;
        else if (velocity.x < 0) facing = 1;

//        p.translate(transform.position.x, transform.position.y);
//        p.scale(transform.scale.x * facing, transform.scale.y);

        // Get the bounding box dimensions of the sprite
        float spriteWidth = spriteBottom.width;
        float spriteHeight = spriteBottom.height;

        // Move the origin to the center of the sprite
//        p.translate(-spriteWidth / 2, -spriteHeight / 2);

        p.imageMode(PApplet.CENTER);
        if (alive && !animatorTop.getAnimation("hurt").isPlaying())
            p.image(spriteBottom, transform.position.x, transform.position.y, spriteWidth * transform.scale.x, spriteHeight * transform.scale.y);
        p.image(spriteTop, transform.position.x, transform.position.y - 7, spriteTop.width * transform.scale.x, spriteTop.height * transform.scale.y);
        p.popMatrix();
        super._display();
    }

    @Override
    public void damage(float damage) {
        if (!alive) return;
        animatorTop.playAnimation("hurt");
        int randomHurt = (int) p.random(hurtSounds.size());
        hurtSounds.get(randomHurt).play();
        super.damage(damage);
    }

    @Override
    public void die() {
        int randomDeathSfx = (int) p.random(deathSounds.size());
        deathSounds.get(randomDeathSfx).play();
        animatorTop.playAnimation("death");
        super.die();
    }

    private void setPlayerSprite() {
//        sprite.getChild("body").setVisible(true);
//        sprite.getChild("hat").setVisible(true);
    }

}
