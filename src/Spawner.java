import processing.core.PApplet;

enum Difficulty {
    EASY,
    MEDIUM,
    HARD
}

public class Spawner {
    PApplet p;

    Difficulty difficulty = Difficulty.EASY;

    int spawnTime = 0;
    float cooldown = 5000;


    Spawner(PApplet p) {
        this.p = p;
    }

    public void _update() {
        if (p.millis() > 10000 && p.millis() < 20000 && difficulty != Difficulty.MEDIUM) {
            difficulty = Difficulty.MEDIUM;
        }

        if (p.millis() > 20000 && difficulty != Difficulty.HARD) {
            difficulty = Difficulty.HARD;
        }
    }

    public boolean shouldSpawn() {
        if (p.millis() - spawnTime > cooldown) {
            p.println(difficulty);
            spawnTime = p.millis();
            switch (difficulty){
                case EASY:
                    cooldown = p.random(2000, 4000);
                    break;
                case MEDIUM:
                    cooldown = p.random(1000, 3000);
                    break;
                case HARD:
                    cooldown = p.random(500, 2000);
                    break;
            }

            return true;
        }
        return false;
    }

}
