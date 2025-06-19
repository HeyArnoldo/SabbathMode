package studio.joaosouza.sabbathMode.managers;

import net.md_5.bungee.api.plugin.Plugin;
import studio.joaosouza.sabbathMode.SabbathMode;
import studio.joaosouza.sabbathMode.config.PluginConfig;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SabbathManager {

    private final Plugin plugin;
    private final PluginConfig config;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public SabbathManager(Plugin plugin) {
        this.plugin = plugin;
        this.config = SabbathMode.getInstance().getPluginConfig();
    }

    public SabbathCheckResult isSabbath(String playerIp){
        //ACÁ SE ENLAZA CON LA API DE GEOLOCAKIZACIÓN PERO TODAVÍA NO LO HACEMOS xd
        ZoneId playerZoneId = null;

        try{
            // EJEMPLO: Esto debería ser reemplazado por la llamada a tu servicio GeoIP.
            // Si tienes una clase `GeoIpService`, sería algo como:
            // String timezoneString = geoIpService.getTimezone(playerIp);
            // if (timezoneString != null) {
            //     playerZoneId = ZoneId.of(timezoneString);
            // } else {
            //     plugin.getLogger().warning("No se pudo determinar la zona horaria para IP: " + playerIp + ". Usando zona de fallback.");
            //     playerZoneId = ZoneId.of("America/Mexico_City"); // Zona de fallback si GeoIP falla
            // }

            playerZoneId = ZoneId.of("America/Mexico_City"); // Zona horaria de Ciudad de México por defecto
        } catch (Exception e) {
            plugin.getLogger().warning("Error al intentar determinar la zona horaria de la ip: " + playerIp + " :" + e.getMessage());
            playerZoneId = ZoneId.of("America/Mexico_City"); // Fallback
        }

        ZonedDateTime nowInPlayerZone = ZonedDateTime.now(playerZoneId);

        ZonedDateTime sabbathStart;
        ZonedDateTime sabbathEnd;

        // --- Lógica de prueba vs. producción ---
        if (config.isTestingEnabled()) {
            // Usar valores de prueba desde la configuración
            DayOfWeek testStartDay = config.getTestSabbathStartDay();
            LocalTime testStartTime = config.getTestSabbathStartTime();
            DayOfWeek testEndDay = config.getTestSabbathEndDay();
            LocalTime testEndTime = config.getTestSabbathEndTime();

            sabbathStart = nowInPlayerZone
                    .with(testStartDay)
                    .with(testStartTime);

            sabbathEnd = nowInPlayerZone
                    .with(testEndDay)
                    .with(testEndTime);

            // Ajustar el rango de tiempo si el día de inicio/fin ya ha pasado en la semana actual.
            // Esto asegura que siempre estemos evaluando el Sabbath "más cercano" (esta semana o la siguiente).
            if (nowInPlayerZone.isAfter(sabbathEnd)) {
                // Si ya pasó el fin del Sabbath de esta semana, movemos el inicio y fin a la próxima semana
                sabbathStart = sabbathStart.plusWeeks(1);
                sabbathEnd = sabbathEnd.plusWeeks(1);
            } else if (nowInPlayerZone.isBefore(sabbathStart) &&
                    sabbathStart.getDayOfWeek().getValue() < nowInPlayerZone.getDayOfWeek().getValue()) {
                // Si el día de inicio del Sabbath ya pasó esta semana (ej. hoy es lunes, Sabbath empieza el domingo),
                // movemos el inicio y fin a la próxima semana.
                sabbathStart = sabbathStart.plusWeeks(1);
                sabbathEnd = sabbathEnd.plusWeeks(1);
            }


            plugin.getLogger().info("Modo de PRUEBA: Comprobando Sabbath entre " + sabbathStart.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) +
                    " y " + sabbathEnd.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + " en zona " + playerZoneId.getId());

        } else {
            // Lógica normal de producción (Viernes 18:00 a Sábado 18:00)
            sabbathStart = nowInPlayerZone
                    .with(DayOfWeek.FRIDAY)
                    .withHour(18)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            sabbathEnd = nowInPlayerZone
                    .with(DayOfWeek.SATURDAY)
                    .withHour(18)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            if (nowInPlayerZone.isAfter(sabbathEnd)) {
                sabbathStart = sabbathStart.plusWeeks(1);
                sabbathEnd = sabbathEnd.plusWeeks(1);
            }
        }

        boolean isSabbathActive = !nowInPlayerZone.isBefore(sabbathStart) && nowInPlayerZone.isBefore(sabbathEnd);

        String formattedSabbathEndTime = sabbathEnd.toLocalTime().format(TIME_FORMATTER);

        return new SabbathCheckResult(isSabbathActive, playerZoneId, formattedSabbathEndTime);
    }

    // CHECK RESULT
    public static class SabbathCheckResult {
        private final boolean isSabbathActive;
        private final ZoneId zoneId;
        private final String saturdaySunsetTime;

        public SabbathCheckResult(boolean isSabbathActive, ZoneId zoneId, String saturdaySunsetTime) {
            this.isSabbathActive = isSabbathActive;
            this.zoneId = ZoneId.systemDefault();
            this.saturdaySunsetTime = saturdaySunsetTime;
        }

        public boolean isSabbathActive() {
            return isSabbathActive;
        }

        public ZoneId getZoneId() {
            return zoneId;
        }

        public String getSaturdaySunsetTime() {
            return saturdaySunsetTime;
        }
    }
}


