import java.util.Dictionary;
import java.util.Hashtable;

public class Input {
    Dictionary<Integer, Boolean> pressedKeys = new Hashtable<Integer, Boolean>();

    public Input() {
        pressedKeys.put(87, false); // W
        pressedKeys.put(65, false); // A
        pressedKeys.put(83, false); // S
        pressedKeys.put(68, false); // D
        pressedKeys.put(38, false); // UP ARROW
        pressedKeys.put(37, false); // LEFT ARROW
        pressedKeys.put(40, false); // DOWN ARROW
        pressedKeys.put(39, false); // RIGHT ARROW
    }

    public void setPressed(Integer character, boolean pressed) {
        pressedKeys.put(character, pressed);
    }

    public boolean getPressed(Integer character) {
        return pressedKeys.get(character);
    }

    public boolean getPressed(String action) {
        return pressedKeys.get(getKeyCode(action));
    }

    private int getKeyCode(String action) {
        return switch (action) {
            case "w" -> 87;
            case "a" -> 65;
            case "s" -> 83;
            case "d" -> 68;
            case "up" -> 38;
            case "left" -> 37;
            case "down" -> 40;
            case "right" -> 39;
            default -> throw new IllegalStateException("Unexpected value: " + action);
        };
    }
}
