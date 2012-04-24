package com.coversal.plugin.vlc;

import com.coversal.ucl.plugin.ProfileAnnouncer;

public class VlcAnnouncer extends ProfileAnnouncer {

	public VlcAnnouncer() {
		defineProfile("VLC", Vlc.class);
	}

}
