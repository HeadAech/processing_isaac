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

                                Vector2 collisionSize =
                                        tile.tileType == TileType.DOOR ?
                                                new Vector2(tile.width, tile.height/2)
                                                : null;
                                if (tile.collidable)
                                    tile.createCollisionShape(room.origin, null);
                                if (tile.tileType == TileType.DOOR) {
                                    float rot = getDoorRotation(tile.position);
                                    tile.setRotation(rot);
                                    tile.collisionShape.setTriggerType(TriggerType.DOOR);
                                    Tile wallTile = new Tile(p, getTile('W'));
                                    wallTile.setPosition(tile.position);
                                    wallTile.setRotation(rot);
                                    room.addTile(wallTile);
                                }
                                if (tile.tileType == TileType.ROCK) {
//                                    tile.scale.x = 0.8f;
//                                    tile.scale.y = 0.8f;
                                    tile.collisionShape.setSize(new Vector2(tile.width * tile.scale.x, tile.height * tile.scale.y));
                                    Tile floorTile = new Tile(p, getTile('.'));
                                    floorTile.setPosition(tile.position);
                                    room.addTile(floorTile);
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

    public Vector2 decideOriginDirection(Vector2 doorLocation) {
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
        if (room == null) return false;
        ArrayList<Vector2> roomDoors = room.getDoorLocations();
        Vector2 oppositeDirection = direction.multiply(-1);
        if (!roomDoors.isEmpty()) {
            for (Vector2 door : roomDoors) {
                Vector2 roomDoorDirection = decideOriginDirection(door);
                if (roomDoorDirection.x == oppositeDirection.x && roomDoorDirection.y == oppositeDirection.y) {
                    return true;
                }
            }
        }
        return false;
    }

    public Vector2 getDoorPositionInNextRoom(Vector2 direction, int idx) {
        Room r = roomsOnFloor.get(idx);
        if (r == null) return null;

        ArrayList<Vector2> roomDoors = r.getDoorLocations();
        Vector2 oppositeDirection = direction.multiply(-1);
        if (!roomDoors.isEmpty()) {
            for (Vector2 door : roomDoors) {
                Vector2 roomDoorDirection = decideOriginDirection(door);
                if (roomDoorDirection.x == oppositeDirection.x && roomDoorDirection.y == oppositeDirection.y) {
                    return door;
                }
            }
        }
        return null;
    }

    public void generateFloor() {
        Room startingRoom = getStartingRoom().clone();
        if (startingRoom == null) {
            return;
        }

        startingRoom.setOrigin(new Vector2(0, 0));
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
        ArrayList<Vector2> roomDoors = startingRoom.getDoorLocations();

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
//                replaceOneRandomNotConnectedDoor(randomEmptyRoom);
            }

            roomsOnFloor.add(randomEmptyRoom);

        }

        //add treasure room
        addRoomToFloor(RoomType.TREASURE);
        //add boss room
        addRoomToFloor(RoomType.BOSS);

        for(Room room : roomsOnFloor) {
            ArrayList<Vector2> doors = room.getDoorLocations();
            for (Vector2 door : doors) {
                Vector2 origin = decideOriginDirection(door);
                Room nextRoom = getRoomByOrigin(new Vector2(origin.x + room.origin.x, origin.y + room.origin.y));
                if (nextRoom == null) continue;
                room.replaceDoorsForRoomType(door, nextRoom.roomType);
            }
            replaceNotConnectedDoors(room);
        }

        startingRoom.discovered = true;
        discoverNearbyRooms(startingRoom);



    }

    private void addRoomToFloor(RoomType type) {
        Room furthestRoom = roomsOnFloor.get(roomsOnFloor.size() - 1);

        if (type == RoomType.TREASURE) {
            furthestRoom = getFurthestRoom();
        } else {
            furthestRoom = getOutsideRoom();
        }

        if (furthestRoom != null) {
            ArrayList<Vector2> doors = furthestRoom.getDoorLocations();
            int randomDoorIdx = (int) p.random(doors.size());
            while (isDoorConnectedToRoom(furthestRoom.origin, doors.get(randomDoorIdx))){
                doors.remove(randomDoorIdx);
                randomDoorIdx = (int) p.random(doors.size());
                if (doors.isEmpty() && randomDoorIdx == 0){
                    break;
                }
            }

            Vector2 origin = decideOriginDirection(doors.get(randomDoorIdx));
            Room randomEmptyRoom = getRandomEmptyRoom();
            randomEmptyRoom.setOrigin(new Vector2(furthestRoom.origin.x + origin.x, furthestRoom.origin.y + origin.y));
            randomEmptyRoom.setRoomType(type);
            roomsOnFloor.add(randomEmptyRoom);
        }
    }

    private Room getOutsideRoom() {
        Room outsideRoom = roomsOnFloor.get(roomsOnFloor.size() - 1);

        for (Room room : roomsOnFloor) {
            ArrayList<Vector2> doors = room.getDoorLocations();
            int randomDoorIdx = (int) p.random(doors.size());
            while (isDoorConnectedToRoom(room.origin, doors.get(randomDoorIdx))){
                doors.remove(randomDoorIdx);
                randomDoorIdx = (int) p.random(doors.size());
                if (doors.isEmpty() && randomDoorIdx == 0){
                    break;
                }
            }

            if (doors.isEmpty() && randomDoorIdx == 0){
                continue;
            } else {
                outsideRoom = room;
                break;
            }


        }

        return outsideRoom;
    }

    public int getRoomIndex(Vector2 origin) {
        for (int i = 0; i < roomsOnFloor.size(); i++) {
            Room r = roomsOnFloor.get(i);
            if (Vector2.areEqual(r.origin, origin)) {
                return i;
            }
        }
        return -1;
    }

    private Room createUniqueRoom(Room originalRoom) {
        // Create a new Room with unique tiles
        Room uniqueRoom = new Room(p, originalRoom.name);
        uniqueRoom.origin = new Vector2(originalRoom.origin);
        for (Tile tile : originalRoom.tiles) {
            Tile newTile = new Tile(p, tile.tileType, tile.name, tile.spritePath);
            newTile.setPosition(new Vector2(tile.position.x, tile.position.y)); // Clone position
            newTile.setRotation(tile.rotation);
            newTile.collidable = tile.collidable;
            newTile.textureImage = tile.textureImage;
            if (tile.collidable) {
                newTile.createCollisionShape(uniqueRoom.origin, null);
                if (tile.tileType == TileType.DOOR) {
                    newTile.collisionShape.setTriggerType(TriggerType.DOOR);
                }
            }
            uniqueRoom.addTile(newTile);
        }
        return uniqueRoom;
    }

    public void discoverNearbyRooms(Room room) {
        ArrayList<Vector2> doors = room.getDoorLocations();
        for (Vector2 doorLocation : doors) {
            Vector2 origin = decideOriginDirection(doorLocation);
            Vector2 origin1 = new Vector2(origin.x + room.origin.x, origin.y + room.origin.y);
            Room room1 = getRoomByOrigin(origin1);
            if (room1 == null) continue;
            room1.discovered = true;
        }
    }

    private Room getRandomRoomOnFloor() {
        int randomIdx = (int) p.random(roomsOnFloor.size());
        Room randomRoom = createUniqueRoom(roomsOnFloor.get(randomIdx));
        return randomRoom;
    }

    private Room getRandomEmptyRoom() {
        int randomIdx = (int) p.random(rooms.size());
        Room randomRoom = createUniqueRoom(rooms.get(randomIdx));
        return randomRoom;
    }

    private void replaceNotConnectedDoors(Room room) {
        ArrayList<Vector2> startingDoors = room.getDoorLocations();
        for (Vector2 doorLocation : startingDoors) {
            replaceDoorInRoom(room, doorLocation);
        }
    }

    private void replaceDoorInRoom(Room room, Vector2 doorLocation) {
        if (!isDoorConnectedToRoom(room.origin, doorLocation)) {
            Tile currentTile = room.getTile(doorLocation.x, doorLocation.y);
            currentTile.tileType = TileType.WALL_TOP;
            Tile wallTile = getTile(getTileWallOrientation(doorLocation));
            Tile clone = wallTile.clone();
            clone.position = new Vector2(currentTile.position.x, currentTile.position.y);
            clone.rotation = 0;
            clone.collidable = true;
            clone.tileType = wallTile.tileType;

            room.replaceDoor(doorLocation.x, doorLocation.y, clone);
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

        replaceDoorInRoom(room, doorLocation);
    }

    private Room getFurthestRoom() {
        Room room = null;
        int x = 0;
        int y = 0;

        for (int i = 0; i < roomsOnFloor.size(); i++) {
            Room currentRoom = roomsOnFloor.get(i);
            if (currentRoom == null) continue;
            int newX = (int) Math.abs(currentRoom.origin.x);
            int newY = (int) Math.abs(currentRoom.origin.y);
            if (newX > Math.abs(x) || newY > Math.abs(y)) {
                if (currentRoom.roomType != RoomType.NORMAL) continue;

                room = currentRoom;
                x = (int) currentRoom.origin.x;
                y = (int) currentRoom.origin.y;
            }
        }

        return room;
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
