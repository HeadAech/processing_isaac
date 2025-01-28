import java.util.UUID;

class DamageUUID {
    UUID uuid;
    float damage;

    DamageUUID(UUID uuid, float damage) {
        this.uuid = uuid;
        this.damage = damage;
    }

}

public class Signals {

    static Signal<Vector2> EnteredDoor = new Signal<>();

    static Signal<UUID> ProjectileDestroyed = new Signal<>();

    static Signal<UUID> ProjectileEnteredCollisionShape = new Signal<>();

    static Signal<Object> UpdateCollisionShapesForPhysics = new Signal<>();

    static Signal<DamageUUID> DamageUUID = new Signal<>();

    static Signal<Projectile> CreateProjectile = new Signal<>();

    static Signal<String> PlaySound = new Signal<>();
}
