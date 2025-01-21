public class Vector2 {
    float x;
    float y;

    Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    Vector2(Vector2 v) {
        this.x = v.x;
        this.y = v.y;
    }

    // Normalize the vector to a unit length
    public Vector2 normalized() {
        float length = (float) Math.sqrt(x * x + y * y);
        if (length != 0) {
            return new Vector2(x / length, y / length);
        } else {
            return new Vector2(0, 0); // Return zero vector if length is zero
        }
    }

    public Vector2 plus(Vector2 v) {
        return new Vector2(x + v.x, y + v.y);
    }

    public Vector2 plus(float value) {
        return new Vector2(x + value, y + value);
    }

    public Vector2 minus(Vector2 v) {
        return new Vector2(x - v.x, y - v.y);
    }

    public Vector2 subtract(float value) {
        return new Vector2(x - value, y - value);
    }

    public Vector2 multiply(Vector2 v) {
        return new Vector2(x * v.x, y * v.y);
    }

    public Vector2 multiply(float value) {
        return new Vector2(x * value, y * value);
    }

    public Vector2 divide(Vector2 v) {
        return new Vector2(x / v.x, y / v.y);
    }

    public static boolean areEqual(Vector2 v1, Vector2 v2) {
        return v1.x == v2.x && v1.y == v2.y;
    }
}
