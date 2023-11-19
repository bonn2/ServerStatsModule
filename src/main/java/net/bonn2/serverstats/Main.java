package net.bonn2.serverstats;

import net.bonn2.Bot;
import net.bonn2.modules.Module;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Main extends Module {

    ChannelUpdater channelUpdater;

    @Override
    public void registerLoggingChannels() {

    }

    @Override
    public void registerSettings() {

    }

    @Override
    public void load() {
        Bot.jda.addEventListener(new Command());
        channelUpdater = new ChannelUpdater(3600);
    }

    @Override
    public CommandData[] getCommands() {
        return new CommandData[] {
                Commands.slash(
                        "stats",
                        "Base command for ServerStats"
                ).addSubcommands(
                        new SubcommandData(
                                "channel",
                                "Create a stats channel"
                        ).addOption(
                                OptionType.ROLE,
                                "role",
                                "A role to track",
                                true
                        )
                )
        };
    }
}