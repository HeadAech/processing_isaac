public class Transform {
    public Vector2 position;
    public Vector2 rotation;
    public Vector2 scale;

    public Transform(Vector2 position, Vector2 rotation, Vector2 scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Transform(Vector2 position, Vector2 rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public Transform(Vector2 position) {
        this.position = position;
    }

    public Transform() {
        this.position = new Vector2(0, 0);
        this.rotation = new Vector2(0, 0);
        this.scale = new Vector2(1, 1);
    }
}
