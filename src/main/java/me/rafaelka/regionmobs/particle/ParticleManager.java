/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.particle;

import com.destroystokyo.paper.ParticleBuilder;
import me.rafaelka.regionmobs.RegionMobsPlugin;
import me.rafaelka.regionmobs.Settings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DenaryDev
 * @since 4:30 17.01.2024
 */
public class ParticleManager {
    private final RegionMobsPlugin plugin;
    private final List<Player> shown = new ArrayList<>();
    private BukkitTask task;

    public ParticleManager(RegionMobsPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::pointsParticleTask, 20, Settings.main().particles.interval);
    }

    public void unload() {
        if (task != null)
            task.cancel();
    }

    private void pointsParticleTask() {
        final var regions = plugin.regionManager().regions().values();
        if (shown.isEmpty()) return;
        final var particle = Settings.main().particles.type;

        for (final var region : regions) {
            final var points = region.points();
            for (final var point : points) {
                final var nearbyPlayers = shown.stream()
                    .filter(player -> player.getLocation().distance(point) <= Settings.main().mobSpawn.maxDistance)
                    .toList();
                if (!nearbyPlayers.isEmpty()) {
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> new ParticleBuilder(particle)
                        .location(point.toCenterLocation())
                        .count(Settings.main().particles.count)
                        .offset(0.05, 0.05, 0.05)
                        .receivers(nearbyPlayers)
                        .spawn());
                }
            }
        }
    }

    public boolean shown(Player player) {
        return shown.contains(player);
    }

    public void show(Player player) {
        shown.add(player);
    }

    public void hide(Player player) {
        shown.remove(player);
    }
}
