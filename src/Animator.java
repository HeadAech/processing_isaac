import processing.core.PApplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Animator {
    PApplet p;

    Map<String, Animation> animations = new HashMap<>();

    String currentAnimation = null;

    Animator(PApplet p) {
        this.p = p;
    }

    public void addAnimation(String key, Animation animation) {
        animations.put(key, animation);
    }

    public void playAnimation(String key) {
        stopAllAnimations();
        currentAnimation = key;
        animations.get(key).play();
    }

    public void playAnimationIfNotPlaying(String key) {
        if (getAnimation(key) != null) {
            if (!getAnimation(key).playing) {
                stopAllAnimations();
                playAnimation(key);
            }
        }
    }

    public Animation getCurrentAnimation() {
        return animations.get(currentAnimation);
    }

    public void stopAllAnimations() {
        currentAnimation = null;
        for (String key: animations.keySet()) {
            animations.get(key).stop();
        }
    }

    public void update(float deltaTime) {
        for (String key: animations.keySet()) {
            Animation animation = animations.get(key);
            animation.update(deltaTime);
        }

    }

    public Animation getAnimation(String key) {
        return animations.get(key);
    }
}
