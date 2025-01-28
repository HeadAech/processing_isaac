import java.util.UUID;

enum CollisionShapeType {
    CIRCLE, BOX
}

enum TriggerType {
    DOOR, EMPTY, ITEM, SPIKES, BUTTON
}

public class CollisionShape implements Cloneable {

    UUID uuid = UUID.randomUUID();

    Vector2 position;
    Vector2 size;

    CollisionShapeType type = CollisionShapeType.BOX;

    float left;
    float top;
    float right;
    float bottom;

    boolean trigger = false;
    TriggerType triggerType = TriggerType.EMPTY;
    boolean triggered = false;
    boolean isWall = false;

    float offsetX = 0, offsetY = 0;

    private void setBounds() {
        left = position.x;
        top = position.y;
        right = position.x + size.x;
        bottom = position.y + size.y;
    }

    public CollisionShape(Vector2 position, Vector2 size) {
        this.position = new Vector2(position);
        this.size = new Vector2(size);
        setBounds();
    }

    public CollisionShape(Vector2 position, Vector2 size, CollisionShapeType type) {
        this.position = position;
        this.size = size;
        this.type = type;
        setBounds();
    }

    public void setPosition(Vector2 position) {
        this.position = new Vector2(position);
        this.position.x += offsetX;
        this.position.y += offsetY;
        setBounds();
    }

    public void setSize(Vector2 size) {
        this.size = new Vector2(size);
        setBounds();
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getSize() {
        return size;
    }

    public CollisionShapeType getType() {
        return type;
    }

    public Vector2 getCenter() {
        return new Vector2(position.x + size.x / 2, position.y + size.y / 2);
    }

    public boolean isTrigger() {
        return trigger;
    }

    public TriggerType getTriggerType() {
        return triggerType;
    }

    public void setTrigger(boolean trigger) {
        this.trigger = trigger;
    }

    public void setTriggerType(TriggerType triggerType) {
        this.trigger = true;
        this.triggerType = triggerType;
    }


    float currentTime = 0;
    float cooldown = 1;
    public void updateTrigger(float delta) {
        if (!trigger) return;

        if (triggered && triggerType != TriggerType.BUTTON) {
            currentTime += delta;
            if (currentTime >= cooldown) {
                triggered = false;
                currentTime = 0;
            }
        }
    }

    @Override
    public String toString() {
        return "CollisionShape [position=" + position.x + ", " + position.y  +
                ", size=" + size +
                ", type=" + type +
                ", left=" + left +
                ", right=" + right +
                ", top=" + top +
                ", bottom=" + bottom +
                "]";
    }

    @Override
    public CollisionShape clone() {
        try {
            CollisionShape copy = (CollisionShape) super.clone();  // Shallow copy of object
            copy.position = new Vector2(position.x, position.y);
            copy.size = new Vector2(size);
            copy.type = type;
            copy.trigger = trigger;
            copy.triggerType = triggerType;
            copy.triggered = triggered;
            copy.left = left;
            copy.top = top;
            copy.right = right;
            copy.bottom = bottom;
            copy.setBounds();
            return copy;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
