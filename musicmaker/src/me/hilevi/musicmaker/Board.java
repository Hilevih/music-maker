package me.hilevi.musicmaker;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;






import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;



public class Board {
	
	private List<List<Note>> notes = new ArrayList<List<Note>>();
	private List<List<Note>> selectedColumns = new ArrayList<List<Note>>();
	private List<Integer> blockedPitches = new ArrayList<Integer>();
	
	
	/* List of possible sound effects
	 * 
	 * NOTE: In order to add more effects, add it to the map below like others,
	 * put(Sound.WHATEVER_THE_SOUND_YOU_WANT_IS, Material.WHATEVER_MATERIAL);
	 */
	@SuppressWarnings("serial")
	private final Map<Sound, Material> blockBySound = new HashMap<Sound, Material>()
	{
		{
			put(Sound.BLOCK_NOTE_BASEDRUM,Material.STONE);
			put(Sound.BLOCK_NOTE_BASS, Material.WOOD);
			put(Sound.BLOCK_NOTE_BELL, Material.GOLD_BLOCK);
			put(Sound.BLOCK_NOTE_CHIME, Material.PACKED_ICE);
			put(Sound.BLOCK_NOTE_FLUTE, Material.CLAY);
			put(Sound.BLOCK_NOTE_GUITAR, Material.WOOL);
			put(Sound.BLOCK_NOTE_HARP, Material.DIRT);
			put(Sound.BLOCK_NOTE_HAT, Material.GLASS);
			put(Sound.BLOCK_NOTE_PLING, Material.GLOWSTONE);
			put(Sound.BLOCK_NOTE_SNARE, Material.SAND);
			put(Sound.BLOCK_NOTE_XYLOPHONE, Material.BONE_BLOCK);
			
			put(Sound.ENTITY_LIGHTNING_THUNDER, Material.BIRCH_FENCE);
		}
	};

	
	
	@SuppressWarnings("serial")
	private final Map<Integer, String> pitches = new HashMap<Integer, String>()
	{
		{
			put(0, "F# / Gb");
			put(1, "G");
			put(2, "G# / Ab");
			put(3, "A");
			put(4, "A# / Bb");
			put(5, "B");
			put(6, "C");
			put(7, "C# / Db");
			put(8, "D");
			put(9, "D# / Eb");
			put(10, "E");
			put(11, "F");
			put(12, "F# / Gb");
			put(13, "G");
			put(14, "G# / Ab");
			put(15, "A");
			put(16, "A# / Bb");
			put(17, "B");
			put(18, "C");
			put(19, "C# / Db");
			put(20, "D");
			put(21, "D# / Eb");
			put(22, "E");
			put(23, "F");
			put(24, "F# / Gb");
			
		}
	};
	
	private World world;
	private Main plugin;
	private int width = 0;
	private int height = 0;
	
	private Sound selectedSound = Sound.BLOCK_NOTE_HARP;
	private int selectedPitch;
	private double selectedVolume;
	private int speed;
	private int notesInMeasure;
	
	BukkitRunnable task = null; // Once is played, creates this task
	
	public Board(Main p, World w) {
		world = w;
		plugin = p;
		initBoard();
	}

	
	/* =======================
	 * = Getters and setters =
	 * =======================
	 */
	
	public int getWidth(){
		return width;
	}
	
	public int getHeight(){
		return height;
	}

	public String getPitchByInt(int i) {
		return pitches.get(i);
	}
	
	public Map<Sound, Material> getSounds(){
		return blockBySound;
	}
	
	public String getSelectedSound() {
		return selectedSound.name();
	}
	
	public int getSelectedPitch() {
		return selectedPitch;
	}
	
	public double getSelectedVolume(){
		return selectedVolume;
	}
	
	public void setSelectedSound(Sound s) {
		selectedSound = s;
	}
	
	public void setSelectedPitch(Integer i) {
		selectedPitch = i;
	}
	
	
	
	// Helper methods for coordinates and list management
	
	
	private Location getTopLeft(Location loc) {
		// Gets the top left corner of the 3x3 area the clicked location is in.
		 
		double x = loc.getX();
		double y = loc.getY();
		
		x = x-x%3;
		y = y+(250-y)%3;
		
		return new Location(world, x, y, 0);
	}
	
	private int columnNum(Location loc) {
		// Gets the column the clicked block is in, in sense of location on the note list
		int x = (int) getTopLeft(loc).getX();
		return x/3-1;
	}
	
	private int rowNum(Location loc) {
		// Gets the row the clicked block is in, in sense of location on the note list
		int y = (int) getTopLeft(loc).getY();
		return (250-y) / 3 - 1;
	}
	
	private int yLocByRow(int row) {
		// Gets the y-coordinate of the given row number, row number being that of the note list
		return 247-3*row;
	}

	
	
	/*	=============================
	 *  = Methods for adding things =
	 *  =============================
	 */

	public void firstAdder(Block block, Player player, boolean isLeftClick, boolean isSneaking) {
		/* Called when shears are clicked
		 * Determines what kind of adding / copying is being done and calls the appropriate method
		 */
		Location loc = block.getLocation();
		// Column copying / adding (Those of the selected columns)
		if (isSneaking && block.getType().equals(Material.DIAMOND_BLOCK)) {
			if (isLeftClick) {
				List<Note> copiedColumn = new ArrayList<Note>();
				for (Note n : notes.get(columnNum(loc))) {
					if (n == null) copiedColumn.add(null);
					else copiedColumn.add(new Note(n.getSound(), n.getNofUses(), n.getVolume()));
				}
				selectedColumns.add(copiedColumn);
			} else {
				addSelectedColumns(loc);
			}
		}
		// Note copying
		else if (isLeftClick) {
			if (isNote(loc) && block.getType() != Material.BEDROCK){
				Note note = getNoteAt(loc);
				selectedSound = note.getSound();
				selectedPitch = note.getNofUses();
				selectedVolume = note.getVolume();
				selectedVolume = Math.round(10.0 * selectedVolume) / 10.0; // Keeps the volume on one decimal accuracy
				playSelectedSound(player);
			}
		}
		// Other adding
		else add(block, player);
	}
	
	private void add(Block block, Player player) {
		// Decides the exact adding that happens based on what kind of block is clicked
		Material mat = block.getType();
		if (mat.equals(Material.DIAMOND_BLOCK)) {
			addColumn(block.getLocation());
		} else if (mat.equals(Material.EMERALD_BLOCK)) {
			addRow(block.getLocation());
		} else if (mat.equals(Material.BEDROCK)) {
			addNote(block.getLocation(), player);
		} else if (mat.equals(Material.REDSTONE_BLOCK)) {
			addColumn(block.getLocation());
			addRow(block.getLocation());
		} else if (mat.equals(Material.CONCRETE)) {
			// Do nothing
		} else {
			playSoundAt(block.getLocation(), player);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void addColumn(Location loc) {
		// Adds a column to the right of the given location
		
		int startingPos = (int) getTopLeft(loc).getX()+4;
		int column = columnNum(loc) + 1;
		
		// Move everything after 3 to the right
		for (int i=0;i<width-startingPos+1;i++) {
			for (int j=0;j<height;j++) {
				Block block = world.getBlockAt(width-i, 250-j, 0);
				world.getBlockAt(width-i+3, 250-j, 0).setType(block.getType());
				if (block.getType().equals(Material.CONCRETE)) world.getBlockAt(width-i+3, 250-j, 0).setData(getConcreteData(block.getX()+3));
			}
		}
		
		// Add empty row after the selected part
		for (int i=startingPos;i<startingPos+3;i++) {
			for (int j=0;j<height; j++) {
				addDefaultBlock(i,j);
			}
		}
		
		// Adds the new column in the list at the wanted position
		List<Note> newColumn = new ArrayList<Note>();
		for (int i=0;i<height;i++) newColumn.add(null);
		notes.add(column, newColumn);
		
		width += 3;
		
	}
	
	@SuppressWarnings("deprecation")
	private void addRow(Location loc) {
		// Adds a row under the given location
		int startingPos = (int) getTopLeft(loc).getY()-4;
		int row = rowNum(loc);
		
		// Move everything after 3 below
		for (int i=0;i<width;i++) {
			for (int j=250-height+1;j<startingPos+1;j++) {
				Block block = world.getBlockAt(width-i-1, j, 0);
				world.getBlockAt(width-i-1, j-3, 0).setType(block.getType());
				if (block.getType().equals(Material.CONCRETE)) world.getBlockAt(width-i-1, j-3, 0).setData(getConcreteData(block.getX()));
			}
		}
		
		// Add empty row after the selected part
		for (int i=0;i<width;i++) {
			for (int j=startingPos; j>startingPos-3; j--) {
				addDefaultBlock(i,250-j);
			}
		}
		
		// Add empty note to the list, at correct part
		for (List<Note> list : notes) {
			if (list.size() == row+1) list.add(null);
			else list.add(row+1, null);
		}
		
		height += 3;
	}
	
	private void addNote(Location loc, Player player) {
		// Adds a note on the given location
		
		Material soundMat = blockBySound.get(selectedSound);
		changeInArea(soundMat, loc); // Updates ingame board
		
		int c = columnNum(loc);
		int r = rowNum(loc);
		
		Note note = new Note(selectedSound, selectedPitch, selectedVolume);
		notes.get(c).set(r, note); // Updates the list
		playSoundAt(loc, player);
	}
	
	private void addSpecificNote(Location loc, Note note, int row) {
		// Helper method for adding selected rows. Creates a copy of the note and adds it as normal
		if (note != null) {
			int x = loc.getBlockX()+3; // Note to be added will be one column to the right
			int y = row;
			int c = columnNum(loc) + 1;
			int r = y;
			Location noteLoc = new Location(world, x, yLocByRow(y), 0);
			
			Material soundMat = blockBySound.get(note.getSound());
			changeInArea(soundMat, noteLoc);
			Note n = new Note(note.getSound(), note.getNofUses(), note.getVolume());
			notes.get(c).set(r, n);
		}
	
	}

	
	private void addSelectedColumns(Location loc) {
		// Goes through all selected columns and adds them and the notes one by one
		for (int i = selectedColumns.size()-1; i >= 0; i--) {
			addColumn(loc);
			for (int j = 0; j < selectedColumns.get(i).size(); j++) {
				Note n = selectedColumns.get(i).get(j);
				addSpecificNote(loc, n, j);
			}
		}
	}
	
	
	/* ===============================
	 * = Methods for removing things =
	 * ===============================
	 */
	
	public void remove(Block block) {
		// Main method for removing. Is called whenever skull is clicked, then calls appropriate method based on block clicked
		Material mat = block.getType();
		if (mat.equals(Material.DIAMOND_BLOCK)) {
			removeColumn(block.getLocation());
		} else if (mat.equals(Material.EMERALD_BLOCK)) {
			removeRow(block.getLocation());
		} else if (mat.equals(Material.BEDROCK)) {
			// Do nothing
		} else if (mat.equals(Material.REDSTONE_BLOCK)) {
			// Do nothing
		} else if (mat.equals(Material.CONCRETE)) {
			// Do nothing
		} else {
			removeNote(block.getLocation());
		}
	}
	
	
	private void removeNote(Location location) {
		// Removes note in given location
		changeInArea(Material.BEDROCK, location);
		
		int c = columnNum(location);
		int r = rowNum(location);
		
		notes.get(c).set(r, null);
		
	}


	@SuppressWarnings("deprecation")
	private void removeRow(Location loc) {
		// Removes row in given location
		int startingPos = (int) getTopLeft(loc).getY()-1;
		int row = rowNum(loc);
		
		// Move everything after 3 up
		for (int i=0;i<width;i++) {
			for (int j=startingPos+1;j>250-height+1;j--) {
				Block block = world.getBlockAt(width-i-1, j-3, 0);
				world.getBlockAt(width-i-1, j, 0).setType(block.getType());
				if (block.getType().equals(Material.CONCRETE)) world.getBlockAt(width-i-1, j, 0).setData(getConcreteData(block.getX()));
			}
		}
		
		// Remove last row
		for (int i=0;i<width;i++) {
			for (int j=250-height+1; j<250-height+4; j++) {
				removeBlock(i,j);
			}
		}
		
		// Add empty note to the list, at correct part
		for (List<Note> list : notes) {
			list.remove(row);
		}
		
		
		height -= 3;
		
	}


	@SuppressWarnings("deprecation")
	private void removeColumn(Location loc) {
		// Removes column in given location
		int startingPos = (int) getTopLeft(loc).getX()+1;
		int column = columnNum(loc);
		
		// Move everything after 3 to the left
		for (int i=0;i<width-startingPos+1;i++) {
			for (int j=0;j<height;j++) {
				Block block = world.getBlockAt(startingPos+i+3, 250-j, 0);
				world.getBlockAt(startingPos+i, 250-j, 0).setType(block.getType());
				if (block.getType().equals(Material.CONCRETE)) world.getBlockAt(startingPos+i, 250-j, 0).setData(getConcreteData(block.getX()-3));
			}
		}
		
		// Remove last column
		for (int i=width-3;i<width;i++) {
			for (int j=0;j<height; j++) {
				removeBlock(i,250-j);
			}
		}
		
		// Removes the wanted column
		notes.remove(column);
		
		width -= 3;
	}


	private void removeBlock(int i, int j) {
		// Helper method, removes block in given location
		world.getBlockAt(i, j, 0).setType(Material.AIR);
		
	}
	
	
	
	/* ==============================
	 * = Methods for changing pitch =
	 * ==============================
	 */
	
	public void changePitch(Block block, Player player, boolean isLeftClick) {
		// Main method for changing pitch. Gets called when ladder is clicked
		if (block.getType() == Material.EMERALD_BLOCK) changeRowPitch(block.getLocation(), isLeftClick);
		else if (block.getType() == Material.DIAMOND_BLOCK) changeColumnPitch(block.getLocation(), isLeftClick);
		else if (block.getType() == Material.CONCRETE); //Do nothing
		else changeNotePitch(block.getLocation(), player, isLeftClick);
	}
	
	private void changeRowPitch(Location loc, boolean isLeftClick) {
		// Changes pitch of the whole row
		int row = rowNum(loc);
		for (List<Note> c : notes) {
			Note n = c.get(row);
			if (n != null) {
				if (isLeftClick) n.setNofUses(getNextPitch(n.getNofUses(), -1));
				else n.setNofUses(getNextPitch(n.getNofUses(), 1));
			}
		}
	}
	
	private void changeColumnPitch(Location loc, boolean isLeftClick) {
		// Changes pitch of the whole column
		int col = columnNum(loc);
		for (Note n : notes.get(col)) {
			if (n != null) {
				if (isLeftClick) n.setNofUses(getNextPitch(n.getNofUses(), -1));
				else n.setNofUses(getNextPitch(n.getNofUses(), 1));
			}
		}
	}
	
	private void changeNotePitch(Location loc, Player player, boolean isLeftClick) {
		// Changes pitch of a single note
		if (isNote(loc)) {
			Note note = getNoteAt(loc);
			if (isLeftClick) note.setNofUses(getNextPitch(note.getNofUses(), -1));
			else note.setNofUses(getNextPitch(note.getNofUses(), 1));
			playSoundAt(loc, player);
		}
	}
	
	public void changeSelectedPitch(boolean isLeftClick, boolean isSneaking, Player player) {
		// Main method for changing selected pitch (one note will have when it's added)
		// Sneaking opens a GUI, left clicking decreases by one, right clicking increases by one
		if (isSneaking) {
			PitchChooserGui gui = new PitchChooserGui(plugin, this);
			gui.openInventory(player);
		} else {
			if (isLeftClick) selectedPitch = getNextPitch(selectedPitch, -1);
			else selectedPitch = getNextPitch(selectedPitch, 1);
			playSelectedSound(player);
		}
		
	}
	
	private int getNextPitch(int p, int n) {
		// Helper for getting the next pitch / number of uses. Makes sure it's between 0-24
		// Going full round takes back to the other end
		int pitch = p;
		pitch += n;
		if (pitch>24) pitch -= 25;
		else if (pitch<0) pitch += 25;
		while (blockedPitches.contains(pitch)) {
			pitch += n;
			if (pitch>24) pitch -= 25;
			else if (pitch<0) pitch += 25;
		}
		return pitch;
	}
	
	
	/* ===============================
	 * = Methods for changing volume =
	 * ===============================
	 */
	
	public void changeVolume(Block block, Player player, boolean isLeftClick) {
		// Main method for changing volume, called when TNT is clicked
		if (block.getType() == Material.EMERALD_BLOCK) changeRowVolume(block.getLocation(), isLeftClick);
		else if (block.getType() == Material.DIAMOND_BLOCK) changeColumnVolume(block.getLocation(), isLeftClick);
		else if (block.getType() == Material.CONCRETE); //Do nothing
		else changeNoteVolume(block.getLocation(), player, isLeftClick);
	}
	
	private void changeRowVolume(Location loc, boolean isLeftClick) {
		// Changes volume of the whole row
		int row = rowNum(loc);
		for (List<Note> c : notes) {
			Note n = c.get(row);
			if (n != null) {
				if (isLeftClick) n.changeVolumeBy(-0.1);
				else n.changeVolumeBy(0.1);
			}
		}
	}
	
	private void changeColumnVolume(Location loc, boolean isLeftClick) {
		// Changes volume of the whole column
		int col = columnNum(loc);
		for (Note n : notes.get(col)) {
			if (n != null) {
				if (isLeftClick) n.changeVolumeBy(-0.1);
				else n.changeVolumeBy(0.1);
			}
		}
	}
	
	public void changeNoteVolume(Location loc, Player player, boolean isLeftClick) {
		// Changes volume of a single note
		if (isNote(loc)) {
			Note note = getNoteAt(loc);
			if (isLeftClick) note.changeVolumeBy(-0.1);
			else note.changeVolumeBy(0.1);
			playSoundAt(loc, player);
		}
	}
	
	
	public void changeSelectedVolume(Player player, boolean isLeftClick){
		// Main method for changing selected volume. Called when feather is clicked
		// Volume must be between 0-1 (lower would cause it to be same as 0, higher same as 1)
		if (isLeftClick) selectedVolume -= (double) 0.1;
		else selectedVolume += (double) 0.1;
		if (selectedVolume < 0) selectedVolume = 0.0;
		else if (selectedVolume > 1) selectedVolume = 1.0;
		selectedVolume = Math.round(10.0 * selectedVolume) / 10.0; // Keeps the volume on one decimal accuracy
		playSelectedSound(player);
	}
	
	
	/* =================
	 * = Sound chooser =
	 * =================
	 */
	public void chooseSound(Player player) {
		// Gets called when noteblock is clicked
		SoundTypeChooserGui gui = new SoundTypeChooserGui(plugin, this);
		gui.openInventory(player);
	}

	
		
	
	/* ==========================
	 * = Other settings methods =
	 * ==========================
	 */
	
	public void openSettings(Player player) {
		// Called when redstone comparator is clicked. Opens a new GUI for other settings and methods
		MiscGui gui = new MiscGui(plugin, this);
		gui.openInventory(player);
	}
	
	public void changeSpeed(Player player, boolean isLeftClick) {
		// Changes speed of the song. Smaller the speed, faster the song
		if (isLeftClick) speed += 1;
		else speed -= 1;
		if (speed < 0) speed = 0;
		player.sendMessage("Speed is now " + speed);
	}
	
	public void reset() {
		// Resets the board and removes all notes in it.
		// NOTE: Only works if the board is currently active, if it's been left won't remove all blocks
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				world.getBlockAt(i, 250-j, 0).setType(Material.AIR);
			}
		}
		
		notes.removeAll(notes);
		initBoard();
		
	}
	
	public void save(Player p) {
		// Saves the current song as an XML file. Requires chat input from user before saving
		if (notes.size() == 0) { // Only happens if every row / column has been fully removed
			p.sendMessage("No notes, cannot save");
			return;
		}
		p.sendMessage("Enter songname");
		InputGetter in = new InputGetter(plugin);
		String input = in.getInput(p);
		new ListToXML(notes, speed, input);
		p.sendMessage("Song saved: " + input);
	}
	
	public void load(Player p) {
		// "Loads" the notes from XML file if given name exists
		p.sendMessage("Enter songname");
		InputGetter in = new InputGetter(plugin);
		String input = in.getInput(p);
		XMLToList xml = new XMLToList(input);
		if (xml.getNotes().size() == 0) {
			p.sendMessage("File does not exist");
			return;
		}
		notes = xml.getNotes();
		speed = xml.getSpeed();
		
		setBoard();
		
		p.sendMessage("Song loaded: " + input);
	}
	
	private void setBoard() {
		// Helper method for load-method. Sets up the visual board based on set notes
		width = notes.size();
		if (width != 0) height = notes.get(0).size();
		else height = 0;
		
		width = width*3+4; // At first width is only amount of columns in notes, needs to be adjusted to board
		height = height*3+4; // Same as for width, only with amount of rows
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				addDefaultBlock(i,j);
				Location loc = new Location(world, i, 250-j, 0);
				
				if (isNote(loc)) {
					
					Block block = world.getBlockAt(loc);
					if (block.getType() == Material.BEDROCK) {
						Material mat = blockBySound.get(getNoteAt(loc).getSound());
						block.setType(mat);
					}
				} 
			}
		}
	}
	
	public void changeNotesInMeasureAmount(Player p, boolean isLeftClick) {
		// Changes the amount of columns a measure will have. Will appear as having different color concrete
		if (isLeftClick) notesInMeasure -= 1;
		else notesInMeasure += 1;
		if (notesInMeasure < 1) notesInMeasure = 1; // Can't be lower than 1
		p.sendMessage("Amount of notes now " + notesInMeasure);
		updateConcrete();
	}
	
	@SuppressWarnings("deprecation")
	public void updateConcrete() {
		// Sets each concrete piece to the correct color
		for (int i = 0; i<width; i++) {
			for (int j = 250-height; j<251; j++) {
				Block block = world.getBlockAt(i, j, 0);
				if (block.getType() == Material.CONCRETE) block.setData(getConcreteData(i));
			}
		}
	}
	
	public void sparseNotes() {
		// Creates an empty row between each column
		for (int i = notes.size() - 1; i >= 0; i--) addColumn(new Location(world, i*3+3, 240, 0));
	}
	
	/* ==============
	 * = Songplayer methods =
	 * ==============
	 */
	
	public void playSong(Block block, Player p, boolean isLeftClick, boolean isSneaking) {
		// Gets called when jukebox is clicked
		if (task != null) { // Stops the ongoing task, if there's one
			task.cancel();
			updateConcrete();
		}
		if (isSneaking) return; // Stops playing the song
		if (isLeftClick) { // Plays song from specific column
			int column = columnNum(block.getLocation());
			playSongFrom(column, p);
		} else playSongFrom(0, p);
	}
	
	private void playSongFrom(int i, Player p) {
		// Plays song starting from the given column number i
		if (i < 0) return;
		PlaySong song = new PlaySong(notes, speed, i, p, plugin, this);
		task = song.getTask();
	}
	
	@SuppressWarnings("deprecation")
	public void updatePlayingColumn(int i) {
		// Changes the color of the concrete on top of column that's playing to green, as well as previous to normal
		int x = i * 3 + 4;
		int y = 250;
		// Previous column
		world.getBlockAt(new Location(world, x-3, y, 0)).setData(getConcreteData(x-3));
		world.getBlockAt(new Location(world, x-2, y, 0)).setData(getConcreteData(x-2));
		// Current column
		world.getBlockAt(new Location(world, x, y, 0)).setData((byte) 5);
		world.getBlockAt(new Location(world, x+1, y, 0)).setData((byte) 5);
	}
	
	
	/* =================
	 * = Other methods =
	 * =================
	 */
	
	private Note getNoteAt(Location loc) {
		// Returns the note at the given location
		int c = columnNum(loc);
		int r = rowNum(loc);
		return notes.get(c).get(r);
	}
	
	private boolean isNote(Location loc) {
		// Checks if the note in given location is actually a note
		int c = columnNum(loc);
		int r = rowNum(loc);
		
		if (c >= notes.size()) return false;
		else if (c < 0) return false;
		else if (r >= notes.get(0).size()) return false;
		else if (r < 0) return false;
		Note note = notes.get(c).get(r);
		if (note != null) {
			return true;
		} else return false;
	}
	
	private void playSoundAt(Location loc, Player player) {
		// Plays the sound at given location, if it has a note
		if (isNote(loc)) {
			int c = columnNum(loc);
			int r = rowNum(loc);
			Note note = notes.get(c).get(r);
			player.playSound(player.getLocation(), note.getSound(), note.getVolume(), note.getPitch());
			
			String str = "§3On board: " + note.getNofUses() + " " + getPitchByInt(note.getNofUses()) + " " + note.getSound().name() + " " + note.getVolume();
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(str));
		}
	}
	
	public void playSelectedSound(Player player) {
		// Plays the sound with the selected settings, as in what a note that would be added sounds like
		player.playSound(player.getLocation(), selectedSound, (float) selectedVolume, (float) Math.pow((double) 2, (double) (selectedPitch-12)/12));
		String str = "§dSelected: " + getSelectedPitch() + " " + getPitchByInt(getSelectedPitch()) + " " + getSelectedSound() + " " + getSelectedVolume();
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(str));
	}
	
	private void changeInArea(Material newMat, Location loc) {
		// Changes an area of blocks into a new one. Used when adding / removing notes
		Location location = getTopLeft(loc);
		for (int i=1;i<3;i++) {
			for (int j=1;j<3;j++) {
				world.getBlockAt(new Location(world, location.getX()+i, location.getY()-j, 0)).setType(newMat);
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	private void addDefaultBlock(int i, int j) {
		// Adds a default block to the given location
		// These include row and column headers, concrete and empty notes
		if (i%3 == 0 || j%3 == 0) {
			Block block = world.getBlockAt(new Location(world, i, 250-j, 0));
			block.setType(Material.CONCRETE);
			block.setData(getConcreteData(block.getX()));
		} else if (i < 3 && j < 3) world.getBlockAt(new Location(world, i, 250-j, 0)).setType(Material.REDSTONE_BLOCK);
		else if (j < 3) {
			world.getBlockAt(new Location(world, i, 250-j, 0)).setType(Material.DIAMOND_BLOCK);
		}
		else if (i < 3) {
			world.getBlockAt(new Location(world, i, 250-j, 0)).setType(Material.EMERALD_BLOCK);
		}
		else world.getBlockAt(new Location(world, i, 250-j, 0)).setType(Material.BEDROCK);
	}
	
	private byte getConcreteData(Integer x) {
		// Gets the color the concrete is supposed to have in given x-coordinate
		byte data = 0;
		if ((x - 3) % (notesInMeasure*2*3) < 3 * notesInMeasure) data = 7;
		else data = 8;
		
		return data;
	}
	
	private void initBoard() {
		// Creates initial board
		
		height = 10; //starting height
		width = 52; //starting width
		
		selectedPitch = 0;
		selectedVolume = 0.7;
		speed = 4;
		notesInMeasure = 8;
		
		
		for (int i=0; i<16; i++) {
			notes.add(new ArrayList<Note>());
			for (int j=0; j<2; j++) {
				notes.get(i).add(null); // Should add null to each position where there's bedrock (or the markers)
			}
		}
		
		for (int i=0; i<width; i++) {
			for (int j=0; j<height; j++) {
				addDefaultBlock(i,j);
			}
		}	
	}
	
	public void clearSelectedColumns() {
		// Removes the copied columns so they won't be added anymore
		selectedColumns.removeAll(selectedColumns);
	}
	
	public boolean isDisabled(int i) {
		// Checks if note has been disabled by the user
		if (blockedPitches.contains(i)) return true;
		else return false;
	}

	public void toggleDisabledPitch(int posClicked) {
		// Changes the status of the pitch. If it has been disabled enables it, and the other way around
		if (blockedPitches.contains(posClicked)) {
			while (posClicked < 25) {
				blockedPitches.remove(blockedPitches.indexOf(posClicked));
				posClicked += 12;
			}
		}
		else {
			while (posClicked < 25) {
				blockedPitches.add(posClicked);
				posClicked += 12;
			}
		}
		
	}
}
