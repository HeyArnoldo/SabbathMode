package studio.joaosouza.sabbathMode;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import studio.joaosouza.sabbathMode.config.PluginConfig;
import studio.joaosouza.sabbathMode.listeners.ConnectionListener;
import studio.joaosouza.sabbathMode.managers.SabbathManager;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public final class SabbathMode extends Plugin {

    private static SabbathMode instance; // Patrón Singleton para acceso fácil al plugin
    private PluginConfig pluginConfig;
    private SabbathManager sabbathManager;


    @Override
    public void onEnable() {
        instance = this; // SINGLETON
        getLogger().info("----------------------------------------");
        getLogger().info(" §aSabbathMode §f- §bIniciando...");
        getLogger().info("----------------------------------------");

        // 1. CONFIGURACIÓN:
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            // Si el archivo de configuración no existe, cópialo desde los recursos por defecto
            try(InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            }catch (Exception e) {
                getLogger().severe("No se pudo cargar la configuración por defecto :c  : " + e.getMessage());
                getProxy().getPluginManager().unregisterListeners(this); // Desregistra si algo falla
                return; // Detiene la ejecución del onEnable
            }
        }

        this.pluginConfig = new PluginConfig(this, configFile);
        this.pluginConfig.loadConfig(); //CARGAR LA CONFIGURACIÓN

        //2. INICIALIZAR MANAGERS
        this.sabbathManager = new SabbathManager(this);

        //3. REGISTRAR LISTENERS
        ProxyServer.getInstance().getPluginManager().registerListener(this, new ConnectionListener(this));

        //4. REGISTRAR COMANDOS
        getLogger().info("----------------------------------------");
        getLogger().info(" §aSabbathMode §f- §aHabilitado Correctamente.");
        getLogger().info("----------------------------------------");
    }

    @Override
    public void onDisable() {
        getLogger().info("§aSabbathMode §f- §cCerrando...");
        ProxyServer.getInstance().getPluginManager().unregisterListeners(this);
        getLogger().info("§aSabbathMode §f- §cDeshabilitado.");
    }

    public static SabbathMode getInstance() {
        return instance;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public SabbathManager getSabbathManager(){
        return sabbathManager;
    }
}
