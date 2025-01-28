import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

enum ItemType {

}

public class Item {
    PApplet p;

    Vector2 position = new Vector2(0, 0);

    PImage image;

    int quality = 0;

    float minY, maxY;

    int dir = 1;

    float animationSpeed = 1.2f;

    Item(PApplet p, Vector2 position) {
        this.p = p;
        this.position = new Vector2(position);
    }

    Item(Item item) {
        this.p = item.p;
        this.position = new Vector2(item.position);
        this.image = item.image;
    }

    float target = minY;

    public void draw(float deltaTime) {

        p.pushMatrix();

        position.y = Util.lerp(position.y, target, deltaTime * animationSpeed);


        if (position.y < minY + 2f)
            target = maxY;
        else if (position.y > maxY - 2f)
            target = minY;

        p.imageMode(PConstants.CENTER);
        p.image(image, position.x + image.width - image.width/3, position.y, image.width, image.height);

        p.popMatrix();
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        minY = position.y - 5;
        maxY = position.y + 5;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public void setImage(PImage image) {
        this.image = image;
    }

    public void loadImage(String path) {
        this.image = p.loadImage(path);
        this.image.resize(52, 52);
    }





}
