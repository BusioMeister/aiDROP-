package ai.drop;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Gamemode implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Ta komenda może być używana tylko przez graczy!");
            return true;
        }

        Player player = (Player) sender;

        if (command.getName().equalsIgnoreCase("gamemode") || command.getName().equalsIgnoreCase("gm")) {
            if (args.length != 1) {
                player.sendMessage(ChatColor.RED + "Poprawne użycie: /gamemode <creative|survival|adventure|spectator>");
                return true;
            }

            GameMode newGameMode = getGameModeFromString(args[0]);
            if (newGameMode == null) {
                player.sendMessage(ChatColor.RED + "Nieprawidłowy tryb gry. Dostępne tryby: creative, survival, adventure, spectator.");
                return true;
            }

            player.setGameMode(newGameMode);
            player.sendMessage(ChatColor.GREEN + "Zmieniłeś tryb gry na " + newGameMode.toString());
            return true;
        }

        return false;
    }

    private GameMode getGameModeFromString(String modeString) {
        try {
            int modeValue = Integer.parseInt(modeString);
            switch (modeValue) {
                case 0:
                    return GameMode.SURVIVAL;
                case 1:
                    return GameMode.CREATIVE;
                case 2:
                    return GameMode.ADVENTURE;
                case 3:
                    return GameMode.SPECTATOR;
                default:
                    return null;
            }
        } catch (NumberFormatException e) {
            switch (modeString.toLowerCase()) {
                case "creative":
                    return GameMode.CREATIVE;
                case "survival":
                    return GameMode.SURVIVAL;
                case "adventure":
                    return GameMode.ADVENTURE;
                case "spectator":
                    return GameMode.SPECTATOR;
                default:
                    return null;
            }
        }
    }
}