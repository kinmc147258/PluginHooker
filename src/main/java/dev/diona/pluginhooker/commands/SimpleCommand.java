package dev.diona.pluginhooker.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import dev.diona.pluginhooker.PluginHooker;
import dev.diona.pluginhooker.commands.subcommands.PlayerCommand;
import dev.diona.pluginhooker.commands.subcommands.PluginCommand;
import dev.diona.pluginhooker.utils.StringUtils;
import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleCommand extends Command {

    public static String PREFIX = "&b[PH] &f> &b";

    private final PluginHooker pluginHooker = PluginHooker.getInstance();

    private final Set<SubCommand> commands = new LinkedHashSet<>();

    public SimpleCommand() {
        super("ph", "PluginHooker Command");
        this.commands.add(new PluginCommand());
        this.commands.add(new PlayerCommand());
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        // check permission first
        if (!commandSender.hasPermission("pluginhooker.admin")) {
            commandSender.sendMessage(StringUtils.colorize(PREFIX + "PluginHooker " + pluginHooker.getDescription().getVersion() + ": Plugin rekker (~DionaMC)"));
            return false;
        } else if (strings.length == 0) {
            showHelp(commandSender);
            return false;
        }
        for (SubCommand subCommand : commands) {
            if (subCommand.getName().equalsIgnoreCase(strings[0])) {
                // remove the first arg
                String[] newArgs = new String[strings.length - 1];
                System.arraycopy(strings, 1, newArgs, 0, strings.length - 1);
                return subCommand.onCommand(commandSender, newArgs);
            }
        }
        showHelp(commandSender);
        return false;
    }

    public void showHelp(CommandSender sender) {
        sender.sendMessage(StringUtils.colorize(PREFIX + "PluginHooker " + pluginHooker.getDescription().getVersion() + ": Plugin rekker (~DionaMC)"));
        sender.sendMessage(StringUtils.colorize(PREFIX + "Commands:"));
        for (SubCommand subCommand : commands) {
            sender.sendMessage(StringUtils.colorize(PREFIX + "/ph " + subCommand.getName() + " &f- &b" + subCommand.getDescription()));
        }
    }
}
