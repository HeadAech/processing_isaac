import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;

import java.util.ArrayList;
import java.util.UUID;

enum TileType{
    BOSS_ROOM_DOOR, DOOR, FLOOR,
    WALL_TOP, WALL_BOTTOM, WALL_LEFT, WALL_RIGHT,
    WALL_CORNER_TOP_LEFT, WALL_CORNER_TOP_RIGHT, WALL_CORNER_BOTTOM_LEFT, WALL_CORNER_BOTTOM_RIGHT,
    ROCK,
    POOP,
    ENEMY_SPAWN,
    DOOR_CLOSED
}

public class Tile implements Cloneable {
    PApplet p;

    UUID uuid = UUID.randomUUID();

    String name;
    String spritePath;
    TileType tileType;
    Vector2 position;
    float rotation;

    PImage textureImage;

    PShape shape;

    float width = 52;
    float height = 52;
    Vector2 scale = new Vector2(1.5f, 1.5f);

    boolean collidable = false;

    CollisionShape collisionShape;

    boolean drawCollider = true;

    boolean destructible = false;

    Vector2 globalPosition = new Vector2(0, 0);

    float health = 6;

    Tile(PApplet p, char sign, String name, String spritePath) {
        this.p = p;
        this.name = name;
        this.spritePath = "data/sprites/" + spritePath;
        this.tileType = getTileType(sign);
        createTextureImage();
        onProjectileEnteredCollisionShape();
    }

    Tile (PApplet p, TileType type, String name, String spritePath) {
        this.p = p;
        this.name = name;
        this.spritePath = "data/sprites/" + spritePath;
        this.tileType = type;
        onProjectileEnteredCollisionShape();
    }

    Tile(PApplet p, Tile tile) {
        this.p = p;
        this.name = tile.name;
        this.spritePath = tile.spritePath;
        this.tileType = tile.tileType;
        this.position = tile.position;
        this.rotation = tile.rotation;
        this.textureImage = tile.textureImage;
        createTextureImage();
        onProjectileEnteredCollisionShape();
    }

    private void onProjectileEnteredCollisionShape() {
        Signals.ProjectileEnteredCollisionShape.connect(uuid -> {
            if (collisionShape == null) return;
            if (collisionShape.uuid.equals(uuid)) {
                if (destructible)
                    this.damage(2);
            }
        });
    }

    public void damage(float damage) {
        health -= damage;
        if (tileType == TileType.POOP) {
            if (health <= 0) {
                this.spritePath = "data/sprites/poop_destroyed.png";
            } else if (health <= 2) {
                this.spritePath = "data/sprites/poop_damaged_2.png";
            } else if (health <= 4) {
                this.spritePath = "data/sprites/poop_damaged_1.png";

            }
            this.createTextureImage();
        }
    }

    public void createTextureImage() {
        textureImage = p.loadImage(this.spritePath);

        if (tileType == TileType.ROCK) {
            ArrayList<Float> rotations = new ArrayList<>();
            rotations.add(0.0f);
            rotations.add(90.0f);
            rotations.add(180.0f);
            rotations.add(270.0f);
            int randIdx = (int) p.random(rotations.size());
            this.textureImage.resize(52, 52);
            this.rotation = rotations.get(randIdx);
            this.destructible = true;
        }
        if (tileType == TileType.POOP) {
            this.textureImage.resize(32,32);
            this.destructible = true;
        }
        p.noSmooth();
    }

    public void drawCollider() {
        p.pushMatrix();

        // Move to the player's position
        p.noFill();
        p.stroke(255,0,0);
        p.rect(collisionShape.left, collisionShape.top, collisionShape.size.x, collisionShape.size.y);

        p.noStroke();
        p.popMatrix();
    }

    public void setCollisionShape(CollisionShape collisionShape) {
        this.collisionShape = collisionShape;
    }

    public void createCollisionShape(Vector2 origin, Vector2 size) {
        float xOffset = origin.x + 1;
        float yOffset = origin.y + 1;
        Vector2 colSize = size == null ? new Vector2(width, height) : new Vector2(size);
        this.collisionShape = new CollisionShape(
                new Vector2(position.x * width * scale.x,
                         position.y * height * scale.y),
                new Vector2(colSize.x * scale.x, colSize.y * scale.y)
        );
    }

    public void setCollisionShapePosition(Vector2 origin, Vector2 roomScale) {

        Vector2 globalPos = getGlobalPosition(origin, roomScale);
        this.collisionShape.setPosition(new Vector2(globalPos.x, globalPos.y));
    }

    public Vector2 getGlobalPosition(Vector2 roomOrigin, Vector2 roomScale) {
        // Calculate the global position by combining the room's transformation with the tile's local transformation
        float globalX = roomOrigin.x * roomScale.x * width * scale.x + position.x * width * scale.x;
        float globalY = roomOrigin.y * roomScale.y * height * scale.y + position.y * height * scale.y;

        return new Vector2(globalX, globalY);
    }

    public void setLockedDoors(boolean locked) {
        String closed = locked ? "closed" : "open";
        if (tileType == TileType.DOOR) {
            if (name.contains("Boss")) {
                this.spritePath = "data/sprites/boss_room_door_"+ closed +".png";
            } else if (name.contains("Treasure")) {
                this.spritePath = "data/sprites/treasure_room_door_"+ closed +".png";
            } else {
                this.spritePath = "data/sprites/door_"+ closed +".png";
            }
            createTextureImage();
        }
    }

    public void draw() {
        p.pushMatrix();

        // Translate to the center of the image
        p.translate(position.x * width * scale.x, position.y * height * scale.y);
        p.translate((width / 2) * scale.x, (height / 2) * scale.y);

        // Apply the rotation
        p.rotate(PApplet.radians(rotation));

        // Set image mode to center to draw the image centered at position
        p.imageMode(PConstants.CENTER);
        // Draw the image, scaling it appropriately
        if (tileType == TileType.POOP) {
            p.image(textureImage, 0, 0, textureImage.width * scale.x, textureImage.height * scale.y);
        }else {
            p.image(textureImage, 0, 0, width * scale.x, height * scale.y);
        }

        // Reset scale (not necessary here since the transformations are done inside the pushMatrix/popMatrix block)
        p.popMatrix();

    }

    public void setSpritePath(String spritePath) {
        this.spritePath = spritePath;
        this.textureImage = p.loadImage(this.spritePath);
        createTextureImage();
    }

    public String getName() {
        return name;
    }

    public String getSpritePath() {
        return spritePath;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public static TileType getTileType(char sign) {
        return switch (sign) {
            case '#' -> TileType.DOOR;
            case '$' -> TileType.DOOR_CLOSED;
            case 'B' -> TileType.BOSS_ROOM_DOOR;
            case 'W' -> TileType.WALL_TOP;
            case 'D' -> TileType.WALL_RIGHT;
            case 'A' -> TileType.WALL_LEFT;
            case 'X' -> TileType.WALL_BOTTOM;
            case 'Q' -> TileType.WALL_CORNER_TOP_LEFT;
            case 'E' -> TileType.WALL_CORNER_TOP_RIGHT;
            case 'Z' -> TileType.WALL_CORNER_BOTTOM_LEFT;
            case 'C' -> TileType.WALL_CORNER_BOTTOM_RIGHT;
            case '.' -> TileType.FLOOR;
            case 'R' -> TileType.ROCK;
            case 'P' -> TileType.POOP;
            case '+' -> TileType.ENEMY_SPAWN;
            default -> TileType.FLOOR;
        };
    }


    @Override
    public Tile clone() {
        try {
            Tile copy = (Tile) super.clone();  // Shallow copy of object
            copy.position = this.position != null ? new Vector2(position.x, position.y) : new Vector2(0,0);
            copy.rotation = rotation;
            copy.scale = new Vector2(scale.x, scale.y);
            copy.textureImage = textureImage;
            copy.shape = shape;
            copy.tileType = tileType;
            copy.p = this.p;
            copy.name = this.name;
            copy.spritePath = this.spritePath;
            copy.height = this.height;
            copy.width = this.width;
            if (collidable && collisionShape != null) {
                copy.collisionShape = collisionShape.clone();
            }
            copy.collidable = collidable;
            copy.destructible = this.destructible;
            copy.uuid = UUID.randomUUID();
            copy.health = health;
            return copy;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
