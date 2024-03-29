package ai.drop;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class DropListener implements Listener {

    public static HashMap<Material, Double> dropChances = new HashMap<>();
    public static HashMap<UUID, HashMap<Material, Boolean>> dropEnabled = new HashMap<>();
    public static HashMap<UUID, Boolean> cobblestoneEnabled = new HashMap<>();
    public static HashMap<UUID, Inventory> playerInventories = new HashMap<UUID, Inventory>();

    static {
        initializeDropChances();
        initializeDropSettings();
    }

    private static void initializeDropChances() {
        dropChances.put(Material.DIAMOND, 0.05);
        dropChances.put(Material.GOLD_INGOT, 0.1);
        dropChances.put(Material.IRON_INGOT, 0.2);
    }

    private static void initializeDropSettings() {
        // Inicjalizacja ustawień dla każdego gracza
        for (Player player : Bukkit.getOnlinePlayers()) {
            dropEnabled.put(player.getUniqueId(), new HashMap<>());
            cobblestoneEnabled.put(player.getUniqueId(), true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        Material blockMaterial = event.getBlock().getType();
        int blockY = event.getBlock().getY();
        ItemStack handItem = player.getInventory().getItemInMainHand();
        if (blockMaterial.equals(Material.STONE)) {
            event.setCancelled(true);
            player.giveExp(1);
            event.getBlock().setType(Material.AIR);
            boolean cobblestoneDropped = false;
            for (Material material : dropChances.keySet()) {
                Double chance = dropChances.get(material);
                Random random = new Random();
                if (canDropMaterial(handItem, material) && chance != null && random.nextDouble() <= chance && dropEnabled.get(playerId).getOrDefault(material, true)) {
                    int minHeight = getMinDropLevel(material);
                    if (blockY < minHeight) {
                        int amount = 1 + (handItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) > 0 ? random.nextInt(handItem.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) + 1) : 0);
                        ItemStack itemStack = new ItemStack(material, amount);
                        addItem(player, itemStack);
                        player.sendMessage("§7 " + "Wydropiłeś " + amount + " " + material.toString());
                        return;
                    }
                }
                if (cobblestoneEnabled.get(playerId) && !cobblestoneDropped) {
                    ItemStack itemStack = new ItemStack(Material.COBBLESTONE, 1);
                    addItem(player, itemStack);
                    cobblestoneDropped = true;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        UUID playerId = player.getUniqueId();
        Inventory clickedInventory = event.getClickedInventory();
        if (playerInventories.containsKey(playerId) && clickedInventory != null && clickedInventory.equals(playerInventories.get(playerId))) {
            event.setCancelled(true);
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem != null) {
                Material material = clickedItem.getType();
                if (dropChances.containsKey(material)) {
                    dropEnabled.get(playerId).put(material, !dropEnabled.get(playerId).getOrDefault(material, true));
                    player.getOpenInventory().setItem(event.getSlot(), createDropItem(playerId, material));
                    // Aktualizuj nazwę itemu w GUI
                    ItemMeta itemMeta = clickedItem.getItemMeta();
                    itemMeta.setDisplayName(material.toString() + " " + (dropEnabled.get(playerId).getOrDefault(material, true) ? "Włączony" : "Wyłączony"));
                    clickedItem.setItemMeta(itemMeta);
                } else if (material.equals(Material.COBBLESTONE)) {
                    cobblestoneEnabled.put(playerId, !cobblestoneEnabled.getOrDefault(playerId, true));
                    new DropGUI(player).updateGUI(); // Odśwież GUI po kliknięciu w Cobblestone
                }
            }
        }
    }

    // Metoda do tworzenia itemów do GUI
    public static ItemStack createDropItem(UUID playerId, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        Double chance = dropChances.get(material);
        double chanceValue = (chance != null) ? chance * 100 : 0;

        List<String> lore = new ArrayList<>();
        lore.add("Drop: " + (material.equals(Material.COBBLESTONE) ? (cobblestoneEnabled.getOrDefault(playerId, true) ? "Włączony" : "Wyłączony") : (dropEnabled.getOrDefault(playerId, new HashMap<>()).getOrDefault(material, true) ? "Włączony" : "Wyłączony")));
        lore.add("Szansa: " + new DecimalFormat("0.##").format(chanceValue) + "%");

        int minHeight = getMinDropLevel(material);
        lore.add("Drop od poziomu: " + minHeight);

        meta.setDisplayName(material.toString());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // Metoda sprawdzająca, czy gracz może dostać dany materiał
    private boolean canDropMaterial(ItemStack handItem, Material material) {
        Material toolMaterial = handItem.getType();
        switch (toolMaterial) {
            case WOODEN_PICKAXE:
                return material.equals(Material.COBBLESTONE);
            case STONE_PICKAXE:
                return material.equals(Material.COBBLESTONE) || material.equals(Material.IRON_INGOT) || material.equals(Material.GOLD_INGOT);
            case IRON_PICKAXE:
            case DIAMOND_PICKAXE:
                return true;
            default:
                return false;
        }
    }

    // Metoda zwracająca minimalny poziom dla danego materiału
    private static int getMinDropLevel(Material material) {
        return (material.equals(Material.DIAMOND)) ? 50 : 80;
    }

    // Metoda dodająca przedmiot do ekwipunku gracza
    private void addItem(Player p, ItemStack iS) {
        Inventory inv = p.getInventory();

        for (ItemStack stack : inv.getContents()) {
            if (stack != null && stack.isSimilar(iS)) {
                int fS = stack.getMaxStackSize() - stack.getAmount();

                if (fS > 0) {
                    stack.setAmount(stack.getAmount() + 1);
                    return;
                }
            }
        }

        int fES = inv.firstEmpty();

        if (fES != -1) {
            inv.setItem(fES, iS);
            return;
        }

        p.getWorld().dropItem(p.getLocation(), iS);
    }

    // Metoda do zapisu stanu dla danego materiału w pliku konfiguracyjnym
    private void saveDropEnabled(UUID playerId, Material material, boolean enabled) {
        FileConfiguration config = Main.getPlugin().getConfig();
        config.set("dropEnabled." + playerId.toString() + "." + material.toString(), enabled);
        Main.getPlugin().saveConfig();
    }
}
