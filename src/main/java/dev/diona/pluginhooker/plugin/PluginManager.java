package dev.diona.pluginhooker.plugin;

import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import dev.diona.pluginhooker.player.DionaPlayer;
import dev.diona.pluginhooker.PluginHooker;
import lombok.Getter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class PluginManager {

    private final Set<Plugin> pluginsToHook = new LinkedHashSet<>();

    public void addPlugin(Plugin plugin) {
        pluginsToHook.add(plugin);
    }

    public void removePlugin(Plugin plugin) {
        if (!pluginsToHook.contains(plugin)) {
            Server.getInstance().getLogger().warning("Warning: " + plugin.getName() + " is not in the plugin hook list! Ignored!");
            return;
        }
        pluginsToHook.remove(plugin);
        for (DionaPlayer dionaPlayer : PluginHooker.getPlayerManager().getPlayers()) {
            dionaPlayer.disablePlugin(plugin);
        }
    }

    public boolean isPluginHooked(Plugin plugin) {
        return pluginsToHook.contains(plugin);
    }

}
