package studio.joaosouza.sabbathMode.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import studio.joaosouza.sabbathMode.SabbathMode;
import studio.joaosouza.sabbathMode.managers.SabbathManager;

import java.net.InetSocketAddress;

public class ConnectionListener implements Listener {

    private final SabbathMode plugin;

    public ConnectionListener(SabbathMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPreLogin(PreLoginEvent event) {
        if (event.isCancelled()) {
            return;
        }

        InetSocketAddress address = event.getConnection().getAddress();
        String ipAddress = address.getAddress().getHostAddress();
        String playerName = event.getConnection().getName();

        if (plugin.getPluginConfig().getWhitelistedPlayers().contains(playerName.toLowerCase())) {
            plugin.getLogger().info("Conexión de " + playerName + " (IP: " + ipAddress + ") PERMITIDA por whitelist.");
            return;
        }

        SabbathManager.SabbathCheckResult checkResult = plugin.getSabbathManager().isSabbath(ipAddress);

        if (checkResult.isSabbathActive()) {
            String deniedMessage = plugin.getPluginConfig().getSabbathDeniedMessage()
                    .replace("{zone_id}", checkResult.getZoneId() != null ? checkResult.getZoneId().getId() : "Desconocida")
                    .replace("{saturday_sunset_time}", checkResult.getSaturdaySunsetTime());

            String formattedDeniedMessage = ChatColor.translateAlternateColorCodes('&', deniedMessage);

            event.setCancelReason(new TextComponent(formattedDeniedMessage));
            event.setCancelled(true);
            plugin.getLogger().info("Conexión de " + playerName + " (IP: " + ipAddress + ") CANCELADA por SabbathMode. Zona: " + checkResult.getZoneId());
        } else {
            // Si no es Sabbath, permitimos la conexión
            plugin.getLogger().info("Conexión de " + playerName + " (IP: " + ipAddress + ") PERMITIDA. No es Sabbath en su zona horaria (" + checkResult.getZoneId() + ").");
        }
    }


}
