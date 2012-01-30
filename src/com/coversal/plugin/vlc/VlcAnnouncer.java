package com.coversal.plugin.vlc;

import com.coversal.ucl.plugin.PluginAnnouncer;

public class VlcAnnouncer extends PluginAnnouncer {

	public VlcAnnouncer() {
		defineProfile("VLC", Vlc.class);
	}

}
