package me.hilevi.musicmaker;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// GUI for choosing a wanted pitch

public class PitchChooserGui implements Listener {
    private final Inventory inv;
    private static Board board;

    public PitchChooserGui(Main p, Board b) {
    	int i = 27;
    	
    	inv = Bukkit.createInventory(null, i, "Choose pitch");
    	if (b != null)  {
    		board = b;
    		initializeItems();
    	}
    }


	// You can call this whenever you want to put the items in
    public void initializeItems() {
    	for (int i=0; i<25; i++) {
    		String key = i + " " + board.getPitchByInt(i);
    		byte data;
    		if (board.getSelectedPitch() == i) data = 4; //Yellow, currently selected
    		else if (board.isDisabled(i)) data = 14; //Red, disabled by user
    		else data = 5; //Green, free to choose
    		ItemStack item = createGuiItem(Material.WOOL, data, key);
    		inv.addItem(item);
    	}
    }
    
  
	private void updateItems(InventoryClickEvent e) {
    	for (int i=0; i<25; i++) {
    		if (board.getSelectedPitch() == i) e.getInventory().setItem(i, createGuiItem(Material.WOOL, (byte) 4, e.getInventory().getItem(i).getItemMeta().getDisplayName()));
    		else if (board.isDisabled(i)) e.getInventory().setItem(i, createGuiItem(Material.WOOL, (byte) 14, e.getInventory().getItem(i).getItemMeta().getDisplayName()));
    		else e.getInventory().setItem(i, createGuiItem(Material.WOOL, (byte) 5, e.getInventory().getItem(i).getItemMeta().getDisplayName()));
    	}
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, byte data, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1, data);
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
    	
    	if (e.getInventory().getName() != "Choose pitch") return;
        
        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        
        int posClicked = e.getRawSlot();
        
        board.setSelectedPitch(posClicked);
        board.playSelectedSound((Player) e.getWhoClicked());
        updateItems(e);
    
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().getName() == "Choose pitch") {
          e.setCancelled(true);
        }
    }
}
