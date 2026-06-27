/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.denarydev.crystal.paper.command.SharedSuggestionProvider;
import me.denarydev.regionmobs.Config;
import me.denarydev.regionmobs.RegionMobsPlugin;
import me.denarydev.regionmobs.region.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * @author DenaryDev
 * @since 21:15 12.01.2024
 */
public abstract class Command {

    public abstract String name();

    public abstract String info();

    public String args() {
        return "";
    }

    public String suggest() {
        return name();
    }

    public abstract ArgumentBuilder<CommandSourceStack, ?> command();

    public String permissionBase() {
        return "regionmobs.command.";
    }

    public String permission() {
        return permissionBase() + name();
    }

    public List<String> aliases() {
        return Collections.emptyList();
    }

    protected LiteralArgumentBuilder<CommandSourceStack> root() {
        return literal(name())
            .requires(this::checkPermission);
    }

    protected LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    protected <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected boolean checkPermission(CommandSourceStack source) {
        return permission().isEmpty()
            || source.getSender().hasPermission("regionmobs.admin")
            || source.getSender().hasPermission(permission());
    }

    protected void sendMessage(CommandSourceStack source, String msg, TagResolver... tags) {
        final List<TagResolver> prefixAndTags = new ArrayList<>();
        prefixAndTags.add(Placeholder.parsed("prefix", Config.messages().replacements.prefix));
        prefixAndTags.addAll(Arrays.stream(tags).toList());

        source.getSender().sendMessage(miniMessage(msg, prefixAndTags.toArray(new TagResolver[0])));
    }

    @NotNull
    protected Component miniMessage(String msg, TagResolver... tags) {
        return MiniMessage.miniMessage().deserialize(msg, tags);
    }

    @Nullable
    protected Player player(CommandSourceStack source) {
        if (!(source.getSender() instanceof Player player)) {
            sendMessage(source, Config.messages().errors.notPlayer);
            return null;
        }

        return player;
    }

    @Nullable
    protected Region region(CommandContext<CommandSourceStack> context) {
        final Region region = RegionMobsPlugin.instance().regionManager().regionById(string(context, "id").toLowerCase());
        if (region == null) {
            sendMessage(context.getSource(), Config.messages().errors.region.notFound);
            return null;
        }

        return region;
    }

    @NotNull
    protected String string(CommandContext<CommandSourceStack> context, String key) {
        return context.getArgument(key, String.class);
    }

    protected CompletableFuture<Suggestions> suggestRegion(SuggestionsBuilder builder) {
        final Set<String> regions = RegionMobsPlugin.instance().regionManager().regions().keySet();

        return SharedSuggestionProvider.suggest(regions, builder);
    }

    protected boolean saveFailed(CommandSourceStack source, Region region) {
        if (!region.save()) {
            sendMessage(source, Config.messages().errors.region.saveError);
            return true;
        }

        return false;
    }
}
