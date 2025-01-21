import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;

enum TileType{
    DOOR, FLOOR,
    WALL_TOP, WALL_BOTTOM, WALL_LEFT, WALL_RIGHT,
    WALL_CORNER_TOP_LEFT, WALL_CORNER_TOP_RIGHT, WALL_CORNER_BOTTOM_LEFT, WALL_CORNER_BOTTOM_RIGHT,
}

public class Tile implements Cloneable {
    PApplet p;

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

    Tile(PApplet p, char sign, String name, String spritePath) {
        this.p = p;
        this.name = name;
        this.spritePath = "data/sprites/" + spritePath;
        this.tileType = getTileType(sign);
        textureImage = p.loadImage(this.spritePath);
//        shape = p.createShape();
//        shape.beginShape();
//        shape.texture(textureImage);
//
//        shape.vertex(0, 0);
//        shape.vertex(100, 0);
//        shape.vertex(100, 100);
//        shape.vertex(0, 100);
//        shape.endShape(p.CLOSE);
    }

    Tile(PApplet p, Tile tile) {
        this.p = p;
        this.name = tile.name;
        this.spritePath = tile.spritePath;
        this.tileType = tile.tileType;
        this.position = tile.position;
        this.rotation = tile.rotation;
        this.textureImage = tile.textureImage;
    }

    private void drawCollider() {
        p.pushMatrix();

        // Move to the player's position
        p.translate(collisionShape.position.x, collisionShape.position.y);

        // Get sprite dimensions
        float colliderWidth = collisionShape.size.x;
        float colliderHeight = collisionShape.size.y;

        // Set the collider to be centered on the player
        p.translate(-colliderWidth / 2, -colliderHeight / 2);

        // Draw the rectangle representing the collider
        p.noFill();
        p.stroke(255, 0, 0); // Red color for visibility
        p.rect(0, 0, colliderWidth, colliderHeight);

        p.popMatrix();
    }

    public void setCollisionShape(CollisionShape collisionShape) {
        this.collisionShape = collisionShape;
    }

    public void createCollisionShape(Vector2 origin) {
        float xOffset = origin.x + 1;
        float yOffset = origin.y + 1;
        this.collisionShape = new CollisionShape(
                new Vector2(position.x * width * scale.x,
                         position.y * height * scale.y),
                new Vector2(width * scale.x, height * scale.y)
        );
    }

    public void setCollisionShapePosition(Vector2 origin) {
        this.collisionShape.setPosition(new Vector2(position.x * width * scale.x + origin.x,
                origin.y + position.y * height * scale.y));
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
        p.image(textureImage, 0, 0, width * scale.x, height * scale.y);

        // Reset scale (not necessary here since the transformations are done inside the pushMatrix/popMatrix block)
        p.popMatrix();

        if (drawCollider && collisionShape != null)
            drawCollider();
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
            case 'W' -> TileType.WALL_TOP;
            case 'D' -> TileType.WALL_RIGHT;
            case 'A' -> TileType.WALL_LEFT;
            case 'X' -> TileType.WALL_BOTTOM;
            case 'Q' -> TileType.WALL_CORNER_TOP_LEFT;
            case 'E' -> TileType.WALL_CORNER_TOP_RIGHT;
            case 'Z' -> TileType.WALL_CORNER_BOTTOM_LEFT;
            case 'C' -> TileType.WALL_CORNER_BOTTOM_RIGHT;
            case '.' -> TileType.FLOOR;
            default -> TileType.FLOOR;
        };
    }


    @Override
    public Tile clone() {
        try {
            Tile copy = (Tile) super.clone();  // Shallow copy of object
            copy.position = new Vector2(position.x, position.y);
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
            copy.collisionShape = collisionShape.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
