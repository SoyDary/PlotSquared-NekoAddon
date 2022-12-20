package com.github.SoyDary.PlotSquaredNekoAddon.Listeners;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.events.PlayerEnterPlotEvent;

public class PlotsListener {
	
	PSNA plugin;
	
	public PlotsListener(PSNA plugin) {
		this.plugin = plugin;
	}
	
	@Subscribe
	public void onPlayerEnterPlotEvent(PlayerEnterPlotEvent e) {
		plugin.getDataManager().checkTempPlayer(e.getPlotPlayer().getUUID());
		
	}
}