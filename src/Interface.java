import processing.core.PApplet;
import processing.core.PImage;

public class Interface {

    PApplet p;

    PImage heartsSpriteSheet;

    int score = 0;

    int heartTileWidth = 32;
    int heartTileHeight = 32;

    PImage emptyRedHeart;
    PImage fullRedHeart;
    PImage halfRedHeart;

    Entity player;


    Interface(PApplet p, PImage heartsSpriteSheet) {
        this.p = p;
        this.heartsSpriteSheet = heartsSpriteSheet;

        fullRedHeart = getTile(0, 0);
        fullRedHeart.resize(heartTileWidth, heartTileHeight);
        halfRedHeart = getTile(1, 0);
        emptyRedHeart = getTile(2, 0);
    }


    private PImage getTile(int x, int y) {
        int sx = x * heartTileWidth;
        int sy = y * heartTileHeight;
        return heartsSpriteSheet.get(sx, sy, heartTileWidth, heartTileHeight);
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public void _update() {

        _display();
    }

    public void _display() {
        p.textAlign(p.LEFT);
        p.pushMatrix();
        p.color(0);
        p.fill(255);

        int x = 30;
        int y = 30;
        p.noSmooth();
        p.scale(1.5f);
        p.imageMode(PApplet.CENTER);
        int fullHearts = (int) (player.health / 2);
        int halfHearts = player.health % 2 == 0 ? 0 : 1;
        int emptyHearts = (int) ((player.maxHealth - player.health) / 2) ;
        for (int i = 0; i < fullHearts; i++) {
            p.image(fullRedHeart, x, y, heartTileWidth, heartTileHeight);
            if (x > 6 * (heartTileWidth - 5)) {
                y += heartTileHeight - 5;
                x = 5;
            }
            x += heartTileWidth - 5;

        }
        for (int i = 0; i < halfHearts; i++) {
            p.image(halfRedHeart, x, y, heartTileWidth, heartTileHeight);
            if (x > 6 * (heartTileWidth - 5)) {
                y += heartTileHeight - 5;
                x = 5;
            }
            x += heartTileWidth - 5;
        }
        for (int i = 0; i < emptyHearts; i++) {
            p.image(emptyRedHeart, x, y, heartTileWidth, heartTileHeight);
            if (x > 6 * (heartTileWidth - 5)) {
                y += heartTileHeight - 5;
                x = 5;
            }
            x += heartTileWidth - 5;
        }

        String fps = "fps: " + p.round(p.frameRate);
        p.text(fps, p.width - p.textWidth(fps) - 5, p.height - p.textAscent());

        p.popMatrix();
    }


    void drawHearts(PImage heart) {

    }

}
