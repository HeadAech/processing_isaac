import processing.core.PApplet;

public class Player extends Entity {

    public float damage = 20;

    public Player(PApplet p) {
        super(p);
        setPlayerSprite();
        setPlayerStats();
    }

    public Player(PApplet p, Vector2 position) {
        super(p, position);
        setPlayerSprite();
        setPlayerStats();
    }

    private void setPlayerStats() {
        super.damage = 20;
        super.speed = 9;
        canGoOutOfBounds = true;

    }

    private void setPlayerSprite() {
        sprite.getChild("body").setVisible(true);
        sprite.getChild("hat").setVisible(true);
    }

}
