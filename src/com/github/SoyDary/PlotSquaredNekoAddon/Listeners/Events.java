package com.github.SoyDary.PlotSquaredNekoAddon.Listeners;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.implementations.GamemodeFlag;
import com.plotsquared.core.plot.flag.implementations.GuestGamemodeFlag;
import com.plotsquared.core.plot.flag.implementations.FlyFlag;

public class Events implements Listener{
	
	PSNA plugin;
	
	public Events(PSNA plugin) {
		this.plugin = plugin;
	}
	
    @EventHandler
	public void fly(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();	
		Plot plot =  BukkitUtil.adapt(p).getCurrentPlot();
		if(plot == null) return;
		if(!plot.getFlag(FlyFlag.class).name().equals("DISABLED")) return;
		if(plot.isOwner(p.getUniqueId()) || plot.isAdded(p.getUniqueId()) || p.hasPermission("plots.admin.flight")) return;
	    e.setCancelled(true);
	    p.setAllowFlight(false);	
	}
    
	@EventHandler
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
		Player p = e.getPlayer();
		Plot plot =  BukkitUtil.adapt(p).getCurrentPlot();
		if(plot == null) return;
		int plotModes = 0;
		String guest_gamemode = plot.getFlag(GuestGamemodeFlag.class).toString();
		String gamemode = plot.getFlag(GamemodeFlag.class).toString();
		if(!guest_gamemode.equals("default")) plotModes++;
		if(!gamemode.equals("default")) plotModes++;		
		if(plotModes == 0) return;
		String newmode = e.getNewGameMode().name().toLowerCase();
		if(plotModes == 1) {
			if(!guest_gamemode.equals("default")) {
				checkCancelled(e, p, plot, newmode, guest_gamemode);
				return;
			}
			if(!gamemode.equals("default")) {
				checkCancelled(e, p, plot, newmode, gamemode);
				return;
			}
		} 
		if(plotModes == 2) {
			if(plugin.getDataManager().tempPlayers.contains(p.getUniqueId()) && newmode.equals(gamemode)) return;	
			checkCancelled(e, p, plot, newmode, guest_gamemode);

		}
	}

	public void checkCancelled(PlayerGameModeChangeEvent e, Player p, Plot plot, String mode, String plotMode) {
		if(!mode.equals(plotMode)) {
			if(plot.isOwner(p.getUniqueId()) || plot.isAdded(p.getUniqueId()) || plot.getMembers().contains(p.getUniqueId()) || p.hasPermission("plots.gamemode.bypass")) return;
			p.sendActionBar(plugin.getUtils().color("&cNo puedes cambiar tu modo de juego en esta parcela."));
			p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.MASTER,1, 1);
			e.setCancelled(true);
		}
	}
}
