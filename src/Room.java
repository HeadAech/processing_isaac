import processing.core.PApplet;

import java.util.ArrayList;

enum RoomType {
    NORMAL, TREASURE, BOSS, SHOP
}

public class Room implements Cloneable {

    PApplet p;

    String name;

    ArrayList<Enemy> enemies = new ArrayList<>();

    ArrayList<Obstacle> obstacles = new ArrayList<>();

    ArrayList<Tile> tiles = new ArrayList<>();

    float width = 15;
    float height = 9;

    Vector2 origin = new Vector2(0, 0);
    Vector2 scale = new Vector2(1.5f, 1.5f);

    boolean discovered = false;

    RoomType roomType = RoomType.NORMAL;


    Room(PApplet p, String name) {
        this.p = p;
        this.name = name;
    }

    void addTile(Tile tile) {
        tiles.add(tile);
    }

    void setOrigin(Vector2 origin) {
        this.origin = origin;
        PApplet.println(origin.x, origin.y);
        for (Tile tile : tiles) {
            tile.globalPosition = tile.getGlobalPosition(this.origin, new Vector2(width, height));
            if (tile.collidable)
                tile.setCollisionShapePosition(new Vector2(this.origin.x, this.origin.y), new Vector2(this.width, this.height));
        }
    }

    public void setRoomType(RoomType type) {
        this.roomType = type;
        ArrayList<Vector2> doors = getDoorLocations();
        for (Vector2 door : doors) {
            replaceDoorsForRoomType(door, type);
        }
    }

    public void replaceDoorsForRoomType(Vector2 doorLocation, RoomType type) {
        if (type == RoomType.NORMAL) return;
        Tile tile = getDoorTile(doorLocation.x, doorLocation.y);
        if (tile.tileType == TileType.DOOR) {
            if (type == RoomType.BOSS) {
//                tile.tileType = TileType.BOSS_ROOM_DOOR;
                tile.setSpritePath("data/sprites/boss_door.png");
            } else if (type == RoomType.SHOP) {
//                    tile.setSpritePath("data/sprites/shop_door.png");
            } else if (type == RoomType.TREASURE) {
                    tile.setSpritePath("data/sprites/treasure_door.png");
            }
        }
    }

    ArrayList<Vector2> getDoorLocations() {
        ArrayList<Vector2> doorLocations = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile.tileType == TileType.DOOR) {
                doorLocations.add(tile.position);
            }
        }
        return doorLocations;
    }

    public Tile getTile(float x, float y) {
        for (Tile tile : tiles) {
            if (tile.position.x == x && tile.position.y == y) {
                return tile;
            }
        }
        return null;
    }

    public void setTile(float x, float y, Tile tile) {
        Tile foundTile = getTile(x, y);
        int index = tiles.indexOf(foundTile);
        tiles.set(index, tile);
    }

    public Tile getDoorTile(float x, float y) {
        for (Tile tile : tiles) {
            if (tile.position.x == x && tile.position.y == y) {
                if (tile.tileType == TileType.DOOR) {
                    return tile;
                }
            }
        }
        return null;
    }

    public void replaceDoor(float x, float y, Tile tile) {
        Tile foundTile = getDoorTile(x, y);
        int index = tiles.indexOf(foundTile);
        tile.createCollisionShape(this.origin, null);
        tile.globalPosition = tile.getGlobalPosition(this.origin, new Vector2(width, height));
        if (tile.collidable)
            tile.setCollisionShapePosition(new Vector2(this.origin.x, this.origin.y), new Vector2(this.width, this.height));
        tiles.set(index, tile);
    }

    public Vector2 getTilePositionByCollision(Vector2 position) {
        for (Tile tile : tiles) {
            if (tile.collisionShape != null) {
                if (Vector2.areEqual(tile.collisionShape.position, position)) {
                    return tile.position;
                }
            }
        }
        return null;
    }

    public Vector2 getGlobalPositionOfOrigin() {
        float globalX = origin.x * scale.x * width;
        float globalY = origin.y * scale.y * height;
        return new Vector2(globalX, globalY);
    }

    // Override clone() to perform a deep copy
    @Override
    public Room clone() {
        try {
            Room copy = (Room) super.clone();  // Shallow copy of object
            copy.tiles = (ArrayList<Tile>) tiles.clone();
            copy.name = name;
            copy.p = p;
            copy.origin = origin;
            copy.scale = scale;
//            copy.obstacles = (ArrayList<Obstacle>) obstacles.clone();
//            copy.enemies = (ArrayList<Enemy>) enemies.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

}
