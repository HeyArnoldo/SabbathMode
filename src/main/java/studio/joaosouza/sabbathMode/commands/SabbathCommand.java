package studio.joaosouza.sabbathMode.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import studio.joaosouza.sabbathMode.SabbathMode;
import studio.joaosouza.sabbathMode.config.PluginConfig;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class SabbathCommand extends Command {

    private final SabbathMode plugin;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public SabbathCommand(SabbathMode plugin) {
        super("sabbath", "sabbathmode.admin", new String[]{});
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        PluginConfig config = plugin.getPluginConfig();

        switch (args[0].toLowerCase()) {
            case "reload":
                config.reloadConfig();
                sender.sendMessage(new TextComponent(ChatColor.GREEN + "Configuración recargada."));
                break;
            case "whitelist":
                handleWhitelist(sender, config, args);
                break;
            case "message":
                handleMessage(sender, config, args);
                break;
            case "testing":
                handleTesting(sender, config, args);
                break;
            default:
                sendHelp(sender);
                break;
        }
    }

    private void handleWhitelist(CommandSender sender, PluginConfig config, String[] args) {
        if (args.length < 3) {
            sendHelp(sender);
            return;
        }
        String player = args[2];
        if (args[1].equalsIgnoreCase("add")) {
            config.addWhitelistedPlayer(player);
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Añadido a la whitelist: " + player));
        } else if (args[1].equalsIgnoreCase("remove")) {
            config.removeWhitelistedPlayer(player);
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Eliminado de la whitelist: " + player));
        } else {
            sendHelp(sender);
        }
    }

    private void handleMessage(CommandSender sender, PluginConfig config, String[] args) {
        if (args.length < 2) {
            sendHelp(sender);
            return;
        }
        if (args[1].equalsIgnoreCase("show")) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', config.getSabbathDeniedMessage())));
        } else if (args[1].equalsIgnoreCase("set")) {
            if (args.length < 3) {
                sendHelp(sender);
                return;
            }
            String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            config.setSabbathDeniedMessage(message);
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Mensaje actualizado."));
        } else {
            sendHelp(sender);
        }
    }

    private void handleTesting(CommandSender sender, PluginConfig config, String[] args) {
        if (args.length < 2) {
            sendHelp(sender);
            return;
        }
        if (args[1].equalsIgnoreCase("on")) {
            config.setTestingEnabled(true);
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Modo de pruebas activado."));
        } else if (args[1].equalsIgnoreCase("off")) {
            config.setTestingEnabled(false);
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Modo de pruebas desactivado."));
        } else if (args[1].equalsIgnoreCase("start") && args.length >= 4) {
            DayOfWeek day = DayOfWeek.valueOf(args[2].toUpperCase());
            LocalTime time = LocalTime.parse(args[3]);
            config.setTestSabbathStart(day, time);
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Horario de inicio actualizado."));
        } else if (args[1].equalsIgnoreCase("end") && args.length >= 4) {
            DayOfWeek day = DayOfWeek.valueOf(args[2].toUpperCase());
            LocalTime time = LocalTime.parse(args[3]);
            config.setTestSabbathEnd(day, time);
            sender.sendMessage(new TextComponent(ChatColor.GREEN + "Horario de fin actualizado."));
        } else {
            sendHelp(sender);
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "Comandos SabbathMode:"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/sabbath reload"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/sabbath whitelist add <jugador>"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/sabbath whitelist remove <jugador>"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/sabbath message show"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/sabbath message set <mensaje>"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/sabbath testing on|off"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/sabbath testing start <DIA> <HH:mm>"));
        sender.sendMessage(new TextComponent(ChatColor.AQUA + "/sabbath testing end <DIA> <HH:mm>"));
    }
}
