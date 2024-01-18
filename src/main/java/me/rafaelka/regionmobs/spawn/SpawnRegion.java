/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.spawn;

import me.rafaelka.regionmobs.RegionMobsPlugin;
import me.rafaelka.regionmobs.region.Region;
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
public class SpawnRegion {
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
        return region.id();
    }

    public void spawnMobs() {
        CompletableFuture.completedFuture(alivePointsAmount()).thenAcceptAsync(this::spawnMobsAsync);
    }

    private void spawnMobsAsync(int aliveAmount) {
        spawnPoints.forEach(point -> {
            if (point.alive() && !playersNearby(point.entityLocation(), region.spawnSettings().despawnDistance()))
                despawnNextTick.add(point);
        });

        final var limit = spawnPoints.size();
        final var diff = limit - aliveAmount;
        if (diff <= 0) return;

        final var max = Math.min(region.spawnSettings().maxCapSize(), diff);
        final var amount = max > region.spawnSettings().minCapSize() ? RANDOM.nextInt(region.spawnSettings().minCapSize(), max) : max;

        final var points = randomPointsNoMobs(amount);
        Bukkit.getScheduler().runTask(RegionMobsPlugin.instance(), () ->
            points.forEach(point -> {
                point.spawn(entityTypes.get(RANDOM.nextInt(entityTypes.size())), region.spawnSettings().allowBabies);
                spawnPoints.add(point);
            })
        );
    }

    private int alivePointsAmount() {
        despawnNextTick.forEach(SpawnPoint::despawn);
        despawnNextTick.clear();
        spawnPoints.forEach(SpawnPoint::validate);
        return (int) spawnPoints.stream().filter(SpawnPoint::alive).count();
    }

    private boolean playersNearby(Location location, int distance) {
        if (location == null) return false;
        return Bukkit.getOnlinePlayers().stream().anyMatch(player -> player.getLocation().distance(location) <= distance);
    }

    @NotNull
    private List<SpawnPoint> randomPointsNoMobs(int size) {
        final var copy = new ArrayList<>(spawnPoints);
        final var result = new ArrayList<SpawnPoint>();
        for (int i = 0; i < copy.size(); i++) {
            final var index = RANDOM.nextInt(copy.size());
            final var point = copy.remove(index);
            if (!point.alive() && playersNearby(point.location(), region.spawnSettings().maxDistance())) {
                spawnPoints.remove(point);
                result.add(point);
                if (result.size() == size) break;
            }
        }
        return result;
    }

    public void despawnAll() {
        spawnPoints.forEach(SpawnPoint::despawn);
    }
}
