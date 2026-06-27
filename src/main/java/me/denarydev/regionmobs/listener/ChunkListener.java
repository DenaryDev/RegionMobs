/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.listener;

import me.denarydev.regionmobs.spawn.SpawnPoint;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;

/**
 * @author DenaryDev
 * @since 17:20 17.01.2024
 */
public final class ChunkListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        final Chunk chunk = event.getChunk();
        final Entity[] entities = chunk.getEntities();

        for (final Entity entity : entities) {
            final PersistentDataContainer container = entity.getPersistentDataContainer();

            if (container.has(SpawnPoint.SPAWNED_MOB)) {
                entity.remove();
            }
        }
    }
}
