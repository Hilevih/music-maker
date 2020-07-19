package me.hilevi.musicmaker;



import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

// The command /create gives the player items needed to make songs, as well as teleports them to the starting place
 

public class CommandCreateSong implements CommandExecutor {
	Main plugin;
	Board board;
	
	
	public CommandCreateSong(Main p) {
		plugin = p;
	}
	

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2,
			String[] arg3) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			World world = player.getWorld();
			
			// Set inventory items
			setInventory(player);
			
			
			// Teleport player to the starting position
			player.teleport(new Location(world, 0, 240, 20, 180, 0));
			
			// Give basic info about the board
			player.sendMessage("Emerald blocks represent rows, diamond block columns and bedrock empty noteslots.");
			player.sendMessage("If there's no board yet, click something and it will spawn");
		}
		return true;
	}
	

	private void setInventory(Player player) {
		player.getInventory().setItem(0, createItem(Material.SHEARS, "§dAdd / Copy", "Right click to add a note, row or column. Left click to copy note. Sneak + Left click to copy column. Sneak + Right click to paste copied columns."));
		player.getInventory().setItem(1, createItem(Material.LADDER, "§bChange pitch of the note you're looking at", "Left click to decrease. Right click to increase."));
		player.getInventory().setItem(2, createItem(Material.TNT, "§bChange volume of the note you're looking at", "Left click to decrease. Right click to increase."));
		player.getInventory().setItem(3, createItem(Material.NOTE_BLOCK, "§1Choose the sound type", "Click to open a GUI."));
		player.getInventory().setItem(4, createItem(Material.BLAZE_POWDER, "§6Choose pitch of the to be added note", "Left click to decrease. Right click to increase."));
		player.getInventory().setItem(5, createItem(Material.FEATHER, "§6Choose volume of the to be added note", "Left click to decrease. Right click to increase."));
		player.getInventory().setItem(6, createItem(Material.JUKEBOX, "§dListen to song", "Left click to listen from the column you're looking at. Right click to listen to the whole song."));
		player.getInventory().setItem(7, createItem(Material.REDSTONE_COMPARATOR, "§dOther settings and tools"));
		player.getInventory().setItem(8, createItem(Material.SKULL_ITEM, "§4Remove note, row or column"));
	}
	
	
	
	
	protected ItemStack createItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

}
