/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.region;

import me.rafaelka.regionmobs.RegionMobsPlugin;
import me.rafaelka.regionmobs.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author DenaryDev
 * @since 19:52 16.01.2024
 */
@ConfigSerializable
public class Region {
    private transient String id;
    private transient World world;
    private transient HoconConfigurationLoader loader;
    private transient CommentedConfigurationNode node;

    @Comment("Активирован ли регион")
    private boolean enabled = false;
    @Comment("Мир, в котором расположена область")
    private String worldName = "";
    @Comment("Точки, на которых появляются мобы")
    private List<Location> points = new ArrayList<>();
    @Comment("Типы мобов, которые появляются")
    private List<EntityType> mobs = new ArrayList<>();

    boolean set(String id, HoconConfigurationLoader loader, CommentedConfigurationNode node) {
        this.id = id;
        this.loader = loader;
        this.node = node;
        return update();
    }

    private boolean update() {
        if (!worldName.isEmpty()) {
            world = Bukkit.getWorld(worldName);
            if (world == null) {
                RegionMobsPlugin.instance().logger().warn("World " + worldName + " for region " + id + " not found!");
            }
        }
        if (enabled && incomplete()) {
            enabled = false;
            RegionMobsPlugin.instance().logger().warn("Region " + id + " is not completed, disabling...");
        }
        if (!points.isEmpty()) {
            points = points.stream().map(Utils::centerLocation).collect(Collectors.toList());
        }
        return save();
    }

    public boolean save() {
        try {
            node.set(this);
            loader.save(node);
            return true;
        } catch (ConfigurateException e) {
            RegionMobsPlugin.instance().logger().error("Error while saving region " + id, e);
        }
        return false;
    }

    public String id() {
        return id;
    }

    public boolean incomplete() {
        return world == null || points.isEmpty() || mobs.isEmpty();
    }

    public boolean enabled() {
        return enabled;
    }

    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    public World world() {
        return world;
    }

    public void world(@Nullable World world) {
        this.world = world;
        this.worldName = world != null ? world.getName() : "";
    }

    public List<Location> points() {
        return Collections.unmodifiableList(points);
    }

    public boolean hasPoint(Location point) {
        return this.points.contains(point);
    }

    public void addPoint(Location point) {
        this.points.add(point);
    }

    public void removePoint(Location point) {
        this.points.remove(point);
    }

    public List<EntityType> mobs() {
        return Collections.unmodifiableList(mobs);
    }

    public boolean hasMob(EntityType mob) {
        return this.mobs.contains(mob);
    }

    public void addMob(EntityType mob) {
        this.mobs.add(mob);
    }

    public void removeMob(EntityType mob) {
        this.mobs.remove(mob);
    }
}