package dev.diona.pluginhooker.hook.impl.bukkit;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.Event;
import cn.nukkit.entity.*;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockIgniteEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.SignChangeEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.event.inventory.EnchantItemEvent;
import cn.nukkit.event.inventory.InventoryClickEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.vehicle.VehicleDamageEvent;
import cn.nukkit.event.vehicle.VehicleDestroyEvent;
import cn.nukkit.plugin.Plugin;
import dev.diona.pluginhooker.PluginHooker;
import dev.diona.pluginhooker.config.ConfigPath;
import dev.diona.pluginhooker.events.BukkitListenerEvent;
import dev.diona.pluginhooker.player.DionaPlayer;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class BukkitCallbackHandler {

    @ConfigPath("hook.bukkit.use-reflection-to-get-event-player")
    public boolean useReflectionToGetEventPlayer;
    @ConfigPath("hook.bukkit.call-event")
    public boolean callEvent;

    private final Map<Class<? extends Event>, Function<Event, Player>> eventMap = new LinkedHashMap<>();

    private final Map<Class<? extends Event>, Field> eventFieldCache = new LinkedHashMap<>();

    private final Set<Class<? extends Event>> failedFieldCache = new HashSet<>();

    public BukkitCallbackHandler() {
        this.initEventMap();
        PluginHooker.getConfigManager().loadConfig(this);
    }

    public boolean handleBukkitEvent(Plugin plugin, Event event) {
        // Don't handle those events
        if (event instanceof PlayerPreLoginEvent || event instanceof PlayerJoinEvent || event instanceof PlayerQuitEvent || event instanceof PlayerLoginEvent)
            return false;

        if (event.getClass().getClassLoader().equals(this.getClass().getClassLoader())) return false;

        if (!PluginHooker.getPluginManager().getPluginsToHook().contains(plugin)) return false;

        DionaPlayer dionaPlayer = PluginHooker.getPlayerManager().getDionaPlayer(this.getPlayerByEvent(event));
        if (dionaPlayer == null) {
            if (!callEvent) {
                return false;
            }
            BukkitListenerEvent bukkitListenerEvent = new BukkitListenerEvent(plugin, event);
            Server.getInstance().getPluginManager().callEvent(bukkitListenerEvent);
            return bukkitListenerEvent.isCancelled();
        } else {
            if (dionaPlayer.getEnabledPlugins().contains(plugin)) {
                if (!callEvent) {
                    return false;
                }
                BukkitListenerEvent bukkitListenerEvent = new BukkitListenerEvent(plugin, event, dionaPlayer);
                Server.getInstance().getPluginManager().callEvent(bukkitListenerEvent);
                return bukkitListenerEvent.isCancelled();
            } else {
                return true;
            }
        }
    }

    private Player getPlayerByEvent(Event event) {
        // return player from PlayerEvent

        if (event instanceof EntityDamageByEntityEvent) {
            Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
            if (damager instanceof Player) {
                return (Player) damager;
            }
            if (damager instanceof EntityProjectile) {
                EntityProjectile projectile = (EntityProjectile) damager;
                Entity projectileSource = projectile.shootingEntity;
                if (projectileSource instanceof Player) return (Player) projectileSource;
            }
        }
        if (event instanceof EntityEvent) {
            Entity entity = ((EntityEvent) event).getEntity();
            if (entity instanceof Player) {
                return (Player) entity;
            }
            if (event instanceof ProjectileLaunchEvent) {
                Entity shooter = ((ProjectileLaunchEvent) event).getEntity().shootingEntity;
                return shooter instanceof Player ? (Player) shooter : null;
            }
        }
        if (event instanceof PlayerEvent) {
            return ((PlayerEvent) event).getPlayer();
        }

        Function<Event, Player> function = this.eventMap.get(event.getClass());
        if (function != null) return function.apply(event);

        // Try to get the player field from the event

        if (useReflectionToGetEventPlayer) {
            if (this.failedFieldCache.contains(event.getClass())) {
                return null;
            }
            Field playerField = this.eventFieldCache.getOrDefault(event.getClass(), null);
            if (playerField == null) {
                try {
                    playerField = event.getClass().getDeclaredField("player");
                    playerField.setAccessible(true);
                    Player player = (Player) playerField.get(event);
                    this.eventFieldCache.put(event.getClass(), playerField);
                    return player;
                } catch (Exception e) {
                    this.failedFieldCache.add(event.getClass());
                    return null;
                }
            }

            try {
                return (Player) playerField.get(event);
            } catch (Exception e) {
                return null;
            }

        }

        return null;
    }

    private void initEventMap() {
        this.eventMap.put(BlockBreakEvent.class, event -> ((BlockBreakEvent) event).getPlayer());
        //this.eventMap.put(BlockDamageEvent.class, event -> ((BlockDamageEvent) event).getPlayer());
        this.eventMap.put(BlockIgniteEvent.class, event -> (Player) ((BlockIgniteEvent) event).getEntity());
        //this.eventMap.put(BlockMultiPlaceEvent.class, event -> ((BlockMultiPlaceEvent) event).getPlayer());
        this.eventMap.put(BlockPlaceEvent.class, event -> ((BlockPlaceEvent) event).getPlayer());
        this.eventMap.put(SignChangeEvent.class, event -> ((SignChangeEvent) event).getPlayer());
        this.eventMap.put(EnchantItemEvent.class, event -> ((EnchantItemEvent) event).getEnchanter());
        this.eventMap.put(InventoryClickEvent.class, event -> ((InventoryClickEvent) event).getPlayer());
        //TODO: 啥玩意基岩版api里没有,以后重写事件
        /*
        this.eventMap.put(InventoryDragEvent.class, event -> {
            HumanEntity player = ((InventoryDragEvent) event).getWhoClicked();
            return player instanceof Player ? (Player) player : null;
        });
        this.eventMap.put(InventoryInteractEvent.class, event -> {
            HumanEntity player = ((InventoryInteractEvent) event).getWhoClicked();
            return player instanceof Player ? (Player) player : null;
        });
        this.eventMap.put(InventoryOpenEvent.class, event -> {
            HumanEntity player = ((InventoryOpenEvent) event).getPlayer();
            return player instanceof Player ? (Player) player : null;
        });
         */
        this.eventMap.put(VehicleDamageEvent.class, event -> {
            Entity attacker = ((VehicleDamageEvent) event).getAttacker();
            return attacker instanceof Player ? (Player) attacker : null;
        });
        this.eventMap.put(VehicleDestroyEvent.class, event -> {
            Entity attacker = ((VehicleDestroyEvent) event).getAttacker();
            return attacker instanceof Player ? (Player) attacker : null;
        });
        this.eventMap.put(PlayerInteractEvent.class, event -> ((PlayerInteractEvent) event).getPlayer());
        this.eventMap.put(PlayerMoveEvent.class, event -> ((PlayerMoveEvent) event).getPlayer());
        this.eventMap.put(DataPacketReceiveEvent.class, event -> ((DataPacketReceiveEvent) event).getPlayer());
        /*
        this.eventMap.put(VehicleEnterEvent.class, event -> {
            Entity enteredEntity = ((VehicleEnterEvent) event).getEntered();
            return enteredEntity instanceof Player ? (Player) enteredEntity : null;
        });
        this.eventMap.put(VehicleEntityCollisionEvent.class, event -> {
            Entity entity = ((VehicleEntityCollisionEvent) event).getEntity();
            return entity instanceof Player ? (Player) entity : null;
        });
        this.eventMap.put(VehicleExitEvent.class, event -> {
            Entity exitedEntity = ((VehicleExitEvent) event).getExited();
            return exitedEntity instanceof Player ? (Player) exitedEntity : null;
        });
         */
    }
}
