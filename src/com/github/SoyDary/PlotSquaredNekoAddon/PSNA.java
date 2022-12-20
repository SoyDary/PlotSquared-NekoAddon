package com.github.SoyDary.PlotSquaredNekoAddon;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.SoyDary.PlotSquaredNekoAddon.Command.CommandCompleter;
import com.github.SoyDary.PlotSquaredNekoAddon.Command.Commands;
import com.github.SoyDary.PlotSquaredNekoAddon.Listeners.Events;
import com.github.SoyDary.PlotSquaredNekoAddon.Listeners.PlotsListener;
import com.github.SoyDary.PlotSquaredNekoAddon.Managers.DataManager;
import com.github.SoyDary.PlotSquaredNekoAddon.Managers.ProtoManager;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Utils;
import com.plotsquared.core.PlotAPI;

public class PSNA extends JavaPlugin{
	
	private Utils utils;
	private DataManager datamanager;
	private PlotsListener plotslistener;
	public ProtoManager protocolmanager = null;
	public PlotAPI plotsAPI; 
	public String prefix = "&8[&6PlotSquared&7-&eNekoAddon&8]";
	
	public void onEnable() {
		this.enablePlugin();
	    getServer().getConsoleSender().sendMessage("[PlotSquared-NekoAddon] plugin activado!");	
	    
	}
	
	
	
	
	public void onDisable() {
		this.disablePlugin();
		getServer().getConsoleSender().sendMessage("[PlotSquared-NekoAddon] plugin desactivado!");
	}
	
	public void reloadPlugin() {
		this.disablePlugin();
		this.enablePlugin();
	}
	
	private void enablePlugin() {
		this.utils = new Utils(this);
		this.datamanager = new DataManager(this);
		this.plotslistener = new PlotsListener(this);
		this.plotsAPI = new PlotAPI();
		plotsAPI.getPlotSquared().getEventDispatcher().registerListener(plotslistener);
		getServer().getPluginCommand("plotsquared-nekoaddon").setExecutor(new Commands(this));	
		getServer().getPluginCommand("plotsquared-nekoaddon").setTabCompleter(new CommandCompleter(this));
		getServer().getPluginManager().registerEvents(new Events(this), this);
		if(     datamanager.config.getBoolean("GamemodeSettings.enabled") 
				&& datamanager.config.getBoolean("GamemodeSettings.supressEssentialsMessage") 
				&& Bukkit.getPluginManager().getPlugin("ProtocolLib") != null 
				&& Bukkit.getPluginManager().getPlugin("ProtocolLib").isEnabled()
				) this.protocolmanager = new ProtoManager(this);
		
	}
	private void disablePlugin() {
		if(this.protocolmanager != null)
			plotsAPI.getPlotSquared().getEventDispatcher().unregisterListener(plotslistener);	
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