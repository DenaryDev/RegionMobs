/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.rafaelka.regionmobs.commands.edit;

import com.mojang.brigadier.builder.ArgumentBuilder;
import me.rafaelka.regionmobs.Settings;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import org.bukkit.Bukkit;
import org.bukkit.World;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author DenaryDev
 * @since 22:38 16.01.2024
 */
public class EditWorld extends Edit {
    @Override
    public String name() {
        return "world";
    }

    @Override
    protected String subName() {
        return "";
    }

    @Override
    public String info() {
        return Settings.messages().commands.edit.world.info;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> subCommand() {
        return argument("world", word())
            .suggests((context, builder) -> SharedSuggestionProvider.suggest(
                Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .toList(), builder)
            ).executes(context -> {
                final var source = context.getSource();

                final var player = serverPlayer(source);
                if (player == null) return 1;

                final var region = region(context);
                if (region == null) return 1;

                final var worldName = string(context, "world");
                final var world = Bukkit.getWorld(worldName);
                if (world == null) {
                    sendMessage(source, Settings.messages().commands.edit.world.invalidWorld, Placeholder.unparsed("world", worldName));
                    return 1;
                }

                region.world(world);
                if (saveFailed(source, region)) return 1;

                sendMessage(source, Settings.messages().commands.edit.world.success,
                    Placeholder.unparsed("world", worldName),
                    Placeholder.unparsed("id", region.id())
                );

                return 1;
            });
    }
}
