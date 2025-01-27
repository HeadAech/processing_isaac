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

    float minimapTileWidth = 20;
    float minimapTileHeight = 20;

    LevelGenerator levelGenerator;

    int currentRoomIdx = 0;
    PImage treasureRoomIcon;
    PImage bossRoomIcon;
    PImage shopRoomIcon;

    //stats icons
    int statIconWidth = 64;
    int statIconHeight = 64;
    PImage statsSpritesheet;
    PImage speedIcon;
    PImage damageIcon;
    PImage firerateIcon;
    PImage rangeIcon;
    PImage shotspeedIcon;
    PImage luckIcon;


    Interface(PApplet p, PImage heartsSpriteSheet) {
        this.p = p;
        this.heartsSpriteSheet = heartsSpriteSheet;

        fullRedHeart = getTile(0, 0, heartTileWidth, heartTileHeight, heartsSpriteSheet);
        fullRedHeart.resize(heartTileWidth, heartTileHeight);
        halfRedHeart = getTile(1, 0,heartTileWidth, heartTileHeight, heartsSpriteSheet);
        emptyRedHeart = getTile(2, 0,heartTileWidth, heartTileHeight, heartsSpriteSheet);

        treasureRoomIcon = p.loadImage("data/sprites/icons/Treasure_Room_icon.png");
        bossRoomIcon = p.loadImage("data/sprites/icons/Boss_Room_Icon.png");
        shopRoomIcon = p.loadImage("data/sprites/icons/Shop_Icon.png");

        //stats
        statsSpritesheet = p.loadImage("data/sprites/spritesheet/hudstats.png");
        speedIcon = getTile(1, 0, statIconWidth, statIconHeight, statsSpritesheet);
        damageIcon = getTile(0, 0, statIconWidth, statIconHeight, statsSpritesheet);
        firerateIcon = getTile(0, 1, statIconWidth, statIconHeight, statsSpritesheet);
        rangeIcon = getTile(2, 0, statIconWidth, statIconHeight, statsSpritesheet);
        shotspeedIcon = getTile(1, 1, statIconWidth, statIconHeight, statsSpritesheet);
        luckIcon = getTile(2, 1, statIconWidth, statIconHeight, statsSpritesheet);
    }


    private PImage getTile(int x, int y, int w, int h, PImage spritesheet) {
        int sx = x * w;
        int sy = y * h;
        return spritesheet.get(sx, sy, w, h);
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
       drawStats();

        String fps = "fps: " + p.round(p.frameRate);
        p.text(fps, p.width - p.textWidth(fps) - 5, p.height - p.textAscent());

        p.popMatrix();
    }

    void drawMinimap() {
        p.pushMatrix();

        p.translate(p.width - minimapTileWidth * levelGenerator.maxRoomsOnFloor + 20, 10);
        p.fill(0, 0, 0, 100f);
        p.noStroke();
//        p.rect(0, 0, minimapTileWidth * levelGenerator.maxRoomsOnFloor - 50, minimapTileHeight * levelGenerator.maxRoomsOnFloor - 50);
        p.noFill();
        p.stroke(0);
        p.strokeWeight(4);
//        p.rect(0, 0, minimapTileWidth * levelGenerator.maxRoomsOnFloor - 50, minimapTileHeight * levelGenerator.maxRoomsOnFloor - 50);

        p.stroke(0);
        p.strokeWeight(2);
        p.translate((minimapTileWidth * levelGenerator.maxRoomsOnFloor - 50) / 2, (minimapTileHeight * levelGenerator.maxRoomsOnFloor - 50) / 2);

        for (int i = 0; i < levelGenerator.roomsOnFloor.size(); i++) {
            Room room = levelGenerator.roomsOnFloor.get(i);

            if (!room.discovered) continue;

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

    void drawStats() {
        p.pushMatrix();

        int x = 50;
        int y = 200;
        p.color(255);
        p.strokeWeight(1);
        p.fill(255);
        p.textSize(22);
        p.textAlign(PApplet.CENTER, PApplet.CENTER);

        String speed = String.format("%.2f", player.speed);
        String firerate = String.format("%.2f", player.firerate);
        String damage = String.format("%.2f", player.damage);
        String range = String.format("%.2f", player.range);
        String shotspeed = String.format("%.2f", player.shotSpeed);
        String luck = String.format("%.2f", player.luck);

        p.image(speedIcon, x, y, statIconWidth, statIconHeight);
        p.text(speed, x + statIconWidth, y);

        y += statIconHeight;

        p.image(firerateIcon, x, y, statIconWidth, statIconHeight);
        p.text(firerate, x + statIconWidth, y);

        y += statIconHeight;

        p.image(damageIcon, x, y, statIconWidth, statIconHeight);
        p.text(damage, x + statIconWidth, y);

        y += statIconHeight;

        p.image(rangeIcon, x, y, statIconWidth, statIconHeight);
        p.text(range, x + statIconWidth, y);

        y += statIconHeight;

        p.image(shotspeedIcon, x, y, statIconWidth, statIconHeight);
        p.text(shotspeed, x + statIconWidth, y);

        y += statIconHeight;

        p.image(luckIcon, x, y, statIconWidth, statIconHeight);
        p.text(luck, x + statIconWidth, y);

        p.popMatrix();
    }


}
