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

    float minimapTileWidth = 15;
    float minimapTileHeight = 15;

    LevelGenerator levelGenerator;

    int currentRoomIdx = 0;
    PImage treasureRoomIcon;
    PImage bossRoomIcon;
    PImage shopRoomIcon;


    Interface(PApplet p, PImage heartsSpriteSheet) {
        this.p = p;
        this.heartsSpriteSheet = heartsSpriteSheet;

        fullRedHeart = getTile(0, 0);
        fullRedHeart.resize(heartTileWidth, heartTileHeight);
        halfRedHeart = getTile(1, 0);
        emptyRedHeart = getTile(2, 0);

        treasureRoomIcon = p.loadImage("data/sprites/icons/Treasure_Room_icon.png");
        bossRoomIcon = p.loadImage("data/sprites/icons/Boss_Room_Icon.png");
        shopRoomIcon = p.loadImage("data/sprites/icons/Shop_Icon.png");
    }


    private PImage getTile(int x, int y) {
        int sx = x * heartTileWidth;
        int sy = y * heartTileHeight;
        return heartsSpriteSheet.get(sx, sy, heartTileWidth, heartTileHeight);
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public void setLevelGenerator(LevelGenerator levelGenerator) {
        this.levelGenerator = levelGenerator;
    }

    public void _update() {

        _display();
    }

    public void _display() {
        p.textAlign(p.LEFT);
        p.pushMatrix();
        p.color(0);
        p.fill(255);

       drawHearts();
       drawMinimap();

        String fps = "fps: " + p.round(p.frameRate);
        p.text(fps, p.width - p.textWidth(fps) - 5, p.height - p.textAscent());

        p.popMatrix();
    }

    void drawMinimap() {
        p.pushMatrix();

        p.translate(p.width - minimapTileWidth * levelGenerator.maxRoomsOnFloor - 30, 30);
        p.fill(0, 0, 0, 100f);
        p.noStroke();
        p.rect(0, 0, minimapTileWidth * levelGenerator.maxRoomsOnFloor, minimapTileHeight * levelGenerator.maxRoomsOnFloor);
        p.noFill();
        p.stroke(0);
        p.strokeWeight(4);
        p.rect(0, 0, minimapTileWidth * levelGenerator.maxRoomsOnFloor, minimapTileHeight * levelGenerator.maxRoomsOnFloor);

        p.stroke(0);
        p.strokeWeight(2);
        p.translate(minimapTileWidth * levelGenerator.maxRoomsOnFloor / 2, minimapTileHeight * levelGenerator.maxRoomsOnFloor / 2);

        for (int i = 0; i < levelGenerator.roomsOnFloor.size(); i++) {
            Room room = levelGenerator.roomsOnFloor.get(i);

//            if (!room.discovered) continue;

            int x = (int) (room.origin.x * minimapTileWidth);
            int y = (int) (room.origin.y * minimapTileHeight);
            if (i == currentRoomIdx) {
                p.fill(255);
            } else {
                p.fill(100);
            }
            p.rect(x, y, minimapTileWidth, minimapTileHeight);
            if (room.roomType != RoomType.NORMAL) {
                PImage icon;
                switch (room.roomType) {
                    case TREASURE -> icon = treasureRoomIcon;
                    case BOSS -> icon = bossRoomIcon;
                    case SHOP -> icon = shopRoomIcon;
                    default -> icon = null;
                }
                p.noSmooth();
                p.image(icon, x + minimapTileWidth/2, y + minimapTileHeight/2, minimapTileWidth - 2, minimapTileHeight - 2);
            }
        }

        p.popMatrix();
    }

    void drawHearts() {
        p.pushMatrix();
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
        p.popMatrix();
    }


}
