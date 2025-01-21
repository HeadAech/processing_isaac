import java.lang.reflect.Array;
import java.util.ArrayList;

public class Physics {

    ArrayList<CollisionShape> collisionShapes;

    public Physics() {}

    public void checkCollisionForPlayerWithWalls(Entity player) {
        ArrayList<CollisionShape> collidingWith = new ArrayList<>();
        for (CollisionShape collisionShape : collisionShapes) {
            boolean collision = false;
            if (isCollidingWithBoxShape(player.collisionShape, collisionShape)) {
                collision = true;
            }

            if (collision) {
                collidingWith.add(collisionShape);
            }
        }

        if (!collidingWith.isEmpty()) {
            applySeparationToPlayer(player, collidingWith);
        }

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
        s = combineSeparationVectors(ss);

        player.targetPosition.x += s.x;
        player.targetPosition.y += s.y;
    }

    private Vector2 combineSeparationVectors(ArrayList<Vector2> separationVectors) {
        Vector2 combinedVector = new Vector2(0, 0);

        for (Vector2 vector : separationVectors) {
            combinedVector = combinedVector.plus(vector);
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
