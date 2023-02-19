package com.github.SoyDary.PlotSquaredNekoAddon.Managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.github.SoyDary.PlotSquaredNekoAddon.Objects.NekoPlot;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums.ProfileColor;

public class Data {
	
	PSNA plugin;
	public Map<Player, ItemStack> offhandItems;
	public Map<String, FileConfiguration> configs;
	public Map<UUID, String> names;
	private Map<String, ProfileColor> profiles;
	boolean isReady = false;
		
	public Data(PSNA plugin) {
		this.offhandItems = new HashMap<Player, ItemStack>();
		this.configs = new HashMap<String, FileConfiguration>();
		this.names = new HashMap<UUID, String>();
		this.profiles = new HashMap<String, ProfileColor>();
		this.plugin = plugin;
		loadPlayersData();
		
	}
	
	private void loadPlayersData() {
		Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
		    @Override
		    public void run() {
		    	File dataFolder = new File(plugin.getDataFolder()+"/data/");
		    	File[] files = dataFolder.listFiles();
		    	for(File file : files) {
		    		if(file.getName().length() < 40 || !file.getName().endsWith(".yml")) continue;
		    		String uuid = file.getName().replaceAll(".yml", "");
		    		getConfiguration(uuid);
		    		getPlayerName(UUID.fromString(uuid));
		    	}
		    	isReady = true;
		    }
		});
	}
	
	public String getPlayerName(UUID uuid) {
		if(names.containsKey(uuid)) {
			return names.get(uuid);
		}
		String name = Bukkit.getOfflinePlayer(uuid).getName();
		names.put(uuid, name);
		return name;
	}
	
	public boolean getFollowState(String uuid) {
		FileConfiguration config = getConfiguration(uuid);
		String state = config.getString("AllowedFollow");	
		if(state != null && state.equals("DISABLED")) return false;
		return true;
	}
	public void setFollowState(String uuid, Boolean state) {
		FileConfiguration config = getConfiguration(uuid);
		config.set("AllowedFollow", state ? "ENABLED" : "DISABLED");
		saveConfig(uuid);	
	}
	
	public boolean getLikeMessageState(String uuid) {
		FileConfiguration config = getConfiguration(uuid);
		String state = config.getString("LikeMessage");	
		if(state != null && state.equals("DISABLED")) return false;
		return true;
	}
	public void setLikeMessgeState(String uuid, Boolean state) {
		FileConfiguration config = getConfiguration(uuid);
		config.set("LikeMessage", state ? "ENABLED" : "DISABLED");
		saveConfig(uuid);	
	}
	public void setMainPlot(String uuid, String path) {
		FileConfiguration config = getConfiguration(uuid);
		config.set("MainPlot", path);
		saveConfig(uuid);
	}
	
	public NekoPlot getMainPlot(String uuid) {
		FileConfiguration config = getConfiguration(uuid);
		if(config.getString("MainPlot") == null) return null;
		NekoPlot nekoplot = new NekoPlot(config.getString("MainPlot"));
		if(!nekoplot.plot.getOwners().contains(UUID.fromString(uuid))) {
			setMainPlot(uuid, null);
			return null;
		}
		return nekoplot;
	}
	public void saveSkin(Player p) {
		String skin = plugin.getUtils().getSkinID(p);
		String savedSkin = this.getSkinID(p.getUniqueId().toString());
		if(savedSkin != null && savedSkin.equals(skin)) return;
		FileConfiguration config = getConfiguration(p.getUniqueId().toString());
		config.set("SkinID", skin);
		saveConfig(p.getUniqueId().toString());	
	}
	
	public String getSkinID(String uuid) {
		FileConfiguration config = getConfiguration(uuid);
		return config.getString("SkinID");
		
	}
	
	public ItemStack getPlayerHead(String uuid) {
		String skin = this.getSkinID(uuid);
		if(skin != null) return plugin.getUtils().getHead(skin, UUID.fromString(uuid));		
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
	    SkullMeta headMeta = (SkullMeta) head.getItemMeta();
	    PlayerProfile profile = Bukkit.createProfile(getPlayerName(UUID.fromString(uuid)));
	    headMeta.setPlayerProfile(profile);
	    head.setItemMeta(headMeta); 
	    return head;
		
	}
	
	public void setProfileColor(String uuid, ProfileColor color) {
		profiles.put(uuid, color);
		FileConfiguration config = getConfiguration(uuid);
		config.set("ProfileColor", color.name());
		saveConfig(uuid);
	}
	
	public ProfileColor getProfileColor(String uuid) {
		if(profiles.containsKey(uuid)) return profiles.get(uuid);
		FileConfiguration config = getConfiguration(uuid);
		String color = config.getString("ProfileColor");
		if(color != null) {
			ProfileColor pc = ProfileColor.valueOf(color);
			profiles.put(uuid, pc);
			return pc;
		}
		profiles.put(uuid, ProfileColor.ORANGE);
		return ProfileColor.ORANGE;
		
	}
	
	public void saveConfig(String uuid) {
		File file = new File(plugin.getDataFolder()+"/data/"+uuid+".yml");
		FileConfiguration config = configs.getOrDefault(uuid, YamlConfiguration.loadConfiguration(file));
		try {
			config.save(file);
	    }
	    catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	
	public FileConfiguration getConfiguration(String uuid) {
		File file = new File(plugin.getDataFolder()+"/data/"+uuid+".yml");
		FileConfiguration config = configs.getOrDefault(uuid, YamlConfiguration.loadConfiguration(file));
		configs.put(uuid, config);
		return config;
	}
	

}
