/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.region;

import me.denarydev.crystal.config.BukkitConfigs;
import me.denarydev.crystal.config.CrystalConfigs;
import me.denarydev.regionmobs.RegionMobsPlugin;
import org.spongepowered.configurate.ConfigurateException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author DenaryDev
 * @since 19:52 16.01.2024
 */
public class RegionManager {
    private final RegionMobsPlugin plugin;
    private final Map<String, Region> regions = new HashMap<>();
    private final Path regionsFolder;

    public RegionManager(RegionMobsPlugin plugin) {
        this.plugin = plugin;
        this.regionsFolder = plugin.getDataFolder().toPath().resolve("regions");
    }

    public void load() throws ConfigurateException {
        try {
            loadRegions();
        } catch (IOException e) {
            throw new ConfigurateException("Failed to load regions", e);
        }
    }

    private void loadRegions() throws IOException {
        regions.clear();
        if (!Files.exists(regionsFolder)) Files.createDirectories(regionsFolder);
        try (final var files = Files.list(regionsFolder)) {
            for (final var file : files.toList()) {
                if (file != null && file.getFileName().toString().endsWith(".conf")) {
                    final var loader = CrystalConfigs.hoconLoader(file, BukkitConfigs.serializers());
                    final var node = loader.load();
                    final var region = node.require(Region.class);

                    if (region.set(file.getFileName().toString().replace(".conf", ""), loader, node)) {
                        regions.put(region.id(), region);
                    }
                }
            }
        }
        plugin.logger().info("Loaded " + regions.size() + " regions");
    }

    public boolean hasRegion(String id) {
        return regions.containsKey(id);
    }

    public boolean createRegion(String id) {
        try {
            final var loader = CrystalConfigs.hoconLoader(regionsFolder.resolve(id + ".conf"), BukkitConfigs.serializers());
            final var node = loader.load();
            final var region = new Region();
            if (region.set(id, loader, node)) {
                regions.put(id, region);
                return true;
            }
        } catch (ConfigurateException e) {
            plugin.logger().error("Failed to create region " + id, e);
        }
        return false;
    }

    public boolean deleteRegion(Region region) {
        regions.remove(region.id());
        final var file = regionsFolder.resolve(region.id() + ".conf");
        try {
            Files.delete(file);
            return true;
        } catch (IOException e) {
            plugin.logger().error("Failed to delete region " + region.id(), e);
        }
        return false;
    }

    public Region regionById(String id) {
        return regions.get(id);
    }

    public Map<String, Region> regions() {
        return regions;
    }
}
