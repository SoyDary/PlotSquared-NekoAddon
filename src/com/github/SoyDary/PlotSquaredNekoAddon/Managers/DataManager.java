package com.github.SoyDary.PlotSquaredNekoAddon.Managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;


public class DataManager {
	
	File configFile;
	public FileConfiguration config;
	
	PSNA plugin;
	public Collection<UUID> tempPlayers;
	
	public DataManager(PSNA plugin) {
		this.plugin = plugin;
		tempPlayers = new ArrayList<UUID>();
		this.loadData();
	}
	
	public void loadData() {	
		this.configFile = new File(plugin.getDataFolder() + File.separator+ "config.yml");
		this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);

		if(!configFile.exists()) {	
            plugin.saveResource("config.yml", false);
            this.reloadConfig();
		}		
	}
	public void reloadConfig() {
		this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
	}
	
	public boolean checkTempPlayer(UUID uuid) {
		if(this.tempPlayers.contains(uuid)) return true;
		this.tempPlayers.add(uuid);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {public void run() {tempPlayers.remove(uuid);}}, 10L);
		return false;
	}

}
