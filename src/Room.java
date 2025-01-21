import processing.core.PApplet;

import java.util.ArrayList;

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


    Room(PApplet p, String name) {
        this.p = p;
        this.name = name;
    }

    void addTile(Tile tile) {
        tiles.add(tile);
    }
    void setOrigin(Vector2 origin) {
        this.origin = origin;
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
        tiles.remove(foundTile);
        tiles.add(tile);
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
