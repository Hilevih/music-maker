package me.hilevi.musicmaker;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

// InputGetter gets the input from the player when needed

public class InputGetter implements Listener {
	
	public static ArrayList<Player> waitingForInputPlayers = new ArrayList<>();
	private static String input = "";
	
	public InputGetter(Main p) {
		
	}
	
	public String getInput(Player p) {
		waitingForInputPlayers.add(p);
		while(waitingForInputPlayers.contains(p));
		return input;
	}
	
	
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		// If player the sends a message is on a "waiting list", will get the input
		if (waitingForInputPlayers.indexOf(p) != -1) {
			 e.setCancelled(true);
			 input = e.getMessage();

			 waitingForInputPlayers.remove(p);
		 }
	}
}
