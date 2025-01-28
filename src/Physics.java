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
            boolean collision = isCollidingWithBoxShape(player.collisionShape, collisionShape);

            if (collision && collisionShape.isTrigger() ) {
                if (collisionShape.triggerType == TriggerType.SPIKES) {
                    Signals.DamageUUID.emit(new DamageUUID(player.collisionShape.uuid, 1));
                    currentTime = 0;
                    collision = false;
                }
                if (currentTime >= triggerCooldown) {
                    if (collisionShape.triggered) continue;
                    collision = false;

                    if (collisionShape.triggerType == TriggerType.DOOR) {
                        collisionShape.triggered = true;
                        Signals.EnteredDoor.emit(collisionShape.position);
                        currentTime = 0;

                    }
                    if (collisionShape.triggerType == TriggerType.ITEM) {
                        collisionShape.triggered = true;
                        System.out.println("ITEM");
                        Signals.ItemPickedUpUUID.emit(collisionShape.uuid);
                        currentTime = 0;
                    }
                    if (collisionShape.triggerType == TriggerType.BUTTON && !collisionShape.triggered) {
                        Signals.RestartGame.emit(null);
                        currentTime = 0;
                    }

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

    public void checkCollisionForEntitiesWithWalls(Entity entity) {
        ArrayList<CollisionShape> collidingWith = new ArrayList<>();
        for (CollisionShape collisionShape : collisionShapes) {
            boolean collision = false;
            if (entity.flying && !collisionShape.isWall) continue;
            if (isCollidingWithBoxShape(entity.collisionShape, collisionShape)) {
                collision = true;
            }

            if (collision) {
                if (!collidingWith.contains(collisionShape)) {
                    collidingWith.add(collisionShape);
                }
            }
        }

        if (!collidingWith.isEmpty()) {
            applySeparationToPlayer(entity, collidingWith);
        }
        collidingWith.clear();
    }

    public void checkCollisionForProjectiles(Projectile projectile, float deltaTime) {
        for (CollisionShape collisionShape : collisionShapes) {
            boolean collision = false;
            if (isCollidingWithBoxShape(projectile.collisionShape, collisionShape)) {
                if (collisionShape.trigger) continue;
                Signals.ProjectileDestroyed.emit(projectile.uuid);
                Signals.ProjectileEnteredCollisionShape.emit(collisionShape.uuid);
            }
        }
    }

    public void checkCollisionForProjectileWithEntity(Projectile projectile, ArrayList<Enemy> entities, Entity player) {
        for (Entity entity : entities) {
            CollisionShape collisionShape = projectile.canDamagePlayer ? player.collisionShape : entity.collisionShape;
            if (isCollidingWithBoxShape(projectile.collisionShape, collisionShape)) {
                Signals.ProjectileDestroyed.emit(projectile.uuid);
//                Signals.ProjectileEnteredCollisionShape.emit(collisionShape.uuid);
                Signals.DamageUUID.emit(new DamageUUID(collisionShape.uuid, projectile.damage));
            }
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
