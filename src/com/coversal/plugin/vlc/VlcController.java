package com.coversal.plugin.vlc;

import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.net.Uri;

import com.coversal.ucl.plugin.Controller;

public class VlcController extends Controller{

	String cmdBaseUri;
	Vlc profile;
    HttpClient httpclient;   

    private String playingMedia = "-";
    
	VlcController(Vlc vlc) {
		super();
		
		profile = vlc;
		httpclient = new DefaultHttpClient();
		
		defineCommand(START_PLAY, "in_play&input=", false);
		defineCommand(PLAY_PAUSE, "pl_pause", false);
		defineCommand(STOP, "pl_stop", false);
		defineCommand(VOL_UP, "volume&val=+15", false);
		defineCommand(VOL_DOWN, "volume&val=-15", false);
		defineCommand(NEXT, "pl_next", false);
		defineCommand(PREVIOUS, "pl_previous", false);
		defineCommand(FORWARD, "seek&val=+10", false);
		defineCommand(REWIND, "seek&val=-10", false);
		defineCommand(FULLSCREEN, "fullscreen", false);
		
		
		defineCommand(PROGRAM_UP, "key&val=chapter-prev", false);
		defineCommand(PROGRAM_DOWN, "key&val=chapter-next", false);
		defineCommand(OK, "key&val=nav-activate", false);
		defineCommand(UP, "key&val=nav-up", false);
		defineCommand(DOWN, "key&val=nav-down", false);
		defineCommand(LEFT, "key&val=nav-left", false);
		defineCommand(RIGHT, "key&val=nav-right", false);
		defineCommand("dvd_menu", "key&val=disc-menu", false);
		defineCommand("audio", "key&val=audio-track", false);
		defineCommand("subtitles", "key&val=subtitle-track", false);
		
		defineKey(CUSTOM1, "dvd_menu", false, "Menu");
		defineKey(CUSTOM2, "audio", false, "Audio");
		defineKey(CUSTOM3, "subtitles", false, "Subtitles");
	}
	
	
	
	@Override
	public boolean execute(String cmd) {
		//if (cmdBaseUri == null)
			cmdBaseUri = "http://" + profile.getValue(Vlc.HOSTNAME) 
					+ ":" + profile.getValue(Vlc.PORT) + "/requests/status.xml?command=";
		
		String post = cmdBaseUri;
		
		if (getCommand(cmd) != null)
			post += getCommand(cmd);
		else 
			// post += cmd.replaceAll("\\\\", "/");
			post += cmd.replaceAll("%5C", "%2F").replaceAll("'", "%5C'");
		
//		Log.d("Coversal VLC PLugin", "CMD: "+ post);
		    // Create a new HttpClient and Post Header   
		    HttpPost httppost = new HttpPost(post); 
		    
	        try {
	        	InputStream is = httpclient.execute(httppost).getEntity().getContent();
	        	if (is != null) {
	        		// consume reponse otherwise we get warnings
	        		//while(is.read() > 0);
	        		readStatus(is);
	        		is.close();
	        	}
			    
			} catch (Exception e) {
				e.printStackTrace();
			}  
		return false;
	}

	
	
	private void readStatus(InputStream is) {
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(is);
			
			// normalize text representation
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("title");
			
			if (nodes != null && nodes.getLength() > 0 && nodes.item(0).getFirstChild() != null)
				playingMedia = nodes.item(0).getFirstChild()
				.getNodeValue().replaceAll(".*/(.+)$", "$1");
			else 
				playingMedia = "-";
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public List<String> getDeviceList() {
		// Not supported yet
		return null;
	}

	
	
	@Override
	public String getLayoutName() {
		return "sshmote";
	}


	@Override
	public int getMediaDuration() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
	@Override
	public int getMediaPosition() {
		// position
		return 0;
	}

	
	
	@Override
	public String getPlayingMedia() {
		return playingMedia;
	}

	@Override
	public boolean isPlaying() {
		execute("");
		
		if (playingMedia == null || playingMedia.equals("") 
				|| playingMedia.equals("-") ) return false;
		else return true;
	}



	@Override
	public boolean isAdvancedController() {
		return true;
	}



	@Override
	public List<String> getContextMenuItems(int type) {
		return null;
	}



	@Override
	public void onItemSelected(String action, String media){
		playingMedia = media.replaceAll(".*/(.+)$", "$1");
		execute(getCommand(Vlc.START_PLAY)+ Uri.encode(media));
	}
	

}
