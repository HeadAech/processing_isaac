import processing.core.PApplet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class LevelGenerator {
    PApplet p;

    ArrayList<Room> rooms = new ArrayList<>();

    ArrayList<Tile> tiles = new ArrayList<>();

    int maxRoomsOnFloor = 15;

    ArrayList<Room> roomsOnFloor = new ArrayList<>();

    LevelGenerator(PApplet p) {
        this.p = p;

    }

    public void prepareTiles() {
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader("data/tiles.txt"));
            String line = reader.readLine();

            int i = 0;

            char sign = 'D';
            String name = "Name";
            String spritePath = "path";
            boolean collision = false;

            while (line != null) {
                System.out.println(line);
                if (line.equals("")) {
                    line = reader.readLine();
                    continue;
                }

                switch (i) {
                    case 0:
                        sign = line.charAt(0);
                        break;
                    case 1:
                        name = line;
                        break;
                    case 2:
                        spritePath = line;
                        break;
                    case 3:
                        collision = Objects.equals(line, "true");
                }

                i++;

                if (i == 4){
                    Tile tile = new Tile(p, sign, name, spritePath);
                    tile.collidable = collision;
                    tiles.add(tile);
                    i = 0;
                }

                //next line
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Tile getTile(char c) {
        Tile tile = null;
        for (Tile t : tiles) {
            if (t.tileType == Tile.getTileType(c)) {
                tile = t;
                break;
            }
        }
        return tile;
    }

    public void prepareRooms() {
        BufferedReader reader;

        File directory = new File("data/rooms");
        int j = 0;
        if (directory.isDirectory()){
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    try {
                        reader = new BufferedReader(new FileReader(file));
                        String line = reader.readLine();

                        int lastDotIndex = file.getName().lastIndexOf(".");

                        String roomName = file.getName().substring(0, lastDotIndex);

                        int x = 0, y = 0;

                        Room room = new Room(p, roomName);

                        while (line != null) {
                            x = 0;
                            for(char c : line.toCharArray()){
                                Tile tile =  new Tile(p, getTile(c));
                                tile.setPosition(new Vector2(x,y));
                                tile.collidable = getTile(c).collidable;
                                if (tile.collidable)
                                    tile.createCollisionShape(room.origin);
                                if (tile.tileType == TileType.DOOR) {
                                    float rot = getDoorRotation(tile.position);
                                    tile.setRotation(rot);
                                    Tile wallTile = new Tile(p, getTile('W'));
                                    wallTile.setPosition(tile.position);
                                    wallTile.setRotation(rot);
                                    room.addTile(wallTile);
                                }
                                room.addTile(tile);
                                x++;
                            }

                            line = reader.readLine();
                            y++;
                        }
                        rooms.add(room);
                        j++;


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private Vector2 pickRandomDoor(ArrayList<Vector2> doorLocations) {
        int randomIdx = (int) p.random(doorLocations.size());
        return doorLocations.get(randomIdx);
    }

    private Vector2 decideOriginDirection(Vector2 doorLocation) {
        if (doorLocation.x == 0) {
            return new Vector2(-1, 0);
        }
        if (doorLocation.y == 0) {
            return new Vector2(0, -1);
        }
        if (doorLocation.x == 14) {
            return new Vector2(1, 0);
        }
        if (doorLocation.y == 8) {
            return new Vector2(0, 1);
        }
        return new Vector2(0, 0);
    }

    public Tile getTile(TileType tileType) {
        for (Tile t : tiles) {
            if (t.tileType == tileType) {
                return t;
            }
        }
        return null;
    }

    public float getDoorRotation(Vector2 doorLocation) {
        if (doorLocation.x == 0) {
            return -90;
        }
        if (doorLocation.y == 0) {
            return 0;
        }
        if (doorLocation.x == 14) {
            return 90;
        }
        if (doorLocation.y == 8) {
            return 180;
        }
        return 0;
    }

    public boolean isDoorConnectedToRoom(Vector2 origin, Vector2 doorlocation) {
        Vector2 direction = decideOriginDirection(doorlocation);
        Room room = getRoomByOrigin(origin.plus(direction));
        return room != null;
    }

    public void generateFloor() {
        Room startingRoom = getStartingRoom().clone();
        if (startingRoom == null) {
            return;
        }

        //populating starting room
        roomsOnFloor.add(startingRoom);

        ArrayList<Vector2> startingDoors = startingRoom.getDoorLocations();
        int randomNumber = (int) p.random(2, startingDoors.size());

        startingDoors = pickRandomDoors(startingDoors, randomNumber);

        for (Vector2 doorLocation : startingDoors) {
            Vector2 origin = decideOriginDirection(doorLocation);
            Room randomRoom = getRandomEmptyRoom();
            randomRoom.setOrigin(new Vector2(randomRoom.origin.x + origin.x, randomRoom.origin.y + origin.y));
            roomsOnFloor.add(randomRoom);
        }

        replaceNotConnectedDoors(startingRoom);

        int roomCount = roomsOnFloor.size();

        for (int i = 0; i < maxRoomsOnFloor - roomCount; i++) {
            Room randomRoom = getRandomRoomOnFloor();
            while (randomRoom.origin.x == 0 && randomRoom.origin.y == 0) {
                randomRoom = getRandomRoomOnFloor();
            }

            ArrayList<Vector2> doors = randomRoom.getDoorLocations();
            int randomDoorIdx = (int) p.random(doors.size());
            while (isDoorConnectedToRoom(randomRoom.origin, doors.get(randomDoorIdx))){
                doors.remove(randomDoorIdx);
                randomDoorIdx = (int) p.random(doors.size());
                if (doors.isEmpty() && randomDoorIdx == 0){
                    break;
                }
            }

            if (doors.isEmpty() && randomDoorIdx == 0){
                i = i == 0 ? 0 : i-1;
                continue;
            }

            Vector2 origin = decideOriginDirection(doors.get(randomDoorIdx));
            Room randomEmptyRoom = getRandomEmptyRoom();
            randomEmptyRoom.setOrigin(new Vector2(randomRoom.origin.x + origin.x, randomRoom.origin.y + origin.y));

            doors = randomEmptyRoom.getDoorLocations();
            int removeDoorsIdx = (int) p.random(doors.size() - 1);
            for (int j = 0; j < removeDoorsIdx; j++) {
                replaceOneRandomNotConnectedDoor(randomEmptyRoom);
            }

            roomsOnFloor.add(randomEmptyRoom);

        }

        for(Room room : roomsOnFloor) {
            replaceNotConnectedDoors(room);
        }

//        for (int i = 0; i < maxRoomsOnFloor; i++) {
//
//
//
//
//        }
    }

    private Room getRandomRoomOnFloor() {
        int randomIdx = (int) p.random(roomsOnFloor.size());
        Room randomRoom = roomsOnFloor.get(randomIdx).clone();
        return randomRoom;
    }

    private Room getRandomEmptyRoom() {
        int randomIdx = (int) p.random(rooms.size());
        Room randomRoom = rooms.get(randomIdx).clone();
        return randomRoom;
    }

    private void replaceNotConnectedDoors(Room room) {
        ArrayList<Vector2> startingDoors = room.getDoorLocations();
        for (Vector2 doorLocation : startingDoors) {
            if (!isDoorConnectedToRoom(room.origin, doorLocation)) {
                Tile currentTile = room.getTile(doorLocation.x, doorLocation.y);
                Tile wallTile = getTile(getTileWallOrientation(doorLocation));
                wallTile.position = currentTile.position;
                wallTile.rotation = 0;
                room.setTile(doorLocation.x, doorLocation.y, wallTile);
            }
        }
    }

    private void replaceOneRandomNotConnectedDoor(Room room) {
        ArrayList<Vector2> startingDoors = room.getDoorLocations();
        int randomIdx = (int) p.random(startingDoors.size());
        while (isDoorConnectedToRoom(room.origin, startingDoors.get(randomIdx))){
            startingDoors.remove(randomIdx);
            randomIdx = (int) p.random(startingDoors.size());
            if (startingDoors.isEmpty() && randomIdx == 0){
                break;
            }
        }
        if (startingDoors.isEmpty() && randomIdx == 0){
            return;
        }

        Vector2 doorLocation = startingDoors.get(randomIdx);

        if (!isDoorConnectedToRoom(room.origin, doorLocation)) {
            Tile currentTile = room.getTile(doorLocation.x, doorLocation.y);
            Tile wallTile = getTile(getTileWallOrientation(doorLocation));
            wallTile.position = currentTile.position;
            wallTile.rotation = 0;
            room.setTile(doorLocation.x, doorLocation.y, wallTile);
        }
    }

    private TileType getTileWallOrientation(Vector2 doorLocation) {
        if (doorLocation.x == 0) {
            return TileType.WALL_LEFT;
        }
        if (doorLocation.y == 0) {
            return TileType.WALL_TOP;
        }
        if (doorLocation.x == 14) {
            return TileType.WALL_RIGHT;
        }
        if (doorLocation.y == 8) {
            return TileType.WALL_BOTTOM;
        }
        return null;
    }

    private Room getRoomByOrigin(Vector2 origin) {
        for (Room room : roomsOnFloor) {
            if (room.origin.x == origin.x && room.origin.y == origin.y) {
                return room;
            }
        }
        return null;
    }
    private ArrayList<Vector2> pickRandomDoors(ArrayList<Vector2> doorLocations, int number) {
        ArrayList<Vector2> doors = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            Vector2 randomDoor = pickRandomDoor(doorLocations);
            doors.add(randomDoor);
            doorLocations.remove(randomDoor);
        }
        return doors;
    }

    private Room getStartingRoom() {
        for (Room room : rooms) {
            if (Objects.equals(room.name, "starting_room")) {
                return room;
            }
        }
        return null;
    }

    public void regenerateFloor() {
        roomsOnFloor.clear();
        generateFloor();
    }

}
