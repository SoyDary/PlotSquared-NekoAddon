package com.github.SoyDary.PlotSquaredNekoAddon.Listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.database.DBFunc;
import com.plotsquared.core.events.PlayerEnterPlotEvent;
import com.plotsquared.core.events.PlayerLeavePlotEvent;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.implementations.FlyFlag;

import net.kyori.adventure.text.Component;


public class PlotsListener {
	
	PSNA plugin;
	HashMap<Player, Integer> tasks = new HashMap<Player, Integer>();
	
	public PlotsListener(PSNA plugin) {
		this.plugin = plugin;
	}
	
	@Subscribe
	public void onPlayerLeavePlotEvent(PlayerLeavePlotEvent e) {
		Player p = (Player) e.getPlotPlayer().getPlatformPlayer();
		if(tasks.containsKey(p)) {
			Bukkit.getScheduler().cancelTask(tasks.remove(p));
			tasks.remove(p);
		}
	}
	@Subscribe
	public void onPlayerEnterPlotEvent(PlayerEnterPlotEvent e) {
		Plot plot = e.getPlot();
		if(!plot.hasOwner() || plot.getOwners().contains(DBFunc.SERVER)) return;
		Player p = (Player) e.getPlotPlayer().getPlatformPlayer();
		if(plugin.getDataManager().like_message_enabled && plugin.getData().getLikeMessageState(p.getUniqueId().toString())) {		
			if(!plot.getOwners().contains(p.getUniqueId()) && !plot.getLikes().containsKey(p.getUniqueId())) {
		        int taskid = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {	        
		            @Override
		            public void run() {	         
		            	Component message = plugin.getUtils().component(plugin.config.getString("LikeMessage.message"), p, plot);
		            	p.sendMessage(message);
		            	tasks.remove(p);
		            }         
		        }, plugin.getDataManager().like_message_delay*20);
		        tasks.put(p, taskid);
			}
		}		
		if(!plugin.getDataManager().gamemode_settings_enabled) return;
		plugin.getDataManager().checkTempPlayer(p.getUniqueId());
		if(plot.getFlag(FlyFlag.class).name().equals("DISABLED")) {
			if(p.getGameMode().name().toString().equals("SPECTATOR")) {	
				p.setGameMode(GameMode.CREATIVE);
				
			}
		}
		
	}
}
