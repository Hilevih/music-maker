package me.hilevi.musicmaker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// GUI for choosing the wanted instrument / sound

public class SoundTypeChooserGui implements Listener {
    private final Inventory inv;
    private static Board board;

    public SoundTypeChooserGui(Main p, Board b) {
    	int i = 18;
    	
    	inv = Bukkit.createInventory(null, i, "Sound chooser");
    	if (b != null)  {
    		board = b;
    		initializeItems();
    	}
    }


	// You can call this whenever you want to put the items in
    public void initializeItems() {
    	List<Sound> keys = new ArrayList<Sound> (board.getSounds().keySet());
    	keys.sort((n1, n2)-> n1.name().compareTo(n2.name())); // Organizes keys alphabetically
    	
    	for (Sound key : keys) {
    		inv.addItem(createGuiItem(board.getSounds().get(key), key.name()));
    	}
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
    	
    	if (e.getInventory().getName() != "Sound chooser") return;
        
        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

    
        board.setSelectedSound(Sound.valueOf(clickedItem.getItemMeta().getDisplayName()));
        board.playSelectedSound((Player) e.getWhoClicked());
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().getName() == "Sound chooser") {
          e.setCancelled(true);
        }
    }
}
