package me.hilevi.musicmaker;

import java.io.File;
import java.util.List;












import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

// Creates and XML file from the given notelist

public class ListToXML {
	List<List<Note>> notes;
	int speed;
	String name;
	String filepath = "./plugins/songs/"; // CHANGE ME IN CASE ANOTHER FILE IS WANTED
	
	public ListToXML(List<List<Note>> n, int s, String songName) {
		notes = n;
		speed = s;
		name = songName;
		
		String filename = name.toLowerCase().replace(" ", "");
		filepath = filepath + filename + ".xml";
		
		createXML();
	}
	
	
	
	
	private void createXML() {
		// Creates a new XML based on given notes
		try {
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document document = builder.newDocument();
			
			Element root = document.createElement("Song");
			document.appendChild(root);
			
			Attr songName = document.createAttribute("Name");
			songName.setValue(name);
			root.setAttributeNode(songName);
			
			
			Attr songSpeed = document.createAttribute("SongSpeed");
			songSpeed.setValue(String.valueOf(speed));
			root.setAttributeNode(songSpeed);
			
			Element songNotes = document.createElement("Notes");
			for (int i = 0; i < notes.size(); i++) {
				List<Note> c = notes.get(i);
				Element column = document.createElement("Column");
				for (int j = 0; j < c.size(); j++) {
					Note n = c.get(j);
					if (n != null) {
						Element note = document.createElement("Note");
						
						Attr pitch = document.createAttribute("Pitch");
						pitch.setValue(String.valueOf(n.getNofUses()));
						note.setAttributeNode(pitch);
						
						Attr delay = document.createAttribute("Delay");
						delay.setValue(String.valueOf(i));
						note.setAttributeNode(delay);
						
						Attr sound = document.createAttribute("SoundType");
						sound.setValue(n.getSound().name());
						note.setAttributeNode(sound);
						
						Attr volume = document.createAttribute("Volume");
						volume.setValue(String.valueOf(n.getVolume()));
						note.setAttributeNode(volume);
						
						Attr row = document.createAttribute("Row");
						row.setValue(String.valueOf(j));
						note.setAttributeNode(row);
						
						column.appendChild(note);
					}
				}
				songNotes.appendChild(column);
			}
			
			root.appendChild(songNotes);
			
			
			TransformerFactory transformerFact = TransformerFactory.newInstance();
			Transformer transformer = transformerFact.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(filepath));
			
			transformer.transform(domSource, streamResult);
			
		} catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
	}
}
