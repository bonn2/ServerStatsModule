package net.bonn2.serverstats;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Command extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("stats")) return;
        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "channel" -> {
                Role role = event.getOption("role").getAsRole();
                long count = 0;
                for (Member member : event.getGuild().getMembers())
                    if (member.getRoles().contains(role)) count++;
                event.getGuild().createVoiceChannel(role.getName() + ": " + count).queue();
            }
        }
    }
}
