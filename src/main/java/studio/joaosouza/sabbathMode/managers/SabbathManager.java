package studio.joaosouza.sabbathMode.managers;

import net.md_5.bungee.api.plugin.Plugin;
import studio.joaosouza.sabbathMode.SabbathMode;
import studio.joaosouza.sabbathMode.config.PluginConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SabbathManager {

    private final Plugin plugin;
    private final PluginConfig config;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public SabbathManager(Plugin plugin) {
        this.plugin = plugin;
        this.config = SabbathMode.getInstance().getPluginConfig();
    }

    public SabbathCheckResult isSabbath(String playerIp){
        ZoneId playerZoneId = determineZoneFromIp(playerIp);

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

    private ZoneId determineZoneFromIp(String ip) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://ipapi.co/" + ip + "/timezone/"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body().trim();
                if (!body.isEmpty()) {
                    return ZoneId.of(body);
                }
            } else {
                plugin.getLogger().warning("Fallo obteniendo zona para " + ip + ": HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error al obtener zona para " + ip + ": " + e.getMessage());
        }
        return ZoneId.systemDefault();
    }

    // CHECK RESULT
    public static class SabbathCheckResult {
        private final boolean isSabbathActive;
        private final ZoneId zoneId;
        private final String saturdaySunsetTime;

        public SabbathCheckResult(boolean isSabbathActive, ZoneId zoneId, String saturdaySunsetTime) {
            this.isSabbathActive = isSabbathActive;
            this.zoneId = zoneId;
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


