package me.hilevi.musicmaker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.Sound;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

// Creates a list out of an XML file, that can then be placed on the board

public class XMLToList {
	
	List<List<Note>> notes = new ArrayList<List<Note>>();
	int speed;
	String name;
	String filepath = "./plugins/songs/"; // CHANGE ME IN CASE ANOTHER ADDRESS IS WANTED
	
	public XMLToList(String name) {
		String parsedName = name.toLowerCase().replace(" ", "");
		filepath = filepath + parsedName + ".xml";
		parseXML();
	}
	
	public List<List<Note>> getNotes() {
		return notes;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	public String getName() {
		return name;
	}
	
	
	private void parseXML() {
		// Goes through the XML file, and adds wanted information in the notelist
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(filepath);
			int largestColumn = 0;
			int width = 0;
			
			NodeList songList = doc.getElementsByTagName("Song");
			for (int i = 0; i<songList.getLength(); i++) {
				Node s = songList.item(i);
				if (s.getNodeType() == Node.ELEMENT_NODE) {
					Element song = (Element) s;
					
					speed = Integer.parseInt(song.getAttribute("SongSpeed"));
					name = song.getAttribute("Name");
					
					NodeList columnList = song.getElementsByTagName("Column");
					for (int j = 0; j<columnList.getLength(); j++) {
						Node c = columnList.item(j);
						if (c.getNodeType() == Node.ELEMENT_NODE) {
							Element column = (Element) c;
							List<Note> notesInColumn = new ArrayList<Note>();
							
							NodeList noteList = column.getElementsByTagName("Note");
							for (int k=0; k<noteList.getLength(); k++) {
								Node n = noteList.item(k);
								if (n.getNodeType() == Node.ELEMENT_NODE) {
									
									Element note = (Element) n;
									Sound sound = Sound.valueOf(note.getAttribute("SoundType"));
									int pitch = Integer.parseInt(note.getAttribute("Pitch"));
									double volume = Double.parseDouble(note.getAttribute("Volume"));
									int delay = Integer.parseInt(note.getAttribute("Delay"));
									int row = Integer.parseInt(note.getAttribute("Row"));
									while (row > notesInColumn.size()) notesInColumn.add(null);
									notesInColumn.add(new Note(sound, pitch, volume));
									if (row > largestColumn) largestColumn = row;
									if (delay > width) width = delay;
								}
							}
							notes.add(notesInColumn);
							
						}
						
					}
				}
			}
			largestColumn += 1;
			evenList(largestColumn, width);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return;
			//e.printStackTrace();
		} 
		
	}
	
	private void evenList(int largestColumn, int width) {
		for (List<Note> column : notes) {
			while (column.size() < largestColumn) column.add(null); // Evens out the columns
		}
	}

	
	
	
	

}
