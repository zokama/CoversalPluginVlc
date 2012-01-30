package com.coversal.plugin.vlc;

import android.os.RemoteException;
import android.text.InputType;

import com.coversal.ucl.api.TextParameter;
import com.coversal.ucl.plugin.Browsable;
import com.coversal.ucl.plugin.Controller;
import com.coversal.ucl.plugin.PluginAnnouncer;
import com.coversal.ucl.plugin.Profile;

public class Vlc extends Profile{
	
	
	static final String HOSTNAME = "Hostname";
	static final String PORT = "Port";
	private VlcBrowser browser;
	private VlcController controller;

	

	public Vlc(PluginAnnouncer pa) {
		super(pa);
	
		// Define required parameters for when a new SSH source is added 
		defineParameter(HOSTNAME, new TextParameter(null, true));
		defineParameter(PORT, new TextParameter("8080", true, InputType.TYPE_CLASS_NUMBER));
		
		browser = new VlcBrowser(this);
		controller = new VlcController(this);
		

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
	public boolean init() {
		if (browser.browse(null) == null) return false;
		else return true;
	}
	
	
	@Override
	public boolean isActive() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isPasswordRequired() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPassword(String arg0) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void close() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConfigurationUpdate() {
	}

}
