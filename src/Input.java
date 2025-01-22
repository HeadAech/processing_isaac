import java.util.Dictionary;
import java.util.Hashtable;

public class Input {
    Dictionary<Character, Boolean> pressedKeys = new Hashtable<Character, Boolean>();

    public Input() {
        pressedKeys.put('w', false);
        pressedKeys.put('s', false);
        pressedKeys.put('d', false);
        pressedKeys.put('a', false);
    }

    public void setPressed(Character character, boolean pressed) {
        pressedKeys.put(character, pressed);
    }

    public boolean getPressed(Character character) {
        return pressedKeys.get(character);
    }
}
