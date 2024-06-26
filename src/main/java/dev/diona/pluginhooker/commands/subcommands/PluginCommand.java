package dev.diona.pluginhooker.commands.subcommands;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.Plugin;
import com.google.common.collect.Lists;
import dev.diona.pluginhooker.PluginHooker;
import dev.diona.pluginhooker.commands.SubCommand;
import dev.diona.pluginhooker.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static dev.diona.pluginhooker.commands.SimpleCommand.PREFIX;

public class PluginCommand extends SubCommand {
    public PluginCommand() {
        super("plugin", "Manage plugins to hook", "/ph plugin <add/remove/list> <plugin name>");
    }

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            this.sendUsage(sender);
            return false;
        }
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 1) {
                sender.sendMessage(StringUtils.colorize(PREFIX + "Usage: /ph plugin add <plugin name>"));
                return false;
            }
            Plugin plugin = Server.getInstance().getPluginManager().getPlugin(args[1]);
            if (plugin == null) {
                sender.sendMessage(StringUtils.colorize(PREFIX + "Plugin " + args[1] + " not found"));
                return false;
            }
            // should not hook self
            if (plugin.equals(PluginHooker.getInstance())) {
                sender.sendMessage(StringUtils.colorize(PREFIX + "PluginHooker cannot hook itself"));
                return false;
            }
            if (PluginHooker.getPluginManager().isPluginHooked(plugin)) {
                sender.sendMessage(StringUtils.colorize(PREFIX + "Plugin " + args[1] + " is already hooked"));
                return false;
            }
            PluginHooker.getPluginManager().addPlugin(plugin);
            sender.sendMessage(StringUtils.colorize(PREFIX + "Plugin " + args[1] + " has been hooked"));
            return true;
        } else if (args[0].equalsIgnoreCase("remove")) {
            if (args.length == 1) {
                sender.sendMessage(StringUtils.colorize(PREFIX + "Usage: /ph plugin remove <plugin name>"));
                return false;
            }
            Plugin plugin = Server.getInstance().getPluginManager().getPlugin(args[1]);
            if (plugin == null) {
                sender.sendMessage(StringUtils.colorize(PREFIX + "Plugin " + args[1] + " not found"));
                return false;
            }
            if (!PluginHooker.getPluginManager().isPluginHooked(plugin)) {
                sender.sendMessage(StringUtils.colorize(PREFIX + "Plugin " + args[1] + " is not hooked"));
                return false;
            }
            PluginHooker.getPluginManager().removePlugin(plugin);
            sender.sendMessage(StringUtils.colorize(PREFIX + "Plugin " + args[1] + " has been unhooked"));
            return true;
        } else if (args[0].equalsIgnoreCase("list")) {
            if (PluginHooker.getPluginManager().getPluginsToHook().size() == 0) {
                sender.sendMessage(StringUtils.colorize(PREFIX + "No plugins are hooked"));
                return true;
            }
            sender.sendMessage(StringUtils.colorize(PREFIX + "Plugins hooked:"));
            for (Plugin plugin : PluginHooker.getPluginManager().getPluginsToHook()) {
                sender.sendMessage(StringUtils.colorize(PREFIX + " - " + plugin.getName()));
            }
            return true;
        } else {
            this.sendUsage(sender);
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> sub = Lists.newArrayList("add", "remove", "list");
            if (args[0].isEmpty()) {
                return sub;
            } else {
                return sub.stream()
                        .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                        .collect(Collectors.toList());
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
            if (args[1].isEmpty()) {
                List<String> plugins = new ArrayList<>();
                for (Plugin plugin : PluginHooker.getInstance().getServer().getPluginManager().getPlugins().values())
                    plugins.add(plugin.getName());
                return plugins;
            } else {
                List<String> plugins = new ArrayList<>();
                for (Plugin plugin : PluginHooker.getInstance().getServer().getPluginManager().getPlugins().values())
                    plugins.add(plugin.getName().toLowerCase());
                return plugins;
            }
        }
        return null;
    }
}
