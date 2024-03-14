package ai.drop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DropCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Komenda tylko dla graczy!");
            return true;
        }
        Player player = (Player)sender;
        if (label.equalsIgnoreCase("drop")) {
            (new DropGUI(player)).open();
            return true;
        }
        return false;
    }
}