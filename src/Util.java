public class Util {

    public static float lerp(float a, float b, float t) {
        return a * (1.0f - t) + b * t;
    }
}
