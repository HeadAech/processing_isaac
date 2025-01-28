
import processing.core.PApplet;
import processing.core.PImage;
import processing.sound.SoundFile;

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

    SoundFile basementMusic;

    SoundManager soundManager;

    public void setup(){
        windowTitle("The Binding of Isaac: Retarded");
        frameRate(120);
//        noSmooth();
        soundManager = new SoundManager(this);

        player = new Player(this, new Vector2((float) width /2, (float) height /2), "data/sprites/spritesheet/isaac_spritesheet.png");
        player.setScale(1.5f);
        player.createCollisionShape();

        projectiles = new ArrayList<>();

        enemies = new ArrayList<>();

//        Entity enemy = new Enemy(this, new Vector2(width/2, height/2), player);
//        enemy.setScale(0.2f);

//        enemies.add(enemy);


        basementMusic = new SoundFile(this, "data/music/basement_theme.mp3");


        heartsSpritesheet = loadImage("data/sprites/spritesheet/hearts.png");
        ui = new Interface(this, heartsSpritesheet);

        ui.setPlayer(player);
        ui.currentRoomIdx = currentRoomIdx;

        spawner = new Spawner(this);

        rocks = new ArrayList<>();

        physics = new Physics();

        levelGenerator = new LevelGenerator(this);
        levelGenerator.setPlayer(player);

        levelGenerator.prepareItems();
        levelGenerator.prepareEnemies();
        levelGenerator.prepareTiles();
        levelGenerator.prepareRooms();

        levelGenerator.generateFloor();
        ui.setLevelGenerator(levelGenerator);

        println("-- Floor --");
        println(levelGenerator.roomsOnFloor.size());


        camera = new Camera(this);

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
                        ui.currentRoomIdx = currentRoomIdx;
                        levelGenerator.discoverNearbyRooms(levelGenerator.roomsOnFloor.get(currentRoomIdx));
                        levelGenerator.onEnterRoom(currentRoomIdx);
                    }
                }
            }
        });


        Signals.ProjectileDestroyed.connect(uuid -> {
           for (Projectile projectile: projectiles) {
               if (projectile.uuid.equals(uuid)) {
                   projectiles.remove(projectile);
                   soundManager.playRandomSound("tear_impact");
                   break;
               }
           }
        });

        Signals.UpdateCollisionShapesForPhysics.connect(data -> {
            updateCollisionShapesForPhysics();
        });

        Signals.CreateProjectile.connect(projectile -> {
            projectiles.add(projectile);
        });

        updateCollisionShapesForPhysics();



//        basementMusic.play();
    }

    public void draw(){

        float currentTime = millis();
        deltaTime = (currentTime - lastFrameTime) / 1000.0f;
        lastFrameTime = currentTime;

//        println("Delta time: " + deltaTime);

        drawBackground();

        checkContinuousInput(deltaTime);

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
                    tile.draw(deltaTime);
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


                room.update(deltaTime);

            }


        }

        for (Entity enemy: currentRoom.enemies) {
            enemy._update(deltaTime);
            enemy._display();
            enemy.drawCollider();
            physics.checkCollisionForEntitiesWithWalls(enemy);

        }

//        translate(0,0);

        for (int i = projectiles.size() - 1; i >= 0; i--) {
            Projectile projectile = projectiles.get(i);
            projectile._update();
            physics.checkCollisionForProjectiles(projectile, deltaTime);
            physics.checkCollisionForProjectileWithEntity(projectile, currentRoom.enemies, player);
            if (!projectile.checkIfValid()) {
                if (projectile.destroyStart == 0) projectile.destroyStart = millis();
                if (millis() - projectile.destroyStart < projectile.destroyEnd) {
                    projectile.onDestroy();
                } else {
                    soundManager.playRandomSound("tear_impact");
                    projectiles.remove(i);  // Remove expired projectile
                }
            }


        }






        projectileDelay += deltaTime;
        player._update(deltaTime);
        player._display();
        if (player.showCollider) {
            player.drawCollider();
        }


        pushMatrix();
        translate(camera.x - width/2, camera.y - height/2);
        ui._update();
        popMatrix();
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

    float projectileDelay = 0.0f;

    public void keyPressed() {

        //movement

        inputManager.setPressed(keyCode, true);

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
            println(player.transform.position.x, player.transform.position.y, player.transform.rotation);
            player.transform.position.x = width/2;
            player.transform.position.y = height/2;
            currentRoomIdx = 0;
            ui.currentRoomIdx = 0;
            levelGenerator.regenerateFloor();
        }
        //shooting



    }

    public void checkContinuousInput(float deltaTime) {
        if (inputManager.getPressed("left") || inputManager.getPressed("right")  || inputManager.getPressed("up")  || inputManager.getPressed("down") ) {
            Vector2 pDir = new Vector2(player.velocity.x/2, player.velocity.y/2).normalized();
            if (inputManager.getPressed("left"))
                pDir.x = -1;
            else if (inputManager.getPressed("right"))
                pDir.x = 1;
            else if (inputManager.getPressed("up"))
                pDir.y = -1;
            else if (inputManager.getPressed("down"))
                pDir.y = 1;

            if (projectileDelay > 1/player.firerate) {
                projectileDelay = 0;
                soundManager.playRandomSound("tear_fire");
                Projectile p = new Projectile(this, new Vector2(player.transform.position.x - (float) player.spriteTop.width /2, player.transform.position.y - (float) player.spriteTop.height /2), pDir);
                p.damage = player.damage;
                projectiles.add(p);
            }
//                println("SHOOTING", pDir.x, pDir.y);
        }
    }

    public void keyReleased() {
        inputManager.setPressed(keyCode, false);
    }

    private void updateCollisionShapesForPhysics() {
        ArrayList<CollisionShape> collisionShapes = new ArrayList<>();
        for (Room room: levelGenerator.roomsOnFloor) {
            for (Tile tile: room.tiles) {
                if (tile.collisionShape != null)
                    collisionShapes.add(tile.collisionShape);
            }
        }
        physics.collisionShapes = collisionShapes;
    }
}