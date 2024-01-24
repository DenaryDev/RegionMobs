/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import me.denarydev.regionmobs.Config;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author DenaryDev
 * @since 23:12 15.01.2024
 */
public class Disable extends Command {
    @Override
    public String name() {
        return "disable";
    }

    @Override
    public String info() {
        return Config.messages().commands.disable.info;
    }

    @Override
    public String args() {
        return "<id>";
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> command() {
        return root().then(argument("id", word())
            .suggests((ctx, builder) -> super.suggestRegion(builder))
            .executes(context -> {
                final var source = context.getSource();
                final var region = region(context);
                if (region == null) return 1;

                if (!region.enabled()) {
                    sendMessage(source, Config.messages().commands.disable.already, Placeholder.unparsed("id", region.id()));
                    return 1;
                }

                region.enabled(false);
                if (saveFailed(source, region)) return 1;
                plugin.spawnManager().removeRegion(region.id());

                sendMessage(source, Config.messages().commands.disable.success, Placeholder.unparsed("id", region.id()));

                return 1;
            })
        );
    }
}
