package me.hilevi.musicmaker;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinMessage implements Listener {
	public OnJoinMessage(Main plugin){
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		event.getPlayer().sendMessage("Welcome " + event.getPlayer().getName() + "! Type /create to get your music creating tools!");
	}

}
