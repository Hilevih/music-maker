package me.hilevi.musicmaker;

import org.bukkit.Sound;

// Represents a single note in the song

public class Note {
	private Sound sound; // Noteblock or other sound
	private int nofUses; // Used for pitch, 0-24
	private double volume; // Volume of note, 0-1
	
	
	public Note(Sound s, int n, double vol) { // Type of sound, 'pitch'/number of uses on noteblock (0-24), volume
		sound = s;
		nofUses = n;
		volume = vol;
	}
	
	public Sound getSound() {
		return sound;
	}
	
	public float getPitch() {
		float pitch = (float) Math.pow((double) 2, (double) (nofUses-12)/12);
		return pitch;
	}
	
	public float getVolume() {
		return (float) volume;
	}
	
	
	public int getNofUses() {
		return nofUses;
	}
	
	
	public void setNofUses(int n) {
		nofUses = n;
	}
	
	
	
	public void changeVolumeBy(double n) {
		volume += n;
		if (volume > 1.0) volume = 1.0;
		else if (volume < 0.0) volume = 0.0;
		volume = Math.round(10.0 * volume) / 10.0;
	}
	
}
