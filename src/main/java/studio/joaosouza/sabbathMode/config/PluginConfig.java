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


    public PluginConfig(Plugin plugin, File configFile) {
        this.plugin = plugin;
        this.configFile = configFile;
    }

    public void loadConfig() {
        try{
            //CONFIGURACIÓN DESDE YAML
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            sabbathDeniedMessage = config.getString("messages.sabbath-denied",
                    "&c¡Hola! En tu zona horaria ({zone_id}), es Sábado desde la puesta de sol hasta la puesta de sol. " +
                            "&cEl acceso está restringido durante este período por motivos religiosos. " +
                            "&cPor favor, intenta conectarte de nuevo después de las {saturday_sunset_time} (hora local).");

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
            config.set("messages.sabbath-denied", "&c¡Hola! En tu zona horaria ({zone_id}), es Sábado desde la puesta de sol hasta la puesta de sol. " +
                    "&cEl acceso está restringido durante este período por motivos religiosos. " +
                    "&cPor favor, intenta conectarte de nuevo después de las {saturday_sunset_time} (hora local).");
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
}
