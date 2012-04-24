package com.coversal.plugin.vlc;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.RemoteException;

import com.coversal.ucl.api.AdapterItem;
import com.coversal.ucl.plugin.PlaylistManager;



public class VlcPlaylistManager extends PlaylistManager {

	private Vlc vlc;
	private ArrayList <AdapterItem> playlist;
	private NodeList playlistNode;
	
	VlcPlaylistManager(Vlc profile) {
		vlc = profile;
		playlist = new ArrayList <AdapterItem>();
		playlistNode = null;
	}
	
	@Override
	public void add(String media) throws RemoteException {
		vlc.getController().execute("in_enqueue&input="+ Uri.encode(media));
	}

	@Override
	public void clear() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public Bitmap getCover(int arg0) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPlayingMediaIndex() throws RemoteException {
		if (playlist == null) return -1;
		
		// update playlist
		getPlaylistItems();
		
		for(int i=0; i<playlistNode.getLength(); i++) {

			if (playlistNode.item(i).getAttributes().getNamedItem("current") != null) {
				String currentId = playlistNode.item(i).getAttributes()
						.getNamedItem("id").getNodeValue();
				
				if (currentId != null) for (int j = 0; j<playlist.size(); j++)
					if (Long.valueOf(currentId) == playlist.get(j).id){
						return j;
					}
			}
		}
				
		return -1;
	}

	@Override
	public List<AdapterItem> getPlaylistItems() throws RemoteException {
		
		playlist.clear();

		String	request = "http://" + vlc.getValue(Vlc.HOSTNAME) 
					+ ":" + vlc.getValue(Vlc.PORT) + "/requests/playlist.xml";
			
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
	
			URL url = new URL(request);
			InputStream stream = url.openStream();
			Document doc = docBuilder.parse(stream);
	
			// normalize text representation
			doc.getDocumentElement().normalize();
			playlistNode = doc.getElementsByTagName("leaf");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		//NodeList dirContent = doc.getElementsByTagName("element");
		for(int i=0; i<playlistNode.getLength(); i++) {

			playlist.add(new AdapterItem(
					Integer.valueOf(playlistNode.item(i).getAttributes().getNamedItem("id").getNodeValue()), 
					playlistNode.item(i).getAttributes().getNamedItem("name").getNodeValue(),
					null, null, null));
		}
		
		return playlist;
	}

	@Override
	public void play(int index, long id) throws RemoteException {
		vlc.getController().execute("pl_play&id="+id);

	}

	@Override
	public void remove(int position, long id) throws RemoteException {
		vlc.getController().execute("pl_delete&id="+playlist.get(position).id);
	}

}
