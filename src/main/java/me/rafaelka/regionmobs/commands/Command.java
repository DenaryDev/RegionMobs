/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.commands;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.rafaelka.regionmobs.RegionMobsPlugin;
import me.rafaelka.regionmobs.Settings;
import me.rafaelka.regionmobs.region.Region;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author DenaryDev
 * @since 21:15 12.01.2024
 */
public abstract class Command {

    protected final RegionMobsPlugin plugin = RegionMobsPlugin.instance();

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
            || source.getBukkitSender().hasPermission("regionmobs.admin")
            || source.getBukkitSender().hasPermission(permission());
    }

    protected void sendMessage(CommandSourceStack source, String msg, TagResolver... tags) {
        final var prefixAndTags = new ArrayList<TagResolver>();
        prefixAndTags.add(Placeholder.parsed("prefix", Settings.messages().replacements.prefix));
        prefixAndTags.addAll(Arrays.stream(tags).toList());
        source.getBukkitSender().sendMessage(miniMessage(msg, prefixAndTags.toArray(new TagResolver[0])));
    }

    @NotNull
    protected Component miniMessage(String msg, TagResolver... tags) {
        return MiniMessage.miniMessage().deserialize(msg, tags);
    }

    @Nullable
    protected ServerPlayer serverPlayer(CommandSourceStack source) {
        if (!source.isPlayer()) {
            sendMessage(source, Settings.messages().errors.notPlayer);
            return null;
        }
        return source.getPlayer();
    }

    @Nullable
    protected Region region(CommandContext<CommandSourceStack> context) {
        final var region = plugin.regionManager().regionById(string(context, "id").toLowerCase());
        if (region == null) {
            sendMessage(context.getSource(), Settings.messages().errors.region.notFound);
            return null;
        }
        return region;
    }

    @NotNull
    protected String string(CommandContext<CommandSourceStack> context, String key) {
        return context.getArgument(key, String.class);
    }

    protected CompletableFuture<Suggestions> suggestRegion(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        final var regions = plugin.regionManager().regions().keySet();
        return SharedSuggestionProvider.suggest(regions, builder);
    }

    protected boolean saveFailed(CommandSourceStack source, Region region) {
        if (!region.save()) {
            sendMessage(source, Settings.messages().errors.region.saveError);
            return true;
        }
        return false;
    }
}
