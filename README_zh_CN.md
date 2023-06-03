# PluginHooker

PluginHooker 是一个 Nukkit 插件，它能够为开发者提供一种便捷的方式来控制玩家的各种监听器。
[Discord](https://discord.gg/bCQ8pEgk4t)

[English](README.md)

## 功能

* Hook Nukkit 事件
* Hook Netty pipeline
* 为每个玩家独立控制监听器

## 已测试环境

* Nukkit/PM1E/PNX: 1.19以上
* Netty: 4.0/4.1

## 用法

将PluginHooker作为Maven依赖项 ([Jitpack](https://jitpack.io/#Diona-testserver/PluginHooker))
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
      <groupId>com.github.Catrainbow233</groupId>
      <artifactId>PluginHooker</artifactId>
      <version>1.0.1</version>
    </dependency>
</dependencies>
```


添加/移除需要被hook的插件
```java
public void hookPlugin() {
    PluginHooker.getPluginManager().addPlugin(pluginToHook);
}

public void unHookPlugin() {
    PluginHooker.getPluginManager().removePlugin(pluginToHook);
}
```

为玩家启用/禁用指定的插件

```java
public void enablePluginForPlayer(Player player) {
    DionaPlayer dionaPlayer = PluginHooker.getPlayerManager().getDionaPlayer(player);
    if (dionaPlayer == null) {
        return;
    }
    dionaPlayer.enablePlugin(pluginToHook);
}

public void disablePluginForPlayer(Player player) {
    DionaPlayer dionaPlayer = PluginHooker.getPlayerManager().getDionaPlayer(player);
    if (dionaPlayer == null) {
        return;
    }
    dionaPlayer.disablePlugin(pluginToHook);
}
```

如果要拦截或在事件被执行前执行自定义的操作,请添加一个事件监听器:
```java
public class ExampleListener implements Listener {

    @EventHandler
    public void onNukkitEvent(NukkitListenerEvent event) {
        // do something
    }
    
}
```

## 特别感谢

* [Poke](https://github.com/Pokemonplatin) 提供了hook事件相关的帮助和需要hook的事件列表
