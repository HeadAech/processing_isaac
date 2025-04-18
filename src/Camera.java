import processing.core.PApplet;

public class Camera {
    PApplet p;

    float x, y;  // Camera position
    float zoom = 1;  // Zoom level

    float targetX, targetY;

    Camera(PApplet p) {
        this.p = p;
        x = 0;
        y = 0;
    }

    // Move the camera by a certain amount
    void move(float dx, float dy) {
        x += dx;
        y += dy;
    }

    // Zoom the camera by a factor
    void zoom(float factor) {
        zoom *= factor;
    }

    public void update(float deltaTime) {
        x = Util.lerp(x, targetX, 8 * deltaTime);
        y = Util.lerp(y, targetY, 8 * deltaTime);
    }

    // Apply the camera transformations
    void apply() {
        p.translate(p.width/2, p.height/2);  // Move the origin to the center of the screen
        p.scale(zoom);  // Apply zoom
        p.translate(-x, -y);  // Move the view to the camera's position
    }
}
