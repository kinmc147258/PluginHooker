package dev.diona.pluginhooker;

import cn.nukkit.plugin.PluginBase;
import dev.diona.pluginhooker.commands.SimpleCommand;
import dev.diona.pluginhooker.config.ConfigManager;
import dev.diona.pluginhooker.config.ConfigPath;
import dev.diona.pluginhooker.hook.HookerManager;
import dev.diona.pluginhooker.listeners.PlayerListener;
import dev.diona.pluginhooker.player.PlayerManager;
import dev.diona.pluginhooker.plugin.PluginManager;
import lombok.Getter;

/**
 * 这是一个Nukkit的移植版本
 *
 * @author Loyisa(原作者)
 * @author Catrainbow(基岩版维护)
 */
@Getter
public final class PluginHooker extends PluginBase {

    @Getter
    private static PluginHooker instance;

    @Getter
    private static HookerManager hookerManager;
    @Getter
    private static PluginManager pluginManager;
    @Getter
    private static PlayerManager playerManager;
    @Getter
    private static ConfigManager configManager;

    public boolean enabledBstats = false;

    public PluginHooker() {
        instance = this;

        configManager = new ConfigManager();
        pluginManager = new PluginManager();
        playerManager = new PlayerManager();
        this.getLogger().info("PluginHooker loaded! start hooking...");
        hookerManager = new HookerManager();
        configManager.loadConfig(this);
    }

    @Override
    public void onEnable() {
        if (enabledBstats) {
            new Metrics(this, 17654);
        }
        // register command
        Bukkit.getPluginCommand("pluginhooker").setExecutor(new SimpleCommand());
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
