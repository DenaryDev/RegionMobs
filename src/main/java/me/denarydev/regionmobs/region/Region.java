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
import io.sapphiremc.lib.configurate.objectmapping.ConfigSerializable;
import io.sapphiremc.lib.configurate.objectmapping.meta.Comment;
import io.sapphiremc.lib.configurate.yaml.YamlConfigurationLoader;
import me.denarydev.crystal.paper.utils.LocationUtils;
import me.denarydev.regionmobs.Config;
import me.denarydev.regionmobs.RegionMobsPlugin;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author DenaryDev
 * @since 19:52 16.01.2024
 */
@ConfigSerializable
public final class Region {
    private transient String id;
    private transient YamlConfigurationLoader loader;
    private transient CommentedConfigurationNode node;

    @Comment("Активирован ли регион")
    private boolean enabled = false;
    @Comment("Точки, на которых появляются мобы")
    private List<Location> points = new ArrayList<>();
    @Comment("Типы мобов, которые появляются")
    private List<EntityType> mobs = new ArrayList<>();
    @Comment("Настройки появления мобов в этой области")
    private SpawnSettings spawnSettings = new SpawnSettings();

    @ConfigSerializable
    public static final class SpawnSettings {
        @Comment("Минимальный размер пачки мобов по умолчанию.\nЕсли -1, используется значение из основного конфига.")
        private int minCapSize = -1;
        @Comment("Максимальный размер пачки мобов по умолчанию.\nЕсли -1, используется значение из основного конфига.")
        private int maxCapSize = -1;
        @Comment("Если расстояние между мобом и игроком больше этого значения, моб деспавнится.\nЕсли -1, используется значение из основного конфига.")
        private int despawnDistance = -1;
        @Comment("Радиус области вокруг игрока, в которой могут появляться мобы.\nЕсли -1, используется значение из основного конфига.")
        private int maxDistance = -1;
        @Comment("Будут ли появляться детёныши мобов.\nУдалите, чтобы сбросить до значения из основного конфига.")
        public boolean allowBabies = Config.settings().mobSpawn.allowBabies;

        public int minCapSize() {
            return minCapSize > 0 ? minCapSize : Config.settings().mobSpawn.minCapSize;
        }

        public int maxCapSize() {
            return maxCapSize > 0 ? maxCapSize : Config.settings().mobSpawn.maxCapSize;
        }

        public int despawnDistance() {
            return despawnDistance > 0 ? despawnDistance : Config.settings().mobSpawn.despawnDistance;
        }

        public int maxDistance() {
            return maxDistance > 0 ? maxDistance : Config.settings().mobSpawn.maxDistance;
        }
    }

    boolean set(String id, YamlConfigurationLoader loader, CommentedConfigurationNode node) {
        this.id = id;
        this.loader = loader;
        this.node = node;
        return update();
    }

    private boolean update() {
        if (this.enabled && incomplete()) {
            this.enabled = false;
            RegionMobsPlugin.instance().logger().warn("Region {} is not completed, disabling...", this.id);
        }

        if (!this.points.isEmpty()) {
            this.points = this.points.stream().map(LocationUtils::centerLocation).collect(Collectors.toList());
        }

        return save();
    }

    public boolean save() {
        try {
            this.node.set(this);
            this.loader.save(this.node);
            return true;
        } catch (ConfigurateException e) {
            RegionMobsPlugin.instance().logger().error("Error while saving region {}", this.id, e);
        }
        return false;
    }

    public String id() {
        return id;
    }

    public boolean incomplete() {
        return this.points.isEmpty() || this.mobs.isEmpty();
    }

    public boolean enabled() {
        return enabled;
    }

    public void enabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Location> points() {
        return Collections.unmodifiableList(this.points);
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
        return Collections.unmodifiableList(this.mobs);
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

    public SpawnSettings spawnSettings() {
        return spawnSettings;
    }
}
