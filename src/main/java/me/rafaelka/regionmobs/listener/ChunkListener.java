/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.listener;

import me.rafaelka.regionmobs.spawn.SpawnPoint;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

/**
 * @author DenaryDev
 * @since 17:20 17.01.2024
 */
public class ChunkListener implements Listener {

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        final var chunk = event.getChunk();
        final var entities = chunk.getEntities();
        for (var entity : entities) {
            final var container = entity.getPersistentDataContainer();
            if (container.has(SpawnPoint.SPAWNED_MOB)) {
                entity.remove();
            }
        }
    }
}
