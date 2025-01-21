
import processing.core.PApplet;

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

    public void setup(){
        player = new Player(this, new Vector2(width/2, height/2));
        player.setScale(0.2f);
        player.createCollisionShape();

        projectiles = new ArrayList<>();

        enemies = new ArrayList<>();

//        Entity enemy = new Enemy(this, new Vector2(width/2, height/2), player);
//        enemy.setScale(0.2f);

//        enemies.add(enemy);

        ui = new Interface(this);

        ui.setPlayerHealth(player.health);

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
    }

    public void draw(){
        drawBackground();

        physics.checkCollisionForPlayerWithWalls(player);

        camera.apply();
        camera.x = player.transform.position.x;
        camera.y = player.transform.position.y;


        for (Room room: levelGenerator.roomsOnFloor) {
            pushMatrix();
            if (room != null) {
                translate(room.origin.x * room.width * 52 * room.scale.x, room.origin.y * room.height * 52 * room.scale.y);
                for (Tile tile: room.tiles) {
                    tile.draw();
                }
            }
            popMatrix();
        }


        translate(0,0);

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

        ui.setPlayerHealth(player.health);
        ui._update();


        if (!player.alive) {
            return;
        }
        player._update();


//        spawner._update();
//        spawnEnemies();

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

        if (spawner.shouldSpawn()) {
            Entity newEnemy = new Enemy(this, randomPoses.get((int) random(0, 3)), player);
            newEnemy.setScale(0.2f);
            enemies.add(newEnemy);
        }
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

        // movement
        if (key == 'w' || key == 'W') {
            player.direction.y = -1;
        }

        if (key == 's' || key == 'S') {
            player.direction.y = 1;
        }

        if (key == 'a' || key == 'A') {
            player.direction.x = -1;
        }

        if (key == 'd' || key == 'D') {
            player.direction.x = 1;
        }

        if (key == '-') {
            camera.zoom(0.9f);
        }

        if (key == '+' || key == '=') {
            camera.zoom(1.1f);
        }

        if (key == '§') {
            levelGenerator.regenerateFloor();
        }

        //shooting
        if (key == CODED) {
            if (keyCode == LEFT || keyCode == RIGHT || keyCode == UP || keyCode == DOWN) {
                Vector2 pDir = new Vector2(player.direction.x, player.direction.y);
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
        if (key == 'w' || key == 'W') {
            player.direction.y = 0;
        }

        if (key == 's' || key == 'S') {
            player.direction.y = 0;
        }

        if (key == 'a' || key == 'A') {
            player.direction.x = 0;
        }

        if (key == 'd' || key == 'D') {
            player.direction.x = 0;
        }
    }
}