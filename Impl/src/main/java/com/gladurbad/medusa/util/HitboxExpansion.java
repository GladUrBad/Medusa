package com.gladurbad.medusa.util;

import lombok.Getter;
import org.bukkit.entity.Entity;

/**
 * Created on 12/5/2020 Package com.gladurbad.medusa.util by GladUrBad
 *
 * This is an enum of entity hitbox expansions, found from
 * https://technical-minecraft.fandom.com/wiki/Entity_Hitbox_Sizes.
 *
 * Names of the entities were found in EntityType.class.
 *
 * This enum is not an accurate representation of hitbox expansions for each entity,
 * if you need something that's accurate, look elsewhere. This is somewhat accurate and
 * helps to prevent Hitbox (A) from falsing due to entities with large hitbox expansions.
 */

@Getter
public enum HitboxExpansion {

    PLAYER("Player", 0.4),
    CREEPER("Creeper", 0.4),
    SKELETON("Skeleton", 0.4),
    SPIDER("Spider", 0.8),
    GIANT("Giant", 2.5),
    ZOMBIE("Zombie", 0.4),
    SLIME("Slime", 1.12), //Works for large slimes only for now.
    GHAST("Ghast", 2.1),
    PIG_ZOMBIE("PigZombie", 0.4),
    ENDERMAN("Enderman", 0.4),
    CAVE_SPIDER("CaveSpider", 0.45),
    SILVERFISH("Silverfish", 0.3),
    BLAZE("Blaze", 0.4),
    MAGMA_CUBE("LavaSlime", 1.12),
    ENDER_DRAGON("EnderDragon", 8.1),
    WITHER("WitherBoss", 0.55),
    BAT("Bat", 0.35),
    WITCH("Witch", 0.4),
    ENDERMITE("Endermite", 0.3),
    GUARDIAN("Guardian",0.525),
    PIG("Pig", 0.55), //Works for both baby and grown pigs.
    SHEEP("Sheep", 0.55),
    COW("Cow", 0.55),
    CHICKEN("Chicken", 0.3), //Works for both baby and grown chickens.
    SQUID("Squid", 0.5),
    WOLF("Wolf", 0.5), //Works for both baby and grown wolves.
    MUSHROOM_COW("MushroomCow", 0.55),
    SNOWMAN("SnowMan", 0.45),
    OCELOT("Ozelot", 0.4),
    IRON_GOLEM("VillagerGolem", 0.8),
    HORSE("EntityHorse", 0.7982),
    RABBIT("Rabbit", 0.3), //Works for both baby and grown rabbits.
    VILLAGER("Villager", 0.4);

    private final double expansion;
    private final String name;

    HitboxExpansion(final String name, final double expansion) {
        this.name = name;
        this.expansion = expansion;
    }

    public static double getExpansion(final Entity entity) {
        for (HitboxExpansion hitboxExpansion : values()) {
            final String name = entity.getType().getName();
            if (name != null) {
                if (name.equalsIgnoreCase(hitboxExpansion.getName())) {
                    return hitboxExpansion.getExpansion();
                }
            }
        }
        return 0.4D;
    }
}
