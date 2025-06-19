package studio.joaosouza.sabbathMode.config;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalTime;

public class PluginConfig {

    private final Plugin plugin;
    private final File configFile;
    private Configuration config;

    private String sabbathDeniedMessage;

    //VARIABLES DE TESTING
    private boolean testingEnabled;
    private DayOfWeek testSabbathStartDay;
    private LocalTime testSabbathStartTime;
    private DayOfWeek testSabbathEndDay;
    private LocalTime testSabbathEndTime;

    private java.util.Set<String> whitelistedPlayers;


    public PluginConfig(Plugin plugin, File configFile) {
        this.plugin = plugin;
        this.configFile = configFile;
    }

    public void loadConfig() {
        try{
            //CONFIGURACIÓN DESDE YAML
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            sabbathDeniedMessage = config.getString("messages.sabbath-denied", "&cEl servidor está cerrado hoy.\n\n&79. Seis días trabajarás, y harás toda tu obra;\n&710. mas el séptimo día es reposo para Jehová tu Dios;\n&6Exodo 20:9-10\n\n&cAcceso restringido en tu zona horaria (&e{zone_id}&c).\n&cPuedes ingresar a partir del &eSábado &ca las &e{saturday_sunset_time} &c(hora local).");

            // CARGAR LISTA DE WHITELIST
            java.util.List<String> wl = config.getStringList("whitelist");
            if (wl != null) {
                whitelistedPlayers = new java.util.HashSet<>();
                for (String p : wl) {
                    if (p != null) {
                        whitelistedPlayers.add(p.toLowerCase());
                    }
                }
            } else {
                whitelistedPlayers = java.util.Collections.emptySet();
            }

            // CARGAR CONFIGURACIONES DE TESTING:
            testingEnabled = config.getBoolean("testing.enabled", false);
            if (testingEnabled) {
                try {
                    testSabbathStartDay = DayOfWeek.valueOf(config.getString("testing.sabbath-start-day", "FRIDAY").toUpperCase());
                    testSabbathStartTime = LocalTime.parse(config.getString("testing.sabbath-start-time", "18:00"));
                    testSabbathEndDay = DayOfWeek.valueOf(config.getString("testing.sabbath-end-day", "SATURDAY").toUpperCase());
                    testSabbathEndTime = LocalTime.parse(config.getString("testing.sabbath-end-time", "18:00"));
                    plugin.getLogger().warning("¡Modo de PRUEBA (TESTING) HABILITADO en SabbathMode! Asegúrate de deshabilitarlo en producción.");
                    plugin.getLogger().warning(String.format("Sabbath de Prueba: %s %s a %s %s",
                            testSabbathStartDay, testSabbathStartTime, testSabbathEndDay, testSabbathEndTime));
                }catch (Exception e) {
                    plugin.getLogger().severe("Error al cargar la configuración de prueba :( :" + e.getMessage());
                    testingEnabled = false;
                }
            }

            saveDefaultValues();

        } catch(Exception ex){
            plugin.getLogger().severe("Error al cargar la configuración: " + ex.getMessage());
        }


    }

    private void saveDefaultValues() {
        boolean changed = false;

        if (!config.contains("messages.sabbath-denied")) {
            config.set("messages.sabbath-denied", "&cEl servidor está cerrado hoy.\n\n&79. Seis días trabajarás, y harás toda tu obra;\n&710. mas el séptimo día es reposo para Jehová tu Dios;\n&6Exodo 20:9-10\n\n&cAcceso restringido en tu zona horaria (&e{zone_id}&c).\n&cPuedes ingresar a partir del &eSábado &ca las &e{saturday_sunset_time} &c(hora local).");
            changed = true;
        }

        if (!config.contains("testing.enabled")) {
            config.set("testing.enabled", false);
            config.set("testing.sabbath-start-day", "FRIDAY");
            config.set("testing.sabbath-start-time", "18:00");
            config.set("testing.sabbath-end-day", "SATURDAY");
            config.set("testing.sabbath-end-time", "18:00");
            changed = true;
        }

        if (!config.contains("whitelist")) {
            java.util.List<String> defaults = new java.util.ArrayList<>();
            defaults.add("EjemploUsuario1");
            defaults.add("EjemploUsuario2");
            config.set("whitelist", defaults);
            changed = true;
        }

        if (changed) {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
                plugin.getLogger().info("Configuración por defecto actualizada/guardada.");
            } catch (Exception ex) {
                plugin.getLogger().severe("Error al guardar la configuración por defecto: " + ex.getMessage());
            }
        }
    }

    public String getSabbathDeniedMessage() {
        return sabbathDeniedMessage;
    }

    public boolean isTestingEnabled() {
        return testingEnabled;
    }

    public DayOfWeek getTestSabbathStartDay() {
        return testSabbathStartDay;
    }

    public LocalTime getTestSabbathStartTime() {
        return testSabbathStartTime;
    }

    public DayOfWeek getTestSabbathEndDay() {
        return testSabbathEndDay;
    }

    public LocalTime getTestSabbathEndTime() {
        return testSabbathEndTime;
    }

    public java.util.Set<String> getWhitelistedPlayers() {
        return whitelistedPlayers;
    }
}
