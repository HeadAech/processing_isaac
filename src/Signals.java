import java.util.UUID;

class DamageUUID {
    UUID uuid;
    float damage;

    DamageUUID(UUID uuid, float damage) {
        this.uuid = uuid;
        this.damage = damage;
    }
}

class ItemUUID {
    UUID uuid;
    Item item;

    ItemUUID(UUID uuid, Item item) {
        this.uuid = uuid;
        this.item = item;
    }
}

class SpawnEnemy {
    EnemyType type;
    Vector2 position;
    SpawnEnemy(EnemyType type, Vector2 position) {
        this.type = type;
        this.position = position;
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
    static Signal<String> PlayMusic = new Signal<>();
    static Signal<Object> StopMusic = new Signal<>();

    static Signal<Item> ItemPickedUp = new Signal<>();
    static Signal<UUID> ItemPickedUpUUID = new Signal<>();

    static Signal<SpawnEnemy> SpawnEnemy = new Signal<>();

    static Signal<Object> RestartGame = new Signal<>();
}
