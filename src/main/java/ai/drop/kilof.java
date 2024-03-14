package ai.drop;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class kilof implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Komenda tylko dla graczy!");
            return true;
        }

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("kilof")) {
            ItemStack kilof = getkilof();
            player.getInventory().addItem(kilof);
            player.sendMessage("Otrzymałeś bombowy kilof!");
        }

        return true;
    }

    private ItemStack getkilof() {
        ItemStack getkilof = new ItemStack(Material.DIAMOND_PICKAXE);

        // Dodanie enchantmentów do łuku
        getkilof.addUnsafeEnchantment(Enchantment.DIG_SPEED, 6);
        getkilof.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);
        getkilof.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
        return getkilof;
    }
}
