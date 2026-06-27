/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.spawn;

import me.denarydev.regionmobs.RegionMobsPlugin;
import me.denarydev.regionmobs.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author DenaryDev
 * @since 3:48 17.01.2024
 */
public final class SpawnRegion {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private final Region region;
    private final List<SpawnPoint> spawnPoints = new ArrayList<>();
    private final List<SpawnPoint> despawnNextTick = new ArrayList<>();
    private final List<EntityType> entityTypes = new ArrayList<>();

    public SpawnRegion(@NotNull Region region) {
        this.region = region;
        this.spawnPoints.addAll(region.points().stream().map(SpawnPoint::new).toList());
        this.entityTypes.addAll(region.mobs());
    }

    public String id() {
        return this.region.id();
    }

    public void spawnMobs() {
        CompletableFuture.completedFuture(alivePointsAmount()).thenAcceptAsync(this::spawnMobsAsync);
    }

    private void spawnMobsAsync(int aliveAmount) {
        this.spawnPoints.forEach(point -> {
            if (point.alive() && !playersNearby(point.entityLocation(), this.region.spawnSettings().despawnDistance())) {
                this.despawnNextTick.add(point);
            }
        });

        final int limit = this.spawnPoints.size();
        final int diff = limit - aliveAmount;
        if (diff <= 0) return;

        final int max = Math.min(this.region.spawnSettings().maxCapSize(), diff);
        final int amount = max > this.region.spawnSettings().minCapSize() ? RANDOM.nextInt(this.region.spawnSettings().minCapSize(), max) : max;

        final List<SpawnPoint> points = randomPointsNoMobs(amount);
        Bukkit.getScheduler().runTask(RegionMobsPlugin.instance(), () ->
            points.forEach(point -> {
                point.spawn(this.entityTypes.get(RANDOM.nextInt(this.entityTypes.size())), this.region.spawnSettings().allowBabies);
                this.spawnPoints.add(point);
            })
        );
    }

    private int alivePointsAmount() {
        this.despawnNextTick.forEach(SpawnPoint::despawn);
        this.despawnNextTick.clear();
        this.spawnPoints.forEach(SpawnPoint::validate);

        return (int) this.spawnPoints.stream().filter(SpawnPoint::alive).count();
    }

    private boolean playersNearby(Location location, int distance) {
        if (location == null) return false;

        return Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getLocation().distance(location) <= distance);
    }

    @NotNull
    private List<SpawnPoint> randomPointsNoMobs(int size) {
        final List<SpawnPoint> copy = new ArrayList<>(this.spawnPoints);
        final List<SpawnPoint> result = new ArrayList<>();

        for (int i = 0; i < copy.size(); i++) {
            final int index = RANDOM.nextInt(copy.size());
            final SpawnPoint point = copy.remove(index);

            if (!point.alive() && playersNearby(point.location(), this.region.spawnSettings().maxDistance())) {
                this.spawnPoints.remove(point);
                result.add(point);

                if (result.size() == size) break;
            }
        }

        return result;
    }

    public void despawnAll() {
        this.spawnPoints.forEach(SpawnPoint::despawn);
    }
}
