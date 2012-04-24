package com.coversal.plugin.vlc;

import android.text.InputType;

import com.coversal.ucl.api.TextParameter;
import com.coversal.ucl.plugin.Browsable;
import com.coversal.ucl.plugin.Controller;
import com.coversal.ucl.plugin.PlaylistManager;
import com.coversal.ucl.plugin.Profile;
import com.coversal.ucl.plugin.ProfileAnnouncer;

public class Vlc extends Profile{
	
	
	static final String HOSTNAME = "Hostname";
	static final String PORT = "Port";
	private VlcBrowser browser;
	private VlcController controller;
	private VlcPlaylistManager playlist;

	

	public Vlc(ProfileAnnouncer pa) {
		super(pa);
	
		// Define required parameters for when a new SSH source is added 
		defineParameter(HOSTNAME, new TextParameter(null, true));
		defineParameter(PORT, new TextParameter("8080", true, InputType.TYPE_CLASS_NUMBER));
		
		browser = new VlcBrowser(this);
		controller = new VlcController(this);
		playlist = new VlcPlaylistManager(this);
	}

	@Override
	public String getIconName() {
		return "vlc";
	}

	@Override
	public String getProfileName() {
		return "VLC";
	}
	
	@Override
	public String getTargetNameField() {
		return HOSTNAME;
	}

	
	@Override
	public Browsable getBrowser() {
		return browser;
	}


	@Override
	public Controller getController() {
		return controller;
	}

	@Override
	public PlaylistManager getPlaylistManager() {
		return playlist;
	}
	
	@Override
	public boolean init() {
		if (browser.browse(null) == null) return false;
		else return true;
	}
	
	
	@Override
	public boolean isActive() {
		return false;
	}


	@Override
	public boolean isPasswordRequired() {
		return false;
	}

	@Override
	public void setPassword(String arg0) {		
	}
	

	@Override
	public void close() {		
	}

	@Override
	public void onConfigurationUpdate() {
	}
}
