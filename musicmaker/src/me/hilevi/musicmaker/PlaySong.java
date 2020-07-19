package me.hilevi.musicmaker;

import java.util.List;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

// Class for playing the wanted song

public class PlaySong {
	int songLength;
	BukkitRunnable myTask;
	int runner = 0; // Position where song is playing at the time
	
	public PlaySong(List<List<Note>> notes, int speed, int startingPos, Player player, Main p, Board board) {
		runner = startingPos;
		
		if (notes.size() > 0) // If song does not exists, does not run the rest. Should always go in the loop.
		{
			myTask = new BukkitRunnable() {
				@Override
				public void run() {
					if (notes.size() > runner) {
						board.updatePlayingColumn(runner);
						for (Note note : notes.get(runner)) {
							if (note != null) {
								float pitch = note.getPitch();
								Sound instrument = note.getSound();
								float volume = note.getVolume();
								
								player.playSound(player.getLocation(), instrument, volume, pitch);
							}
						}
						runner++;
					} else {
						board.updateConcrete();
						cancel();
					}
					
				}
			};
			

			myTask.runTaskTimer(p, 0, speed);
			
			songLength = notes.size() * speed + 20; //Will be the last delay finally, effectively the last note of the song
			
		}
	}
	
	public BukkitRunnable getTask() {
		return myTask;
	}
}
