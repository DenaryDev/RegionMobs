/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.spawn;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author DenaryDev
 * @since 3:46 17.01.2024
 */
public class SpawnPoint {
    public static final NamespacedKey SPAWNED_MOB = new NamespacedKey("regionmobs", "spawned_mob");
    private final Location location;
    @Nullable
    private Entity entity;

    public SpawnPoint(@NotNull Location location) {
        this.location = location;
    }

    public void spawn(@NotNull EntityType type, boolean allowBabies) {
        entity = location.getWorld().spawnEntity(location, type);

        if (!allowBabies && entity instanceof Ageable ageable && !ageable.isAdult()) {
            ageable.setAdult();
        }

        final var container = entity.getPersistentDataContainer();
        container.set(SPAWNED_MOB, PersistentDataType.BOOLEAN, true);
    }

    public void validate() {
        if (entity != null && !entity.isValid()) {
            despawn();
        }
    }

    public void despawn() {
        if (entity != null) {
            entity.remove();
            entity = null;
        }
    }

    public boolean alive() {
        return entity != null && entity.isValid();
    }

    public Location location() {
        return location;
    }

    @Nullable
    public Location entityLocation() {
        return entity != null ? entity.getLocation() : null;
    }
}
