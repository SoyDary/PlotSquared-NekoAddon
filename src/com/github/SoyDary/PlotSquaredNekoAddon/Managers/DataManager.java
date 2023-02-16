package com.github.SoyDary.PlotSquaredNekoAddon.Managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.github.SoyDary.PlotSquaredNekoAddon.Objects.NekoPlot;
import com.github.SoyDary.PlotSquaredNekoAddon.Objects.NekoItem.ItemKey;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.ComponentUtil.CustomComponent;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums.ClickAction;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums.ProfileColor;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.ItemUtils;

import net.kyori.adventure.text.Component;



public class DataManager {
	
	private File configFile;
	private File messagesFile;
	public FileConfiguration config;
	public FileConfiguration messages;
	private PSNA plugin;
	public Collection<UUID> tempPlayers;
	public Collection<String> worldSorter;
	public HashMap<String, CustomComponent> customComponents;
	public HashMap<Player, NekoPlot> plotNaming;
	public HashMap<ProfileColor, ItemStack> profileItems;
	public HashMap<ItemKey, ItemStack> guiItems;
	public HashMap<String, ItemStack> encodedItems;
	public HashMap<String, String> ignoredPlaceholders;
	
	public Boolean gamemode_settings_enabled;
	public long like_message_delay;
	public String like_message;
	public Boolean like_message_enabled;

	
	public DataManager(PSNA plugin) {
		this.plugin = plugin;
		this.tempPlayers = new ArrayList<UUID>();
		this.plotNaming = new HashMap<Player, NekoPlot>();
		this.encodedItems = new HashMap<String, ItemStack>();
		this.ignoredPlaceholders = new 	HashMap<String, String>();
		this.loadConfig();
		this.loadSettings();
		this.loadWorlds();
		this.ignoredPlaceholders();
		this.loadItems();
		this.loadChatComponents();
	}
	
	private void ignoredPlaceholders() {
		for(String str : config.getStringList("PlotsMenu.IgnoredPlaceholders")) {
			String[] a = str.split(";");
			if(a.length > 2) continue;
			this.ignoredPlaceholders.put(a[0], a[1]);
		}
	}
	
	private void loadItems() {
		this.profileItems = new HashMap<ProfileColor, ItemStack>();
		for(ProfileColor profile : ProfileColor.class.getEnumConstants()) {
			ItemStack item = new ItemStack(Material.valueOf(profile.name()+"_STAINED_GLASS_PANE"));
			ItemMeta meta = item.getItemMeta();
			meta.displayName(Component.text(""));
			item.setItemMeta(meta);
			ItemUtils.setData(item, "plot_item", "FILLER");
			this.profileItems.put(profile, item);
		}	
		
		this.guiItems = new HashMap<ItemKey, ItemStack>();
		ItemStack TRUSTED_PLOTS = new ItemStack(Material.BIRCH_DOOR);
		ItemMeta TRUSTED_PLOTS_META = TRUSTED_PLOTS.getItemMeta();
		TRUSTED_PLOTS_META.displayName(plugin.getUtils().color("&e&lParcelas confiadas"));
		List<Component> TRUSTED_PLOTS_LORE = new ArrayList<Component>();
		TRUSTED_PLOTS_LORE.add(plugin.getUtils().color("&fMenú de parcelas en las que"));
		TRUSTED_PLOTS_LORE.add(plugin.getUtils().color("&fse te otorgaron permisos."));
		TRUSTED_PLOTS_META.lore(TRUSTED_PLOTS_LORE);
		TRUSTED_PLOTS.setItemMeta(TRUSTED_PLOTS_META);
		ItemUtils.setData(TRUSTED_PLOTS, "plot_item", "TRUSTED_PLOTS");
		this.guiItems.put(ItemKey.TRUSTED_PLOTS, TRUSTED_PLOTS);
		
		ItemStack OWNED_PLOTS = new ItemStack(Material.OAK_DOOR);
		ItemMeta OWNED_PLOTS_META = OWNED_PLOTS.getItemMeta();
		OWNED_PLOTS_META.displayName(plugin.getUtils().color("&6&lMis parcelas"));
		List<Component> OWNED_PLOTS_LORE = new ArrayList<Component>();
		OWNED_PLOTS_LORE.add(plugin.getUtils().color("&fMenú principal de parcelas."));
		OWNED_PLOTS_META.lore(OWNED_PLOTS_LORE);
		OWNED_PLOTS.setItemMeta(OWNED_PLOTS_META);
		ItemUtils.setData(OWNED_PLOTS, "plot_item", "OWNED_PLOTS");
		this.guiItems.put(ItemKey.OWNED_PLOTS, OWNED_PLOTS);
		
		
		ItemStack STARBOARD = new ItemStack(Material.NETHER_STAR);
		ItemMeta STARBOARD_META = STARBOARD.getItemMeta();
		STARBOARD_META.displayName(plugin.getUtils().color("&#ffff00&lParcelas más votadas"));
		List<Component> STARBOARD_LORE = new ArrayList<Component>();
		STARBOARD_LORE.add(plugin.getUtils().color("&fMenú de las parcelas con más estrellas"));
		STARBOARD_LORE.add(plugin.getUtils().color("&fde todo el servidor."));
		STARBOARD_META.lore(STARBOARD_LORE);
		STARBOARD.setItemMeta(STARBOARD_META);
		ItemUtils.setData(STARBOARD, "plot_item", "STARBOARD");
		this.guiItems.put(ItemKey.STARBOARD, STARBOARD);
		
		ItemStack PREV_PAGE = plugin.getUtils().getHeadfromUrl("a2f0425d64fdc8992928d608109810c1251fe243d60d175bed427c651cbe");
		ItemMeta PREV_PAGE_META = PREV_PAGE.getItemMeta();
		PREV_PAGE_META.displayName(plugin.getUtils().color("&6&lPágina anterior"));
		PREV_PAGE.setItemMeta(PREV_PAGE_META);
		ItemUtils.setData(PREV_PAGE, "plot_item", "PREV_PAGE");
		this.guiItems.put(ItemKey.PREV_PAGE, PREV_PAGE);
		
		ItemStack NEXT_PAGE = plugin.getUtils().getHeadfromUrl("6d865aae2746a9b8e9a4fe629fb08d18d0a9251e5ccbe5fa7051f53eab9b94");
		ItemMeta NEXT_PAGE_META = NEXT_PAGE.getItemMeta();
		NEXT_PAGE_META.displayName(plugin.getUtils().color("&6&lPágina siguiente"));
		NEXT_PAGE.setItemMeta(NEXT_PAGE_META);
		ItemUtils.setData(NEXT_PAGE, "plot_item", "NEXT_PAGE");
		this.guiItems.put(ItemKey.NEXT_PAGE, NEXT_PAGE);
		
	}
	
	private void loadChatComponents() {
		this.customComponents = new HashMap<String, CustomComponent>();
		if(config.getConfigurationSection("ChatComponents") == null) return;
		for(String key : config.getConfigurationSection("ChatComponents").getKeys(false)) {
			String label = "%"+key+"%";
			String hover = null;
			String clickAction = config.getString("ChatComponents."+key+".ClickAction");
			String action = ""+config.getString("ChatComponents."+key+".Actions");
			if(config.getString("ChatComponents."+key+".Label") != null) label = config.getString("ChatComponents."+key+".Label");
			if(!config.getStringList("ChatComponents."+key+".Hover").isEmpty()) {
				List<String> lines = config.getStringList("ChatComponents."+key+".Hover");
				StringBuilder sb = new StringBuilder();
				for(int i = 0; i < lines.size(); i++) {
					if(i > 0) sb.append("\n");
					sb.append(lines.get(i));
				}
				hover = sb.toString();
			} else hover = config.getString("ChatComponents."+key+".Hover");
			if(clickAction != null) {
				boolean validAction = false;
				clickAction = clickAction.toUpperCase();
				for(ClickAction click :ClickAction.class.getEnumConstants()) {
					if(click.name().equals(clickAction)) {
						validAction = true;
						break;
					}
				}
				if(!validAction) {
					plugin.getLogger().log(Level.WARNING, "La acción '"+clickAction+"' es inválida en el componente: "+key);
					clickAction = null;
				}		
			}
			CustomComponent component = new CustomComponent(key ,label, clickAction, action, hover);	
			this.customComponents.put("%"+key+"%", component);
		}
	}
	private void loadSettings() {
		this.gamemode_settings_enabled = config.getBoolean("GamemodeSettings.enabled");
		this.like_message_enabled = config.getBoolean("LikeMessage.enabled");
		this.like_message_delay = config.getLong("LikeMessage.delay");
		this.like_message = config.getString("LikeMessage.message");
	}
	private void loadWorlds() {
		this.worldSorter = new ArrayList<String>();
		for(String w : config.getStringList("PlotsMenu.WorldSorter")) { 
			if(!worldSorter.contains(w)) this.worldSorter.add(w); 
		}
	}
	
	public String getWorldAlias(String world) {
		String alias = config.getString("PlotsMenu.WorldAlias."+world); 
		return alias == null ? world: alias;
	}
	
	
	private void loadConfig() {	
		this.configFile = new File(plugin.getDataFolder() + File.separator+ "config.yml");
		if(!configFile.exists()) {	
            plugin.saveResource("config.yml", false);
		} 
		this.messagesFile = new File(plugin.getDataFolder() + File.separator+ "messages.yml");
		if(!messagesFile.exists()) {	
            plugin.saveResource("messages.yml", false);
		} 
		reloadConfig();
		plugin.messages = this.messages;
		plugin.config = this.config;
	}
	public void reloadConfig() {
		this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
		this.messages = (FileConfiguration)YamlConfiguration.loadConfiguration(this.messagesFile);
		plugin.config = this.config;
		plugin.messages = this.messages;
	}
	
	
	public boolean checkTempPlayer(UUID uuid) {
		if(this.tempPlayers.contains(uuid)) return true;
		this.tempPlayers.add(uuid);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {public void run() {tempPlayers.remove(uuid);}}, 10L);
		return false;
	}

}
