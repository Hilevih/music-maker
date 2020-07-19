package me.hilevi.musicmaker;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/* Class for listening to interact events. If player interacts and is within the board, calls appropriate method.
 * First click creates board regardless.
 */

public class TargetBlockChecker implements Listener {
	private static Board board = null;
	private Main plugin;
	
	public TargetBlockChecker(Main p) {
		plugin = p;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Block block = player.getTargetBlock(null, 50);
		Material heldItem = player.getInventory().getItemInMainHand().getData().getItemType();
		boolean isLeftClick = event.getAction().equals(Action.LEFT_CLICK_AIR);
		boolean isSneaking = player.isSneaking();
		if (board == null) board = new Board(plugin, event.getPlayer().getWorld());
		else {
			if (onBoard(board, block)) {
				if (heldItem.equals(Material.SHEARS)) {
					board.firstAdder(block, player, isLeftClick, isSneaking);
				} else if (heldItem.equals(Material.LADDER)) {
					board.changePitch(block, player, isLeftClick);
				} else if (heldItem.equals(Material.TNT)) {
					board.changeVolume(block, player, isLeftClick);
				} else if (heldItem.equals(Material.NOTE_BLOCK)) {
					board.chooseSound(player);
				} else if (heldItem.equals(Material.BLAZE_POWDER)) {
					board.changeSelectedPitch(isLeftClick, isSneaking, player);
				} else if (heldItem.equals(Material.FEATHER)) {
					board.changeSelectedVolume(player, isLeftClick);
				} else if (heldItem.equals(Material.JUKEBOX)) {
					board.playSong(block, player, isLeftClick, isSneaking);
				} else if (heldItem.equals(Material.REDSTONE_COMPARATOR)) {
					board.openSettings(player);
				} else if (heldItem.equals(Material.SKULL_ITEM)) {
					board.remove(block);
				}
			}
			
		}
		
	}
	
	private boolean onBoard(Board board, Block targetedBlock) {
		double x = targetedBlock.getX();
		double y = targetedBlock.getY();
		if (250 > y && 250-board.getHeight() < y && 0 < x && board.getWidth() > x) return true;
		else return false;
	}

	
	

}
