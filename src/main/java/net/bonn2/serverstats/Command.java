package net.bonn2.serverstats;

import com.google.gson.*;
import net.bonn2.Bot;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Command extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("stats")) return;
        switch (Objects.requireNonNull(event.getSubcommandName())) {
            case "channel" -> {
                event.deferReply(true).queue();
                Role role = event.getOption("role").getAsRole();
                long count = 0;
                for (Member member : event.getGuild().getMembers())
                    if (member.getRoles().contains(role)) count++;
                VoiceChannel channel = event.getGuild().createVoiceChannel(role.getName() + ": " + count).complete();
                File statsFolder = new File(
                        Bot.localPath + File.separator +
                                "serverstats" + File.separator +
                                event.getGuild().getId()
                );
                statsFolder.mkdirs();
                try {
                    File statsFile = new File(statsFolder + File.separator + "stats.json");
                    statsFile.createNewFile();
                    try (FileInputStream is = new FileInputStream(statsFile)) {
                        JsonArray jsonArray = new Gson().fromJson(new String(is.readAllBytes()), JsonArray.class);
                        is.close();
                        if (jsonArray == null) jsonArray = new JsonArray();
                        JsonObject jsonObject = new JsonObject();
                        jsonObject.add("type", new JsonPrimitive("role"));
                        jsonObject.add("channel_id", new JsonPrimitive(channel.getId()));
                        jsonObject.add("role_id", new JsonPrimitive(role.getId()));
                        jsonArray.add(jsonObject);
                        try (FileOutputStream os = new FileOutputStream(statsFile)) {
                            os.write(
                                    new GsonBuilder()
                                            .setPrettyPrinting()
                                            .create()
                                            .toJson(jsonArray)
                                            .getBytes(StandardCharsets.UTF_8)
                            );
                            event.getHook().editOriginal("Success").queue();
                        }
                    }
                } catch (IOException e) {
                    channel.delete().queue();
                    event.getHook().editOriginal("Failed to save stat.").queue();
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
