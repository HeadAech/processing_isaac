import processing.core.PApplet;

public class Player extends Entity {

    public float damage = 20;

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
        super.damage = 20;
        super.health = 20;
        super.maxHealth = health;
        canGoOutOfBounds = true;
    }

    private void setPlayerSprite() {
//        sprite.getChild("body").setVisible(true);
//        sprite.getChild("hat").setVisible(true);
    }

}
