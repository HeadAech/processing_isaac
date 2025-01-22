import java.lang.reflect.Array;
import java.util.ArrayList;

public class Physics {

    ArrayList<CollisionShape> collisionShapes;

    public Physics() {}

    float currentTime = 0;
    float triggerCooldown = 1;

    public void checkCollisionForPlayerWithWalls(Entity player, float deltaTime) {
        ArrayList<CollisionShape> collidingWith = new ArrayList<>();
        if (currentTime <= triggerCooldown)
            currentTime += deltaTime;

        for (CollisionShape collisionShape : collisionShapes) {
            boolean collision = false;
            if (isCollidingWithBoxShape(player.collisionShape, collisionShape)) {
                collision = true;
            }

            if (collision && collisionShape.isTrigger() &&  currentTime >= triggerCooldown) {
                if (!collisionShape.triggered) {
                    collisionShape.triggered = true;
                    Signals.EnteredDoor.emit(collisionShape.position);
                    currentTime = 0;
                    collision = false;
                }
            }

            if (collision) {
                if (!collidingWith.contains(collisionShape))
                    collidingWith.add(collisionShape);
            }
        }

        if (!collidingWith.isEmpty()) {
            applySeparationToPlayer(player, collidingWith);
        }

        collidingWith.clear();
    }

    private Vector2 getSeparationVectorBox(CollisionShape shape1, CollisionShape shape2) {
        float left = shape1.right - shape2.left;
        float right = shape2.right - shape1.left;
        float top = shape1.bottom - shape2.top;
        float bottom = shape2.bottom - shape1.top;

        Vector2 s = new Vector2(0, 0);
        if (left < right) {
            s.x = -left;
        } else {
            s.x = right;
        }

        if (top < bottom) {
            s.y = -top;
        } else {
            s.y = bottom;
        }

        if (Math.abs(s.x) < Math.abs(s.y)) {
            s.y = 0;
        }
        else if (Math.abs(s.x) > Math.abs(s.y)) {
            s.x = 0;
        }

        return s;
    }

    private void applySeparationToPlayer(Entity player, ArrayList<CollisionShape> collisionShapes) {
        ArrayList<Vector2> ss = new ArrayList<>();

        for (CollisionShape collisionShape : collisionShapes) {
            Vector2 a;

            a = getSeparationVectorBox(player.collisionShape, collisionShape);
            ss.add(a);
        }

        Vector2 s = new Vector2(0, 0);
        if (ss.size() > 2)
            s = combineSeparationVectors(ss);
        else
            s = getCombinedSeparationVector(ss);

        player.transform.position.x += s.x;
        player.transform.position.y += s.y;
    }

    private Vector2 combineSeparationVectors(ArrayList<Vector2> separationVectors) {
        Vector2 combinedVector = new Vector2(0, 0);

        for (Vector2 vector : separationVectors) {
            combinedVector.x += vector.x;
            combinedVector.y += vector.y;
        }

        return combinedVector;
    }

    private Vector2 getCombinedSeparationVector(ArrayList<Vector2> separationVectors) {
        Vector2 combinedVector = new Vector2(0, 0);

        for (Vector2 vector : separationVectors) {
            if (Math.abs(vector.x) > Math.abs(combinedVector.x)) {
                combinedVector.x = vector.x;
            }
            if (Math.abs(vector.y) > Math.abs(combinedVector.y)) {
                combinedVector.y = vector.y;
            }
        }

        if (Math.abs(combinedVector.x) < Math.abs(combinedVector.y)) {
            combinedVector.x = 0;
        } else if (Math.abs(combinedVector.x) > Math.abs(combinedVector.y)) {
            combinedVector.y = 0;
        }

        return combinedVector;
    }

    public boolean isCollidingWithBoxShape(CollisionShape shape1, CollisionShape shape2) {
        return shape1.right > shape2.left
                && shape2.right > shape1.left
                && shape1.bottom > shape2.top
                && shape2.bottom > shape1.top;
    }
}
