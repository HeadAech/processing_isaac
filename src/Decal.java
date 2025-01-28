import processing.core.PApplet;
import processing.core.PImage;

public class Decal {
    PApplet p;

    Vector2 position = new Vector2(0,0);

    PImage image;

    Decal(PApplet p, Vector2 position, String spritePath) {
        this.p = p;
        this.position = new Vector2(position);
        image = p.loadImage(spritePath);
    }

    void draw() {
        p.pushMatrix();

        p.image(image, position.x, position.y, image.width, image.height);

        p.popMatrix();
    }


}
