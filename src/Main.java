
import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

public class Main extends PApplet {

    public static void main(String[] args) {
        PApplet.main("Main");
    }

    public void settings(){
        size(1170, 700);
    }

    Entity player;

    ArrayList<Projectile> projectiles;

    ArrayList<Entity> enemies;

    ArrayList<Rock> rocks;

    Interface ui;

    Spawner spawner;

    LevelGenerator levelGenerator;

    int currentRoomIdx = 0;

    Camera camera;

    Physics physics;

    float lastFrameTime;
    float deltaTime;

    Input inputManager;

    PImage heartsSpritesheet;

    public void setup(){
        windowTitle("The Binding of Isaac: Retarded");
        frameRate(120);
//        noSmooth();

        player = new Player(this, new Vector2((float) width /2, (float) height /2), "data/sprites/isaac/isaac_head_front.png");
        player.setScale(1.5f);
        player.createCollisionShape();

        projectiles = new ArrayList<>();

        enemies = new ArrayList<>();

//        Entity enemy = new Enemy(this, new Vector2(width/2, height/2), player);
//        enemy.setScale(0.2f);

//        enemies.add(enemy);

        heartsSpritesheet = loadImage("data/sprites/spritesheet/hearts.png");
        ui = new Interface(this, heartsSpritesheet);

        ui.setPlayer(player);

        spawner = new Spawner(this);

        rocks = new ArrayList<>();

        physics = new Physics();

        levelGenerator = new LevelGenerator(this);
        levelGenerator.prepareTiles();
        levelGenerator.prepareRooms();
        for(Room room: levelGenerator.rooms) {
            println(room.name);
        }
        levelGenerator.generateFloor();
        println("-- Floor --");
        println(levelGenerator.roomsOnFloor.size());

        ArrayList<CollisionShape> collisionShapes = new ArrayList<>();
        for (Room room: levelGenerator.roomsOnFloor) {
            for (Tile tile: room.tiles) {
                if (tile.collisionShape != null)
                    collisionShapes.add(tile.collisionShape);
            }
        }
        physics.collisionShapes = collisionShapes;
        camera = new Camera(this);
//        camera.zoom(0.1f);
//        spawnRocks();

        lastFrameTime = millis();
        inputManager = new Input();
        player.setInput(inputManager);

        Signals.EnteredDoor.connect(data -> {
            println("-- Entered Door --");
            println(data.x + " " + data.y);
            Room r = levelGenerator.roomsOnFloor.get(currentRoomIdx);
            if (r != null) {
                Vector2 doorLocation =  r.getTilePositionByCollision(data);
                Vector2 originDir = levelGenerator.decideOriginDirection(doorLocation);
                println("Found room in direction: ", originDir.x, originDir.y);
                int nextRoomIdx = levelGenerator.getRoomIndex(new Vector2(r.origin.x + originDir.x, r.origin.y + originDir.y));
                if (nextRoomIdx != -1) {
                    currentRoomIdx = nextRoomIdx;
                    Vector2 doorLocationInNextRoom = levelGenerator.getDoorPositionInNextRoom(originDir, nextRoomIdx);
                    if (doorLocationInNextRoom != null) {
                        Room nextRoom = levelGenerator.roomsOnFloor.get(currentRoomIdx);
                        Tile doorTile = nextRoom.getDoorTile(doorLocationInNextRoom.x, doorLocationInNextRoom.y);
                        Vector2 globalPos = doorTile.globalPosition;
                        float offsetFromDoor = 25;
                        if (originDir.y == 1 || originDir.y == -1) {
                            player.transform.position.y = globalPos.y + ((doorTile.height + offsetFromDoor) * originDir.y);
                            player.resetVelocity();
                        } else {
                            player.transform.position.x = globalPos.x + ((doorTile.width + offsetFromDoor) * originDir.x);
                            player.resetVelocity();
                        }
                    }
                }
            }
        });
    }

    public void draw(){

        float currentTime = millis();
        deltaTime = (currentTime - lastFrameTime) / 1000.0f;
        lastFrameTime = currentTime;

//        println("Delta time: " + deltaTime);

        drawBackground();

        physics.checkCollisionForPlayerWithWalls(player, deltaTime);

        camera.update(deltaTime);
        camera.apply();
        Room currentRoom = levelGenerator.roomsOnFloor.get(currentRoomIdx);
        if (currentRoom != null) {
            int signX = Integer.signum((int) currentRoom.origin.x);
            int signY = Integer.signum((int) currentRoom.origin.y);
            camera.targetX = signX* (Math.abs(currentRoom.origin.x) * 2) * (currentRoom.width/2) * 52 * currentRoom.scale.x + width/2;
            camera.targetY = signY* (Math.abs(currentRoom.origin.y) * 2) * (currentRoom.height/2) * 52 * currentRoom.scale.y + height/2;
        }




        for (Room room: levelGenerator.roomsOnFloor) {
            if (room != null) {
                pushMatrix();
                translate(room.origin.x * room.width * 52 * room.scale.x, room.origin.y * room.height * 52 * room.scale.y);
                for (Tile tile: room.tiles) {
                    tile.draw();
                }
                popMatrix();

                pushMatrix();
                for (Tile tile: room.tiles) {
                    if (tile.collisionShape != null && tile.drawCollider){
                        tile.drawCollider();
                    }
                    if (tile.collisionShape != null) {
                        tile.collisionShape.updateTrigger(deltaTime);
                    }
                }
                popMatrix();

            }


        }


//        translate(0,0);

        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile._update();

            if (!projectile.checkIfValid()) {
                if (projectile.destroyStart == 0) projectile.destroyStart = millis();
                if (millis() - projectile.destroyStart < projectile.destroyEnd) {
                    projectile.onDestroy();
                } else {
                    projectiles.remove(i);  // Remove expired projectile
                }
            }


        }

        pushMatrix();
        translate(camera.x - width/2, camera.y - height/2);
        ui._update();
        popMatrix();


        if (!player.alive) {
            return;
        }
        player._update(deltaTime);
        if (player.showCollider) {
            player.drawCollider();
        }



    }

    public void spawnEnemies(){
        ArrayList<Vector2> randomPoses = new ArrayList<>();
        Vector2 randomPos = new Vector2(-100, height/2);
        Vector2 randomPos1 = new Vector2(width + 100, height/2);
        Vector2 randomPos2 = new Vector2(width/2, height + 100);
        Vector2 randomPos3 = new Vector2(width/2, -100);

        randomPoses.add(randomPos);
        randomPoses.add(randomPos1);
        randomPoses.add(randomPos2);
        randomPoses.add(randomPos3);

//        if (spawner.shouldSpawn()) {
//            Entity newEnemy = new Enemy(this, randomPoses.get((int) random(0, 3)), player, "data/sprites/isaac/isaac_head_front.png");
//            newEnemy.setScale(0.2f);
//            enemies.add(newEnemy);
//        }
    }

    public void drawBackground() {
        background(200);
    }

    public void spawnRocks() {
        float noiseScale = 0.5f; // Adjust noise scale for rock density and placement
        for (int i = 0; i < width; i += 20) { // Larger step to reduce rock density
            for (int j = 0; j < height; j += 20) {
                float noiseVal = noise(i * noiseScale, j * noiseScale);

                // Adjust threshold for sparse rock placement
                if (noiseVal > 0.85) {
                    Vector2 position = new Vector2(i, j);
                    Vector2 scale = new Vector2(0.2f + noiseVal * 0.1f, 0.2f + noiseVal * 0.1f); // Scale based on noise
                    Rock rock = new Rock(this, position, scale);
                    rocks.add(rock);
                }
            }
        }
    }


    public void keyPressed() {

        //movement

        inputManager.setPressed(key, true);

        if (key == '-') {
            this.camera.zoom(0.9f);
        }

        if (key == '+' || key == '=') {
            camera.zoom(1.1f);
        }

        if (key == 'p') {
            player.damage(1);
        }

        if (key == '§') {
            levelGenerator.regenerateFloor();
        }
        //shooting
        if (key == CODED) {
            if (keyCode == LEFT || keyCode == RIGHT || keyCode == UP || keyCode == DOWN) {
                Vector2 pDir = new Vector2(player.acceleration.x, player.acceleration.y);
                switch (keyCode) {
                    case LEFT:
                        pDir.x = -1;
                        break;
                    case RIGHT:
                        pDir.x = 1;
                        break;
                    case UP:
                        pDir.y = -1;
                        break;
                    case DOWN:
                        pDir.y = 1;
                        break;
                }

                Projectile p = new Projectile(this, player.transform.position, pDir);
                p.transform.scale.x = 0.2f;
                p.transform.scale.y = 0.2f;
                projectiles.add(p);
//                println("SHOOTING", pDir.x, pDir.y);
            }
        }

    }

    public void keyReleased() {
        inputManager.setPressed(key, false);
    }
}