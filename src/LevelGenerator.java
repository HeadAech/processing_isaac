import processing.core.PApplet;
import processing.core.PImage;

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

    ArrayList<Enemy> enemies = new ArrayList<>();


    ArrayList<Item> items = new ArrayList<>();

    int maxRoomsOnFloor = 15;

    ArrayList<Room> roomsOnFloor = new ArrayList<>();

    Entity player;

    int currentRoomIdx = 0;

    LevelGenerator(PApplet p) {
        this.p = p;

        Signals.SpawnEnemy.connect(SpawnEnemy -> {
            EnemyType type = SpawnEnemy.type;
            Vector2 position = new Vector2(SpawnEnemy.position);

            Enemy e = new Enemy(getEnemy(type), type);
            e.transform.position.x = position.x;
            e.transform.position.y = position.y;

            roomsOnFloor.get(currentRoomIdx).addEnemy(e);
        });
    }

    public void setPlayer(Entity player) {
        this.player = player;
    }

    public void prepareItems() {

        Item bloodOfTheMartyr = new Item(p, new Vector2(0, 0));
        bloodOfTheMartyr.loadImage("data/sprites/items/blood_of_the_martyr.png");
        bloodOfTheMartyr.setQuality(3);
        bloodOfTheMartyr.addStatModifier("damage", 1);
        bloodOfTheMartyr.addStatModifier("range", 2);
        bloodOfTheMartyr.addStatModifier("firerateMultiplier", 0.1f);
        items.add(bloodOfTheMartyr);

        Item belt = new Item(p, new Vector2(0, 0));
        belt.loadImage("data/sprites/items/belt.png");
        belt.setQuality(1);
        belt.addStatModifier("speed", 0.2f);
        items.add(belt);

        Item cricketsHead = new Item(p, new Vector2(0, 0));
        cricketsHead.loadImage("data/sprites/items/crickets_head.png");
        cricketsHead.setQuality(4);
        cricketsHead.addStatModifier("damageMultiplier", 1f);
        items.add(cricketsHead);

        Item meat = new Item(p, new Vector2(0, 0));
        meat.loadImage("data/sprites/items/meat.png");
        meat.setQuality(1);
        meat.addStatModifier("damage", 0.3f);
        meat.addStatModifier("healthUp", 2);
        items.add(meat);

        Item smallRock = new Item(p, new Vector2(0, 0));
        smallRock.loadImage("data/sprites/items/small_rock.png");
        smallRock.setQuality(2);
        smallRock.addStatModifier("damage", 1f);
        smallRock.addStatModifier("speed", -0.2f);
        items.add(smallRock);

        Item soyMilk = new Item(p, new Vector2(0, 0));
        soyMilk.loadImage("data/sprites/items/soy_milk.png");
        soyMilk.setQuality(2);
        soyMilk.addStatModifier("firerateMultiplier", 5f);
        soyMilk.addStatModifier("damageMultiplier", -0.7f);
        soyMilk.addStatModifier("damage", -2);
        items.add(soyMilk);

    }

    public void prepareEnemies() {

        //dip
        Enemy dip = new Enemy(p, new Vector2(p.width/2 - 100, p.height/2), player, "data/sprites/spritesheet/dip.png", EnemyType.DIP);
        dip.createCollisionShape();
        dip.health = 5.5f;

        PImage dipSprite = p.loadImage("data/sprites/spritesheet/dip.png");

        Animation idle = new Animation(p, dipSprite, dip.spriteBottom);
        idle.setPlayStyle(PlayStyle.PS_LOOP);
        idle.addFrame(new Vector2(0, 0));
        idle.addFrame(new Vector2(1, 0));
        idle.addFrame(new Vector2(2, 0));
        idle.setDuration(0.15f);
        dip.animatorBottom.addAnimation("idle", idle);
        dip.animatorBottom.playAnimation("idle");


        enemies.add(dip);

        //pooter
        Enemy pooter = new Enemy(p, new Vector2(0,0), player, "data/sprites/spritesheet/pooter.png", EnemyType.POOTER);
        pooter.createCollisionShape();
        pooter.health = 8.5f;

        PImage pooterSprite = p.loadImage("data/sprites/spritesheet/pooter.png");

        Animation pooterIdle = new Animation(p, pooterSprite, pooter.spriteBottom);
        pooterIdle.setPlayStyle(PlayStyle.PS_LOOP);
        pooterIdle.addFrame(new Vector2(0, 3));
        pooterIdle.addFrame(new Vector2(1, 3));
        pooterIdle.addFrame(new Vector2(2, 3));
        pooterIdle.addFrame(new Vector2(3, 3));
        pooterIdle.setDuration(0.02f);
        pooter.animatorBottom.addAnimation("idle", pooterIdle);
        pooter.animatorBottom.playAnimation("idle");

        Animation pooterFire = new Animation(p, pooterSprite, pooter.spriteBottom);
        pooterFire.setPlayStyle(PlayStyle.PS_NORMAL);
        pooterFire.addFrame(new Vector2(0, 0));
        pooterFire.addFrame(new Vector2(1, 0));
        pooterFire.addFrame(new Vector2(2, 0));
        pooterFire.addFrame(new Vector2(3, 0));

        pooterFire.addFrame(new Vector2(0, 1));
        pooterFire.addFrame(new Vector2(1, 1));
        pooterFire.addFrame(new Vector2(2, 1));
        pooterFire.addFrame(new Vector2(3, 1));

        pooterFire.addFrame(new Vector2(0, 2));
        pooterFire.addFrame(new Vector2(1, 2));
        pooterFire.addFrame(new Vector2(2, 2));
        pooterFire.addFrame(new Vector2(3, 2));
        pooterFire.setDuration(0.02f);

        pooter.animatorBottom.addAnimation("fire", pooterFire);


        enemies.add(pooter);



    }

    public void prepareBosses() {
        Enemy dingle = new Enemy(p, new Vector2(0,0), player, "data/sprites/spritesheet/dingle_spritesheet.png", EnemyType.DINGLE);
//        dingle.spriteBottom.resize(300, 300);

//        dingle.transform.scale.x = 2;
//        dingle.transform.scale.y = 2;
        dingle.spriteBottom.resize(96, 96);
        dingle.createCollisionShape();
        dingle.health = 650;
        dingle.isBoss = true;

        PImage dingleSprite = p.loadImage("data/sprites/spritesheet/dingle_spritesheet.png");

        Animation dingleIdle = new Animation(p, dingleSprite, dingle.spriteTop);
        dingleIdle.frameSize.x = 96;
        dingleIdle.frameSize.y = 96;
        dingleIdle.setPlayStyle(PlayStyle.PS_LOOP);
        dingleIdle.addFrame(new Vector2(0, 0));
        dingleIdle.addFrame(new Vector2(1, 0));
        dingleIdle.addFrame(new Vector2(2, 0));
        dingleIdle.addFrame(new Vector2(3, 0));
        dingleIdle.addFrame(new Vector2(4, 0));
        dingleIdle.setDuration(0.1f);
        dingle.animatorBottom.addAnimation("idle", dingleIdle);
        dingle.animatorBottom.playAnimation("idle");

        Animation dingleCharge = new Animation(p, dingleSprite, dingle.spriteBottom);
        dingleCharge.frameSize.x = 96;
        dingleCharge.frameSize.y = 96;
        dingleCharge.setPlayStyle(PlayStyle.PS_NORMAL);
        dingleCharge.addFrame(new Vector2(0, 1));
        dingleCharge.addFrame(new Vector2(1, 1));
        dingleCharge.addFrame(new Vector2(2, 1));
        dingleCharge.addFrame(new Vector2(3, 1));
        dingleCharge.addFrame(new Vector2(4, 1));
        dingleCharge.addFrame(new Vector2(5, 1));
        dingleCharge.setDuration(0.3f);
        dingle.animatorBottom.addAnimation("charge", dingleCharge);
//        dingle.animatorBottom.playAnimation("charge");

        Animation dingleExhausted = new Animation(p, dingleSprite, dingle.spriteBottom);
        dingleExhausted.frameSize.x = 96;
        dingleExhausted.frameSize.y = 96;
        dingleExhausted.setPlayStyle(PlayStyle.PS_PINGPONG);
        dingleExhausted.addFrame(new Vector2(0, 2));
        dingleExhausted.addFrame(new Vector2(1, 2));
        dingleExhausted.addFrame(new Vector2(2, 2));
        dingleExhausted.addFrame(new Vector2(3, 2));
        dingleExhausted.addFrame(new Vector2(4, 2));
        dingleExhausted.setDuration(0.2f);
        dingle.animatorBottom.addAnimation("exhausted", dingleExhausted);

        Animation dingleSpawn = new Animation(p, dingleSprite, dingle.spriteBottom);
        dingleSpawn.frameSize.x = 96;
        dingleSpawn.frameSize.y = 96;
        dingleSpawn.setPlayStyle(PlayStyle.PS_PINGPONG);
        dingleSpawn.addFrame(new Vector2(0, 3));
        dingleSpawn.addFrame(new Vector2(1, 3));
        dingleSpawn.addFrame(new Vector2(2, 3));
        dingleSpawn.setDuration(0.2f);
        dingle.animatorBottom.addAnimation("spawn", dingleSpawn);

        Animation dingleShoot = new Animation(p, dingleSprite, dingle.spriteBottom);
        dingleShoot.frameSize.x = 96;
        dingleShoot.frameSize.y = 96;
        dingleShoot.setPlayStyle(PlayStyle.PS_NORMAL);
        dingleShoot.addFrame(new Vector2(0, 4));
        dingleShoot.addFrame(new Vector2(1, 4));
        dingleShoot.addFrame(new Vector2(2, 4));
        dingleShoot.addFrame(new Vector2(3, 4));
        dingleShoot.addFrame(new Vector2(4, 4));
        dingleShoot.addFrame(new Vector2(5, 4));
        dingleShoot.addFrame(new Vector2(6, 4));
        dingleShoot.addFrame(new Vector2(7, 4));
        dingleShoot.setDuration(0.18f);
        dingle.animatorBottom.addAnimation("shoot", dingleShoot);

        Animation dingleDeath = new Animation(p, dingleSprite, dingle.spriteBottom);
        dingleDeath.frameSize.x = 96;
        dingleDeath.frameSize.y = 96;
        dingleDeath.setPlayStyle(PlayStyle.PS_NORMAL);
        dingleDeath.addFrame(new Vector2(0, 5));
        dingleDeath.addFrame(new Vector2(1, 5));
        dingleDeath.addFrame(new Vector2(2, 5));
        dingleDeath.addFrame(new Vector2(3, 5));
        dingleDeath.addFrame(new Vector2(4, 5));
        dingleDeath.addFrame(new Vector2(5, 5));
        dingleDeath.addFrame(new Vector2(6, 5));
        dingleDeath.addFrame(new Vector2(7, 5));
        dingleDeath.addFrame(new Vector2(8, 5));
        dingleDeath.setDuration(0.1f);
        dingle.animatorBottom.addAnimation("death", dingleDeath);

        enemies.add(dingle);
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
            if (t.character == c) {
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
                                if (tile.collidable) {
                                    tile.createCollisionShape(room.origin, null);
                                    if (tile.tileType == TileType.WALL_LEFT
                                            || tile.tileType == TileType.WALL_RIGHT
                                            || tile.tileType == TileType.WALL_BOTTOM
                                            || tile.tileType == TileType.WALL_TOP
                                            || tile.tileType == TileType.WALL_CORNER_TOP_LEFT
                                            || tile.tileType == TileType.WALL_CORNER_TOP_RIGHT
                                            || tile.tileType == TileType.WALL_CORNER_BOTTOM_LEFT
                                            || tile.tileType == TileType.WALL_CORNER_BOTTOM_RIGHT) {
                                        tile.collisionShape.isWall = true;
                                    }
                                }
                                if (tile.tileType == TileType.DOOR) {
                                    float rot = getDoorRotation(tile.position);
                                    tile.setRotation(rot);
                                    tile.collisionShape.setTriggerType(TriggerType.DOOR);
                                    Tile wallTile = new Tile(p, getTile('W'));
                                    wallTile.setPosition(tile.position);
                                    wallTile.setRotation(rot);
                                    room.addTile(wallTile);
                                }
                                if (tile.tileType == TileType.ROCK || tile.tileType == TileType.POOP) {
//                                    tile.scale.x = 0.8f;
//                                    tile.scale.y = 0.8f;
                                    tile.collisionShape.setSize(new Vector2(tile.width * tile.scale.x, tile.height * tile.scale.y));
                                    tile.destructible = true;
                                    Tile floorTile = new Tile(p, getTile('.'));
                                    floorTile.setPosition(tile.position);
                                    room.addTile(floorTile);
                                }
                                if (tile.tileType == TileType.YELLOW_BUTTON) {
                                    tile.collisionShape.setSize(new Vector2(tile.width * tile.scale.x, tile.height * tile.scale.y));
                                    Tile floorTile = new Tile(p, getTile('.'));
                                    floorTile.setPosition(tile.position);
                                    room.addTile(floorTile);
                                }
                                if (tile.tileType == TileType.SPIKES) {
                                    tile.collisionShape.setSize(new Vector2(tile.width * tile.scale.x, tile.height * tile.scale.y));
                                    tile.collisionShape.setTriggerType(TriggerType.SPIKES);
                                    Tile floorTile = new Tile(p, getTile('.'));
                                    floorTile.setPosition(tile.position);
                                    room.addTile(floorTile);
                                }
                                if (tile.tileType == TileType.ENEMY_SPAWN) {
                                    int randEnemyIdx = (int) p.random(enemies.size());
                                    EnemyType enemyType = EnemyType.DIP;
                                    if (tile.name.contains("pooter")) enemyType = EnemyType.POOTER;
                                    else if (tile.name.contains("dip")) enemyType = EnemyType.DIP;
                                    else if (tile.name.contains("dingle")) enemyType = EnemyType.DINGLE;
                                    Enemy enemy = getEnemy(enemyType);

                                    enemy.transform.position.x = tile.getGlobalPosition(room.origin, room.scale).x;
                                    enemy.transform.position.y = tile.getGlobalPosition(room.origin, room.scale).y;
                                    room.addEnemy(enemy);
                                }
                                if (tile.tileType == TileType.ITEM_PEDESTAL) {
                                    tile.collisionShape.setSize(new Vector2(tile.textureImage.width * tile.scale.x, tile.textureImage.height * tile.scale.y));
                                    Vector2 newCollisionShapePos = tile.collisionShape.getPosition();
                                    newCollisionShapePos.x += tile.textureImage.width * tile.scale.x * 22;
                                    newCollisionShapePos.y += tile.textureImage.height * tile.scale.y;
                                    tile.collisionShape.setPosition(newCollisionShapePos);
                                    tile.collisionShape.trigger = true;
                                    tile.collisionShape.setTriggerType(TriggerType.ITEM);

                                    int randomitemIdx = (int) p.random(items.size());
                                    Item item = items.get(randomitemIdx);
                                    Vector2 pos = new Vector2(tile.getGlobalPosition(room.origin, room.scale).x, tile.getGlobalPosition(room.origin, room.scale).y);
                                    item.setPosition(pos);
                                    tile.setItem(item);

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

                        if (room.name.contains("treasure"))
                            room.roomType = RoomType.TREASURE;
                        else if (room.name.contains("boss"))
                            room.roomType = RoomType.BOSS;
                        else
                            room.roomType = RoomType.NORMAL;
                        rooms.add(room);
                        j++;


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private Enemy getEnemy(EnemyType type) {
        for (Enemy enemy: enemies) {
            if (enemy.type == type) {
                return enemy;
            }
        }
        return null;
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
        Room startingRoom = createUniqueRoom(getStartingRoom());
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
            Room randomRoom = getRandomEmptyRoom(RoomType.NORMAL);
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
            Room randomEmptyRoom = getRandomEmptyRoom(RoomType.NORMAL);
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
            Room randomEmptyRoom = getRandomEmptyRoom(type);
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
        int enemyIdx = 0;
        for (Tile tile : originalRoom.tiles) {
            Tile newTile = new Tile(p, tile.character, tile.tileType, tile.name, tile.spritePath);
            newTile.setPosition(new Vector2(tile.position.x, tile.position.y)); // Clone position
            newTile.setRotation(tile.rotation);
            newTile.collidable = tile.collidable;
            newTile.textureImage = tile.textureImage;
            newTile.destructible = tile.destructible;
            newTile.health = tile.health;
            if (tile.item != null)
                newTile.item = new Item(tile.item);
            if (tile.collidable) {
                newTile.createCollisionShape(uniqueRoom.origin, null);
                if (tile.tileType == TileType.DOOR) {
                    newTile.collisionShape.setTriggerType(TriggerType.DOOR);
                }
                if (tile.tileType == TileType.ITEM_PEDESTAL) {
                    int randomitemIdx = (int) p.random(items.size());
                    Item item = new Item(items.get(randomitemIdx));
                    Vector2 pos = new Vector2(tile.getGlobalPosition(uniqueRoom.origin, uniqueRoom.scale).x, tile.getGlobalPosition(uniqueRoom.origin, uniqueRoom.scale).y);
                    item.setPosition(pos);
                    newTile.setItem(item);

                    newTile.collisionShape.setSize(new Vector2(newTile.textureImage.width * newTile.scale.x, newTile.textureImage.height * newTile.scale.y));
                    newTile.collisionShape.offsetX = 20;
                    newTile.collisionShape.offsetY = 20;
                    newTile.collisionShape.trigger = true;
                    newTile.collisionShape.setTriggerType(TriggerType.ITEM);
                }

                if (tile.tileType == TileType.YELLOW_BUTTON) {
                    Vector2 pos = new Vector2(tile.getGlobalPosition(uniqueRoom.origin, uniqueRoom.scale).x, tile.getGlobalPosition(uniqueRoom.origin, uniqueRoom.scale).y);

                    newTile.collisionShape.setSize(new Vector2(32 * newTile.scale.x, 29 * newTile.scale.y));
                    newTile.collisionShape.offsetX = 17;
                    newTile.collisionShape.offsetY = 17;
                    newTile.collisionShape.trigger = true;
                    newTile.collisionShape.setTriggerType(TriggerType.BUTTON);
                }

                if (tile.tileType == TileType.SPIKES) {
                    newTile.collisionShape.setTriggerType(TriggerType.SPIKES);
                }
            }
            if (tile.tileType == TileType.ENEMY_SPAWN) {
                Enemy enemy = originalRoom.enemies.get(enemyIdx);
                Enemy newEnemy = new Enemy(enemy, enemy.type);
//                newEnemy.createCollisionShape();
                newEnemy.health = enemy.health;
                newEnemy.transform.scale = new Vector2(enemy.transform.scale.x, enemy.transform.scale.y);
//                newEnemy.createCollisionShape();
                newEnemy.spriteBottom.resize(enemy.spriteBottom.width, enemy.spriteBottom.height);
                newEnemy.transform.position.x = tile.getGlobalPosition(originalRoom.origin, originalRoom.scale).x;
                newEnemy.transform.position.y = tile.getGlobalPosition(originalRoom.origin, originalRoom.scale).y;
                newEnemy.collisionShape.setSize(enemy.collisionShape.size);


                uniqueRoom.addEnemy(newEnemy);
                enemyIdx++;
            }
//            uniqueRoom.enemies.clear();
//            for (Enemy enemy: originalRoom.enemies) {
//                uniqueRoom.addEnemy(new Enemy(enemy));
//            }
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

    public void onEnterRoom(int idx) {
        Room room = roomsOnFloor.get(idx);
        if (room == null) return;

        if (!room.enemies.isEmpty()) {
            room.lock();
        }
    }

    private Room getRandomRoomOnFloor() {
        int randomIdx = (int) p.random(roomsOnFloor.size());
        Room randomRoom = createUniqueRoom(roomsOnFloor.get(randomIdx));
        return randomRoom;
    }

    private Room getRandomEmptyRoom(RoomType type) {
        int randomIdx = (int) p.random(rooms.size());
        Room r = rooms.get(randomIdx);
        while (r.roomType != type){
            randomIdx = (int) p.random(rooms.size());
            r = rooms.get(randomIdx);
        }

        return createUniqueRoom(r);
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
