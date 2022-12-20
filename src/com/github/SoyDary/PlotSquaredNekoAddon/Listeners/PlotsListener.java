package com.github.SoyDary.PlotSquaredNekoAddon.Listeners;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.events.PlayerEnterPlotEvent;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.implementations.FlyFlag;

public class PlotsListener {
	
	PSNA plugin;
	
	public PlotsListener(PSNA plugin) {
		this.plugin = plugin;
	}
	
	@Subscribe
	public void onPlayerEnterPlotEvent(PlayerEnterPlotEvent e) {
		Plot plot = e.getPlot();
		Player p = (Player) e.getPlotPlayer().getPlatformPlayer();
		
		if(plugin.getDataManager().config.getBoolean("GamemodeSettings.enabled")) {
			plugin.getDataManager().checkTempPlayer(p.getUniqueId());
		}
		if(plot.getFlag(FlyFlag.class).name().equals("DISABLED")) {
			if(p.getGameMode().name().toString().equals("SPECTATOR")) {	
				p.setGameMode(GameMode.CREATIVE);
				
			}
		}

		
	}
}
