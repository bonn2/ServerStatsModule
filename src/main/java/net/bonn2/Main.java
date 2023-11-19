package net.bonn2;

import net.bonn2.modules.Module;
import net.bonn2.modules.settings.Settings;
import net.bonn2.modules.settings.types.Setting;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public class Main extends Module {

    @Override
    public void registerSettings() {
        Settings.register(this, "example", Setting.Type.STRING, "",
                "This is an example setting, that is different for every server.");
    }

    @Override
    public void load() {

    }

    @Override
    public CommandData[] getCommands() {
        return new CommandData[0];
    }
}