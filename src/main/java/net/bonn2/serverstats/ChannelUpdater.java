package net.bonn2.serverstats;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.bonn2.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class ChannelUpdater {
    Timer timer;

    public ChannelUpdater(long seconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new UpdaterTask(), 0, seconds * 1000);
    }

    static class UpdaterTask extends TimerTask {
        public void run() {
            Bot.logger.info("hi");
            File statsFolder = new File(Bot.localPath + File.separator + "serverstats");
            if (!statsFolder.exists()) return;
            for (String filename : statsFolder.list()) {
                Guild guild = Bot.jda.getGuildById(filename);
                if (guild == null) return;
                File statsFile = new File(statsFolder + File.separator + filename + File.separator + "stats.json");
                try (FileInputStream is = new FileInputStream(statsFile)) {
                    JsonArray jsonArray = new Gson().fromJson(new String(is.readAllBytes()), JsonArray.class);
                    if (jsonArray == null) return;
                    for (JsonElement jsonElement : jsonArray.asList()) {
                        if (jsonElement instanceof JsonObject jsonObject) {
                            switch (jsonObject.get("type").getAsString()) {
                                case "role" -> {
                                    VoiceChannel channel = guild.getVoiceChannelById(jsonObject.get("channel_id").getAsString());
                                    Role role = guild.getRoleById(jsonObject.get("role_id").getAsString());
                                    // TODO: Remove these from the json file
                                    if (channel == null) return;
                                    if (role == null) return;
                                    long count = 0;
                                    for (Member member : guild.getMembers())
                                        if (member.getRoles().contains(role)) count++;
                                    if (!channel.getName().equals(role.getName() + ": " + count))
                                        channel.getManager().setName(role.getName() + ": " + count).queue();
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    return;
                }
            }
        }
    }
}
