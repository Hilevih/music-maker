package me.hilevi.musicmaker;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		
		getCommand("create").setExecutor(new CommandCreateSong(this));		
		
		PluginManager pm = getServer().getPluginManager();
		
		OnJoinMessage joinListener = new OnJoinMessage(this);
		pm.registerEvents(joinListener, this);
		
		TargetBlockChecker targetListener = new TargetBlockChecker(this);
		pm.registerEvents(targetListener, this);
			
		SoundTypeChooserGui soundChooserListener = new SoundTypeChooserGui(this, null);
		pm.registerEvents(soundChooserListener, this);
			
		MiscGui miscGuiListener = new MiscGui(this, null);
		pm.registerEvents(miscGuiListener, this);
			
		PitchDisablerGui disablerGuiListener = new PitchDisablerGui(this, null);
		pm.registerEvents(disablerGuiListener, this);
		
		PitchChooserGui pitchChooserGuiListener = new PitchChooserGui(this, null);
		pm.registerEvents(pitchChooserGuiListener, this);
		
		InputGetter chatListener = new InputGetter(this);
		pm.registerEvents(chatListener, this);
		
	}
	
}
