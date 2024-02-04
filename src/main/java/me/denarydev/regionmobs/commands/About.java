/*
 * Copyright (c) 2024 DenaryDev
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package me.denarydev.regionmobs.commands;

import com.mojang.brigadier.builder.ArgumentBuilder;
import me.denarydev.regionmobs.BuildConstants;
import me.denarydev.regionmobs.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author DenaryDev
 * @since 19:12 04.02.2024
 */
public class About extends Command {
    @Override
    public String name() {
        return "about";
    }

    @Override
    public String info() {
        return Config.messages().commands.delete.info;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> command() {
        return root().executes(context -> {
            final var platform = Component.text(Bukkit.getName()).hoverEvent(HoverEvent.showText(
                Component.text(Bukkit.getVersion(), NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true)
            ));
            sendMessage(context.getSource(), Config.messages().commands.about.success,
                Placeholder.unparsed("version", BuildConstants.VERSION),
                Placeholder.component("platform", platform),
                Placeholder.unparsed("author", "DenaryDev"),
                Placeholder.unparsed("build_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss XX").format(new Date(Long.parseLong(BuildConstants.BUILD_TIME)))),
                Placeholder.unparsed("commit", BuildConstants.GIT_COMMIT), Placeholder.unparsed("branch", BuildConstants.GIT_BRANCH)
            );
            return 1;
        });
    }
}
