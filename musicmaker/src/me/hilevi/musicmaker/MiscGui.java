package me.hilevi.musicmaker;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MiscGui  implements Listener {
    private final Inventory inv;
    private static Board board;
    Main plugin;
    
    // GUI for all the extra methods

    public MiscGui(Main p, Board b) {
    	int i = 9; // Size of inventory
    	plugin = p;
    	inv = Bukkit.createInventory(null, i, "All kinds of settings");
    	if (b != null)  {
    		board = b;
    		initializeItems();
    	}
    }


	// You can call this whenever you want to put the items in
    public void initializeItems() {
    	inv.addItem(createGuiItem(Material.NOTE_BLOCK, "Disable pitches", "Choose notes that can't be cycled to."));
    	inv.addItem(createGuiItem(Material.SUGAR, "Change song speed", "Right click to increase, left to decrease", "Higher the speed value, slower the song"));
    	inv.addItem(createGuiItem(Material.DRAGON_EGG, "Save song", "Name must be given afterwards in chat", "Requires a file called 'songs' in plugins folder"));
    	inv.addItem(createGuiItem(Material.RECORD_3, "Load song", "Name must be given afterwards in chat", "Requires a file called 'songs' in plugins folder"));
    	inv.addItem(createGuiItem(Material.REDSTONE_COMPARATOR, "Change amount of notes in measure", "Left click to decrease. Right click to increase."));
    	inv.addItem(createGuiItem(Material.BONE, "Remove selected columns"));
    	inv.addItem(createGuiItem(Material.DISPENSER, "Sparse notes", "Adds one column between each columns", "CANNOT BE UNDONE"));
    	inv.setItem(8, createGuiItem(Material.TNT, "Restart", "CANNOT BE UNDONE (unless saved before)"));
    }

    // Method to create a gui item with a custom name, and description
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
    	
    	if (e.getInventory().getName() != "All kinds of settings") return;
        
        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player p = (Player) e.getWhoClicked();
        boolean isLeftClick = e.getClick().equals(ClickType.LEFT);
        
        if (clickedItem.getType() == Material.NOTE_BLOCK) {
        	PitchDisablerGui gui = new PitchDisablerGui(plugin, board);
        	gui.openInventory(p);
        } else if (clickedItem.getType() == Material.SUGAR) {
        	board.changeSpeed(p, isLeftClick);
        } else if (clickedItem.getType() == Material.TNT) {
        	board.reset();
        } else if (clickedItem.getType() == Material.DRAGON_EGG) {
        	p.closeInventory();
        	board.save(p);
        } else if (clickedItem.getType() == Material.RECORD_3) {
        	p.closeInventory();
        	board.load(p);
        } else if (clickedItem.getType() == Material.REDSTONE_COMPARATOR) {
        	board.changeNotesInMeasureAmount(p, isLeftClick);
        } else if (clickedItem.getType() == Material.BONE) {
        	board.clearSelectedColumns();
        } else if (clickedItem.getType() == Material.DISPENSER) {
        	board.sparseNotes();
        }
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().getName() == "All kinds of settings") {
          e.setCancelled(true);
        }
    }
	
	
}
