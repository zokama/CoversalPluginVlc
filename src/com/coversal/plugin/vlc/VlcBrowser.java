package com.coversal.plugin.vlc;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.coversal.ucl.api.AdapterItem;
import com.coversal.ucl.plugin.Browsable;


public class VlcBrowser extends Browsable {
	
	// Global variables
	NodeList currentNode;
	String currentDir;
	private static final String HOME_STR = "~";
	
	Vlc profile;

	VlcBrowser(Vlc vlc) {
		super(vlc);
		profile = vlc;
		
		profile.setOptionValue(OPTION_HOME_DIR, HOME_STR);

	}

	
	
	String getPath (String item) {
		if (currentNode == null || item.equals(profile.getOptionValue(OPTION_HOME_DIR)))
			return  profile.getOptionValue(OPTION_HOME_DIR);
		
		for(int i=0; i<currentNode.getLength(); i++) {
			if(currentNode.item(i).getAttributes().getNamedItem("name").getNodeValue().equals(item))
				return currentNode.item(i).getAttributes().getNamedItem("path").getNodeValue();
		}
		
		return HOME_STR;
	}
	
	
	
	@Override
	public List<AdapterItem> browse(String dir) {
		ArrayList <AdapterItem> dirContent = new ArrayList <AdapterItem>(); 

		Log.d("Coversal VLC PLugin", "BEFORE Browsing: "+ profile.getOptionValue(OPTION_HOME_DIR)+" - "+dir);
		
		if (currentDir == null)
			currentDir = "http://" + profile.getValue(Vlc.HOSTNAME) 
					+ ":" + profile.getValue(Vlc.PORT) + "/requests/browse.xml";
		
		if (dir == null || dir.equals("")) dir = profile.getOptionValue(OPTION_HOME_DIR);
		
		Uri.Builder builder = Uri.parse(currentDir).buildUpon();
		builder.query("");
		builder.appendQueryParameter("dir", getPath(dir));
		currentDir = builder.build().toString();
		
		Log.d(profile.getOptionValue(OPTION_HOME_DIR)+" - "+dir+" - Coversal VLC PLugin", "Browsing: "+ currentDir);
		
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
	
			URL url = new URL(currentDir);
			InputStream stream = url.openStream();
			Document doc = docBuilder.parse(stream);
	
			// normalize text representation
			doc.getDocumentElement().normalize();
			currentNode = doc.getElementsByTagName("element");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		if (dir.equals(BACK_STR)) {
			Vlc.debug("BACK STRING DETECTED setting current dir to "+currentDir.replaceAll("(.*)%2F.+%2F\\.\\.$", "$1"));
			currentDir = currentDir.replaceAll("(.*)(%2F|%5C).+(%2F|%5C)\\.\\.$", "$1");
			if (currentDir.equals(""))
				currentDir = ROOT_STR;
		}
		
		//NodeList dirContent = doc.getElementsByTagName("element");
		for(int i=0; i<currentNode.getLength(); i++) {
			dirContent.add(new AdapterItem(-1, currentNode.item(i).getAttributes().
				getNamedItem("name").getNodeValue(), null, null, null));
		}
		
		standardSort(dirContent);
		
		return dirContent;
	}

	
	
	@Override
	public List<AdapterItem> browseBack() {
			return browse(BACK_STR);
	}

	
		
	@Override
	public Bitmap getCover(String item) {
		return null;
	}

	
	
	@Override
	public String getCurrentDir() {
		if (currentDir == null) return HOME_STR;
		return Uri.decode(currentDir.replaceAll("http.+browse\\.xml\\?dir=(.*)$", "$1"));
	}

	
	
	@Override
	public int getItemType(String item) {

		if (item.equals(BACK_STR)) return ITEM_TYPE_BACK;
		
		if (currentNode == null) return Vlc.ITEM_TYPE_UNKNOWN;
		
		for(int i=0; i<currentNode.getLength(); i++) {
			if(currentNode.item(i).getAttributes().getNamedItem("name").getNodeValue()
					.equals(item)) {
				
				String type =  currentNode.item(i).getAttributes().getNamedItem("type")
						.getNodeValue();
				
				if (type.contains("dir"))
					return Vlc.ITEM_TYPE_DIRECTORY;
				else if (currentNode.item(i).getAttributes().getNamedItem("type")
						.getNodeValue().equalsIgnoreCase("file"))
					return Vlc.ITEM_TYPE_MULTIMEDIA;

			}
		}
		
		return Vlc.ITEM_TYPE_UNKNOWN;
	}



	@Override
	public String getFullPath(String item) {
		for(int i=0; i<currentNode.getLength(); i++) {
			if(currentNode.item(i).getAttributes().getNamedItem("name").getNodeValue()
					.equals(item)) {
				
				Node n = currentNode.item(i).getAttributes().getNamedItem("uri");
				if (n != null)
					return n.getNodeValue(); //VLC v2.x
				else 
					return getCurrentDir()+"/"+item; //VLC v1.x
					
			}
		}
		
		return item;
	}

}
