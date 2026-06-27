/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.region;

import io.sapphiremc.lib.configurate.CommentedConfigurationNode;
import io.sapphiremc.lib.configurate.ConfigurateException;
import io.sapphiremc.lib.configurate.yaml.YamlConfigurationLoader;
import me.denarydev.crystal.config.ConfigLoaders;
import me.denarydev.crystal.paper.configurate.PaperSerializers;
import me.denarydev.regionmobs.RegionMobsPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author DenaryDev
 * @since 19:52 16.01.2024
 */
public final class RegionManager {
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
        this.regions.clear();

        if (!Files.exists(this.regionsFolder)) {
            Files.createDirectories(this.regionsFolder);
        }

        try (Stream<Path> files = Files.list(this.regionsFolder)) {
            for (Path file : files.toList()) {
                if (file != null && file.getFileName().toString().endsWith(".yml")) {
                    final YamlConfigurationLoader loader = ConfigLoaders.yaml(file, PaperSerializers.get());
                    final CommentedConfigurationNode node = loader.load();
                    final Region region = node.require(Region.class);

                    if (region.set(file.getFileName().toString().replace(".yml", ""), loader, node)) {
                        regions.put(region.id(), region);
                    }
                }
            }
        }

        this.plugin.logger().info("Loaded {} regions", this.regions.size());
    }

    public boolean hasRegion(String id) {
        return this.regions.containsKey(id);
    }

    public boolean createRegion(String id) {
        try {
            final YamlConfigurationLoader loader = ConfigLoaders.yaml(this.regionsFolder.resolve(id + ".yml"), PaperSerializers.get());
            final CommentedConfigurationNode node = loader.load();
            final Region region = new Region();

            if (region.set(id, loader, node)) {
                this.regions.put(id, region);
                return true;
            }
        } catch (ConfigurateException e) {
            this.plugin.logger().error("Failed to create region {}", id, e);
        }

        return false;
    }

    public boolean deleteRegion(Region region) {
        this.regions.remove(region.id());
        final Path file = this.regionsFolder.resolve(region.id() + ".yml");

        try {
            Files.delete(file);
            return true;
        } catch (IOException e) {
            this.plugin.logger().error("Failed to delete region {}", region.id(), e);
        }

        return false;
    }

    public Region regionById(String id) {
        return this.regions.get(id);
    }

    public Map<String, Region> regions() {
        return regions;
    }
}
