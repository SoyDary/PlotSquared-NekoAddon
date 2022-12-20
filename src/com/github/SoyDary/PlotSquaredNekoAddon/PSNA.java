package com.github.SoyDary.PlotSquaredNekoAddon;

import org.bukkit.plugin.java.JavaPlugin;

import com.github.SoyDary.PlotSquaredNekoAddon.Listeners.Events;
import com.github.SoyDary.PlotSquaredNekoAddon.Listeners.PlotsListener;
import com.github.SoyDary.PlotSquaredNekoAddon.Managers.DataManager;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Utils;
import com.plotsquared.core.PlotAPI;

public class PSNA extends JavaPlugin{
	
	private Utils utils;
	private DataManager datamanager;
	private PlotsListener plotslistener;
	public PlotAPI plotsAPI; 
	public String prefix = "&8[&6PlotSquared&7-&eNekoAddon&8]";
	
	public void onEnable() {
		this.utils = new Utils(this);
		this.datamanager = new DataManager(this);
		this.plotslistener = new PlotsListener(this);
		this.plotsAPI = new PlotAPI();
		//getServer().getPluginCommand("plotsquared-nekoaddon").setExecutor(new Commands(this));	
		//getServer().getPluginCommand("plotsquared-nekoaddon").setTabCompleter(new CommandCompleter(this));
		plotsAPI.getPlotSquared().getEventDispatcher().registerListener(plotslistener);
		getServer().getPluginManager().registerEvents(new Events(this), this);
	    getServer().getConsoleSender().sendMessage("[PlotSquared-NekoAddon] plugin activado!");	
	}
	
	public void onDisable() {
		plotsAPI.getPlotSquared().getEventDispatcher().unregisterListener(plotslistener);
		getServer().getConsoleSender().sendMessage("[PlotSquared-NekoAddon] plugin desactivado!");
	}
	
	public static PSNA getInstance() {
	    return JavaPlugin.getPlugin(PSNA.class);
	}
	
	public Utils getUtils() {
		return this.utils;
	}
	
	public DataManager getDataManager() {
		return this.datamanager;
	}
	
	
}