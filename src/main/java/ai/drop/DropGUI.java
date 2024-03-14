package ai.drop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DropGUI {

    private final Player player;

    public DropGUI(Player player) {
        this.player = player;
    }

    public void open() {
        Inventory gui = Bukkit.createInventory(null, 9, "Drop Settings");

        // Dodaj itemy dla rud
        for (Material material : DropListener.dropChances.keySet()) {
            ItemStack item = DropListener.createDropItem(player.getUniqueId(), material);
            gui.addItem(item);
        }

        // Dodaj cobblestone na końcu
        ItemStack cobblestoneItem = new ItemStack(Material.COBBLESTONE);
        ItemMeta cobblestoneMeta = cobblestoneItem.getItemMeta();
        cobblestoneMeta.setDisplayName("Cobblestone");
        List<String> cobblestoneLore = new ArrayList<>();
        cobblestoneLore.add("Drop: " + (DropListener.cobblestoneEnabled.getOrDefault(player.getUniqueId(), true) ? "Włączony" : "Wyłączony"));
        cobblestoneMeta.setLore(cobblestoneLore);
        cobblestoneItem.setItemMeta(cobblestoneMeta);
        gui.setItem(8, cobblestoneItem);

        player.openInventory(gui);
        DropListener.playerInventories.put(player.getUniqueId(), gui);
    }

    // Metoda do aktualizacji GUI
    public void updateGUI() {
        Inventory gui = DropListener.playerInventories.get(player.getUniqueId());
        if (gui != null) {
            for (int i = 0; i < 8; i++) {
                ItemStack item = gui.getItem(i);
                if (item != null) {
                    Material material = item.getType();
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        List<String> lore = new ArrayList<>();
                        lore.add("Drop: " + (material.equals(Material.COBBLESTONE) ? (DropListener.cobblestoneEnabled.getOrDefault(player.getUniqueId(), true) ? "Włączony" : "Wyłączony") : (DropListener.dropEnabled.getOrDefault(player.getUniqueId(), new HashMap<>()).getOrDefault(material, true) ? "Włączony" : "Wyłączony")));
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        gui.setItem(i, item);
                    }
                }
            }
            // Aktualizuj Cobblestone
            ItemStack cobblestoneItem = gui.getItem(8);
            if (cobblestoneItem != null) {
                ItemMeta cobblestoneMeta = cobblestoneItem.getItemMeta();
                if (cobblestoneMeta != null) {
                    List<String> cobblestoneLore = new ArrayList<>();
                    cobblestoneLore.add("Drop: " + (DropListener.cobblestoneEnabled.getOrDefault(player.getUniqueId(), true) ? "Włączony" : "Wyłączony"));
                    cobblestoneMeta.setLore(cobblestoneLore);
                    cobblestoneItem.setItemMeta(cobblestoneMeta);
                    gui.setItem(8, cobblestoneItem);
                }
            }
        }
    }
}
