package dev.diona.pluginhooker.config;

import cn.nukkit.utils.Config;
import dev.diona.pluginhooker.PluginHooker;
import java.lang.reflect.Field;

public class ConfigManager {

    private final Config config = new Config(PluginHooker.getInstance().getDataFolder() + "/config.yml", 2);

    public ConfigManager() {
        try {
            if (!config.exists("hook")){
                PluginHooker.getInstance().saveResource("config.yml",true);
            }
            config.save(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadConfig(Object target) {
        Class<?> targetClass = target.getClass();
        for (Field field : targetClass.getFields()) {

            ConfigPath annotation = field.getAnnotation(ConfigPath.class);
            if (annotation == null) continue;

            Class<?> type = field.getType();
            try {
                if (type == int.class || type == Integer.class) {
                    field.set(target, config.getInt(annotation.value()));
                } else if (type == long.class || type == Long.class) {
                    field.set(target, config.getLong(annotation.value()));
                } else if (type == double.class || type == Double.class) {
                    field.set(target, config.getDouble(annotation.value()));
                } else if (type == boolean.class || type == Boolean.class) {
                    field.set(target, config.getBoolean(annotation.value()));
                } else if (type == String.class) {
                    field.set(target, config.getString(annotation.value()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loadConfig(Class<?> targetClass) {
        for (Field field : targetClass.getFields()) {

            ConfigPath annotation = field.getAnnotation(ConfigPath.class);
            if (annotation == null) continue;

            Class<?> type = field.getType();
            try {
                if (type == int.class || type == Integer.class) {
                    field.set(null, config.getInt(annotation.value()));
                } else if (type == long.class || type == Long.class) {
                    field.set(null, config.getLong(annotation.value()));
                } else if (type == double.class || type == Double.class) {
                    field.set(null, config.getDouble(annotation.value()));
                } else if (type == boolean.class || type == Boolean.class) {
                    field.set(null, config.getBoolean(annotation.value()));
                } else if (type == String.class) {
                    field.set(null, config.getString(annotation.value()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
