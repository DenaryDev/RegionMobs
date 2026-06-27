/*
 * Copyright (c) 2026 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.denarydev.regionmobs.Config;
import me.denarydev.regionmobs.RegionMobsPlugin;
import me.denarydev.regionmobs.region.Region;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * @author DenaryDev
 * @since 2:42 13.01.2024
 */
public final class Delete extends Command {
    @Override
    public String name() {
        return "delete";
    }

    @Override
    public String info() {
        return Config.messages().commands.delete.info;
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
                final CommandSourceStack source = context.getSource();
                final Region region = region(context);
                if (region == null) return 1;

                if (!RegionMobsPlugin.instance().regionManager().deleteRegion(region)) {
                    sendMessage(source, Config.messages().commands.delete.error, Placeholder.unparsed("id", region.id()));
                    return 1;
                }

                sendMessage(source, Config.messages().commands.delete.success, Placeholder.unparsed("id", region.id()));

                return 1;
            })
        );
    }
}
