/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.spawn;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author DenaryDev
 * @since 3:46 17.01.2024
 */
public final class SpawnPoint {
    public static final NamespacedKey SPAWNED_MOB = new NamespacedKey("regionmobs", "spawned_mob");
    private final Location location;
    @Nullable
    private Entity entity;

    public SpawnPoint(@NotNull Location location) {
        this.location = location;
    }

    public void spawn(@NotNull EntityType type, boolean allowBabies) {
        this.entity = this.location.getWorld().spawnEntity(this.location, type);

        if (!allowBabies && this.entity instanceof Ageable ageable && !ageable.isAdult()) {
            ageable.setAdult();
        }

        final PersistentDataContainer container = this.entity.getPersistentDataContainer();
        container.set(SPAWNED_MOB, PersistentDataType.BOOLEAN, true);
    }

    public void validate() {
        if (this.entity != null && !this.entity.isValid()) {
            despawn();
        }
    }

    public void despawn() {
        if (this.entity != null) {
            this.entity.remove();
            this.entity = null;
        }
    }

    public boolean alive() {
        return this.entity != null && this.entity.isValid();
    }

    public Location location() {
        return this.location;
    }

    @Nullable
    public Location entityLocation() {
        return this.entity != null ? this.entity.getLocation() : null;
    }
}
