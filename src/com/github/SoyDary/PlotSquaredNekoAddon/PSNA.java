package com.github.SoyDary.PlotSquaredNekoAddon;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;
import com.github.SoyDary.PlotSquaredNekoAddon.Command.CommandCompleter;
import com.github.SoyDary.PlotSquaredNekoAddon.Command.Commands;
import com.github.SoyDary.PlotSquaredNekoAddon.Listeners.Events;
import com.github.SoyDary.PlotSquaredNekoAddon.Listeners.InventoryListener;
import com.github.SoyDary.PlotSquaredNekoAddon.Listeners.PlotsListener;
import com.github.SoyDary.PlotSquaredNekoAddon.Managers.Data;
import com.github.SoyDary.PlotSquaredNekoAddon.Managers.DataManager;
import com.github.SoyDary.PlotSquaredNekoAddon.Managers.ProtoManager;
import com.github.SoyDary.PlotSquaredNekoAddon.Objects.PAPI_Extension;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Utils;
import com.plotsquared.core.PlotAPI;

public class PSNA extends JavaPlugin{
	
	private Utils utils;
	private DataManager datamanager;
	private PlotsListener plotslistener;
	private Data data;
	public FileConfiguration config;
	public FileConfiguration messages;
	public ProtoManager protocolmanager = null;
	public PlotAPI plotsAPI; 
	private Commands commands;
	public Essentials essentials;
	public String prefix = "&8[&6PlotSquared&7-&eNekoAddon&8]";
	
	public void onEnable() {
		this.enablePlugin();
		getServer().getPluginManager().registerEvents(new Events(this), this);
		getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
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
		this.plotsAPI = new PlotAPI();
		this.utils = new Utils(this);
		this.datamanager = new DataManager(this);
		this.plotslistener = new PlotsListener(this);
		this.data = new Data(this);
		this.commands = new Commands(this);
		new PAPI_Extension(this).register();
		plotsAPI.getPlotSquared().getEventDispatcher().registerListener(plotslistener);
		if(Bukkit.getPluginManager().isPluginEnabled("Essentials")) this.essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		getServer().getPluginCommand("plotsquared-nekoaddon").setExecutor(commands);	
		getServer().getPluginCommand("starboard").setExecutor(commands);	
		getServer().getPluginCommand("plotsquared-nekoaddon").setTabCompleter(new CommandCompleter(this));
		getServer().getPluginCommand("starboard").setTabCompleter(new CommandCompleter(this));
		if(     config.getBoolean("GamemodeSettings.enabled") 
				&& config.getBoolean("GamemodeSettings.supressEssentialsMessage") 
				&& Bukkit.getPluginManager().getPlugin("ProtocolLib") != null 
				&& Bukkit.getPluginManager().getPlugin("ProtocolLib").isEnabled()
				) this.protocolmanager = new ProtoManager(this);
		
	}
	private void disablePlugin() {
		for(Player p : getData().offhandItems.keySet()) p.closeInventory();
		if(this.protocolmanager != null)
			plotsAPI.getPlotSquared().getEventDispatcher().unregisterListener(plotslistener);	
	}
	public static PSNA getInstance() {
	    return JavaPlugin.getPlugin(PSNA.class);
	}
	
	public Data getData() {
		return this.data;
	}
	public Utils getUtils() {
		return this.utils;
	}
	
	public DataManager getDataManager() {
		return this.datamanager;
	}
	
	
}