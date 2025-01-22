
enum CollisionShapeType {
    CIRCLE, BOX
}

enum TriggerType {
    DOOR, EMPTY
}

public class CollisionShape implements Cloneable {

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

        if (triggered) {
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
            copy.setBounds();
            return copy;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
