import processing.core.PApplet;

public class Interface {

    PApplet p;

    int score = 0;

    Interface(PApplet p) {
        this.p = p;
    }

    float playerHealth;

    public void setPlayerHealth(float v) {
        playerHealth = v;
    }

    public void _update() {

        _display();
    }

    public void _display() {
        p.textAlign(p.LEFT);
        p.pushMatrix();
        p.color(0);
        p.fill(0);
        p.textSize(32);
        p.text("HP: " + p.str((int)playerHealth), 0, p.textAscent());
        p.text("Score: " + p.str(score), 0, p.textAscent() * 2);

        String fps = "fps: " + p.round(p.frameRate);
        p.text(fps, p.width - p.textWidth(fps) - 5, p.height - p.textAscent());

        if (playerHealth <= 0) {
            p.color(230, 10, 0);
            p.textAlign(p.CENTER, p.CENTER);
            p.text("GAME OVER", p.width / 2, p.height / 2);
            p.text("SCORE   " + score, p.width / 2, p.height / 2 + 20 + p.textAscent());

        }

        p.popMatrix();
    }


}
