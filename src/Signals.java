import java.util.UUID;

public class Signals {

    static Signal<Vector2> EnteredDoor = new Signal<>();

    static Signal<UUID> ProjectileDestroyed = new Signal<>();

    static Signal<UUID> ProjectileEnteredCollisionShape = new Signal<>();

    static Signal<Object> UpdateCollisionShapesForPhysics = new Signal<>();
}
