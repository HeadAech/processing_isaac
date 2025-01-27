import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;

enum PlayStyle {
    PS_NORMAL,
    PS_LOOP,
    PS_LOOP_REVERSE,
    PS_PINGPONG,
    PS_REVERSE,
    PS_PINGPONG_REVERSE,
}

public class Animation {
    PApplet p;

    PImage spritesheet;
    PImage frame;

    Vector2 frameSize = new Vector2(32, 32);

    float currentTime = 0.0f;
    float duration = 0.0f;

    ArrayList<Vector2> framePositions = new ArrayList<>();

    int currentFrame = 0;

    int direction = 1;

    int playCount = 0;

    PlayStyle playStyle = PlayStyle.PS_NORMAL;

    boolean playing = false;

    private void updateFrame() {
        if (currentFrame >= framePositions.size() || currentFrame < 0) {
            currentFrame = 0;
        }
        Vector2 framePosition = framePositions.get(currentFrame);
        frame = getTile((int) framePosition.x, (int) framePosition.y);
    }

    private PImage getTile(int x, int y) {
        int sx = (int) (x * frameSize.x);
        int sy = (int) (y * frameSize.y);
        return spritesheet.get(sx, sy, (int) frameSize.x, (int) frameSize.y);
    }

    public Animation(PApplet p, PImage spritesheet, PImage frame) {
        this.p = p;
        this.frame = frame;
        this.spritesheet = spritesheet;
        this.frame = spritesheet.get();
    }

    public void update(float deltaTime) {
        if (!playing) return;
        if (playStyle == PlayStyle.PS_NORMAL && playCount > 0) return;

        currentTime += deltaTime;
        if (currentTime >= duration) {
            currentTime = 0.0f;

            switch (playStyle) {
                case PS_NORMAL:
                    currentFrame = (currentFrame + 1) % framePositions.size();
                    if (currentFrame == framePositions.size() - 1) {
                        stop();
                        playCount++;
                    }
                    break;
                case PS_LOOP:
                    currentFrame = (currentFrame + 1) % framePositions.size();
                    break;
                case PS_PINGPONG:
                    currentFrame += direction;
                    if (currentFrame >= framePositions.size() - 1 || currentFrame <= 0) {
                        direction *= -1;
                    }
                    break;
                case PS_PINGPONG_REVERSE:
                    currentFrame += direction;
                    if (currentFrame >= framePositions.size() - 1 || currentFrame <= 0) {
                        direction *= -1;
                    }
                    break;
                case PS_REVERSE:
                    currentFrame = currentFrame <= 0 ? framePositions.size() - 1 : currentFrame - 1;
                    break;
                default:
                    currentFrame = (currentFrame + 1) % framePositions.size();
                    break;
            }

            updateFrame();


        }
    }

    void setFirstFrame() {
        updateFrame();
    }

    void addFrame(Vector2 position) {
        framePositions.add(position);
    }

    void setFramePositions(ArrayList<Vector2> framePositions) {
        this.framePositions = framePositions;
    }

    void setFrameSize(int width, int height) {
        frameSize.x = width;
        frameSize.y = height;
    }

    void setDuration(float duration) {
        this.duration = duration;
    }

    void play() {
        if (playStyle == PlayStyle.PS_REVERSE || playStyle == PlayStyle.PS_PINGPONG_REVERSE) {
            currentFrame = framePositions.size() - 1;
            direction = -1;
        } else {
            currentFrame = 0;
        }

        currentTime = 0.0f;
        playCount = 0;
        playing = true;
        updateFrame();
    }

    void stop() {
        playing = false;
    }

    boolean isPlaying() {
        return playing;
    }

    void setPlayStyle(PlayStyle playStyle) {
        this.playStyle = playStyle;
    }

    PlayStyle getPlayStyle() {
        return playStyle;
    }
}
