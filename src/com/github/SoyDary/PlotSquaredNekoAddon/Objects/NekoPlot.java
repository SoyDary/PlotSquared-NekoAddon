package com.github.SoyDary.PlotSquaredNekoAddon.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums.MenuType;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums.ProfileColor;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.ItemUtils;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.database.DBFunc;
import com.plotsquared.core.events.TeleportCause;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.implementations.DescriptionFlag;
import com.plotsquared.core.plot.flag.implementations.GamemodeFlag;
import com.plotsquared.core.plot.flag.implementations.GuestGamemodeFlag;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class NekoPlot {
	
	private PSNA plugin = PSNA.getInstance();
	public String path;
	public String ownerName;
	public String ownerUUID;
	public String tempNaming;
	public Plot plot;
	public ProfileColor profileColor;
	
	

	public NekoPlot(Plot plot) {
		this.plot = plot;
		this.path = plot.getArea().getWorldName()+"_"+plot.getId().toString();
		this.ownerName = plugin.getData().getPlayerName(plot.getOwner());
		this.ownerUUID = plot.getOwner().toString();
		this.profileColor = plugin.getData().getProfileColor(ownerUUID);
	}
	
	public NekoPlot(String tag) {
		if(tag == null) {
			this.plot = null;
			return;
		}	
		try {
			this.plot = Plot.fromString(plugin.plotsAPI.getPlotSquared().getPlotAreaManager().getPlotAreaByString(tag.split("_")[0]), tag.split("_")[1]);
		} catch(Exception e) {}
		this.path = tag; 
		if(plot == null) return;
		if(plot.hasOwner()) {
			this.ownerName = plugin.getData().getPlayerName(plot.getOwner());
			this.ownerUUID = plot.getOwner().toString();
			this.profileColor = plugin.getData().getProfileColor(ownerUUID);
		}
	}
	
	public void setCustomName(String name) {
		FileConfiguration config = plugin.getData().getConfiguration(ownerUUID);
		config.set("PlotsData."+path+".customName", name);	
		plugin.getData().saveConfig(ownerUUID);
	}
	
	public void teleportPlayer(Player p, TeleportCause cause) {
		if(!canJoin(p)) {
			p.sendMessage(plugin.getUtils().color("&8[&6P2&8] &cNo puedes ir a esta parcela porque fuiste denegado de ella."));		
			return;
		}
		plot.teleportPlayer(BukkitUtil.adapt(p), cause, null);
	}
	
	public boolean canJoin(Player p) {
		if(!p.hasPermission("multiverse.access."+plot.getWorldName())) return false;
		if(plot.isOwner(p.getUniqueId()) || plot.getMembers().contains(p.getUniqueId())) return true;
		if(plot.isDenied(p.getUniqueId()) && !p.hasPermission("plots.visit.denied")) return false;
		if(plot.getDenied().contains(DBFunc.EVERYONE) && !plot.isAdded(p.getUniqueId()))  return false;
		return true;
	}
	public ItemStack getItem(Player viewer, MenuType type, Boolean isMainPlot) {
		FileConfiguration config = plugin.getData().getConfiguration(ownerUUID);
		String itemString = config.getString("PlotsData."+path+".item");
		ItemStack item = type == MenuType.StarBoard ? plugin.getData().getPlayerHead(ownerUUID) : decodeItem(itemString);
		ItemMeta meta = item.getItemMeta();
		meta.displayName(plugin.getUtils().color("&f"+setPlaceholders(viewer, plugin.config.getString("PlotsMenu.Items.PlotSelector.name"), isMainPlot, true)));
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
		meta.addItemFlags(ItemFlag.HIDE_DYE);
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);			
		meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);	
		List<Component> lore = new ArrayList<Component>();
		List<String> configLore = type == MenuType.Owned ? plugin.config.getStringList("PlotsMenu.Items.PlotSelector.lore") : plugin.config.getStringList("PlotsMenu.Items.PlotSelector.trusted_lore");	
		for(String line : configLore) {
			boolean ignoreLine = false;
			for(String str : plugin.getDataManager().ignoredPlaceholders.keySet()) {
				if(ignoreLine) break;
				if(line.contains(str)) {
					if(setPlaceholders(viewer, str, isMainPlot, true).equals(setPlaceholders(viewer, plugin.getDataManager().ignoredPlaceholders.get(str), isMainPlot, true))) {
						ignoreLine = true;
						break;
					}
				}
			}
			if(!ignoreLine) {
				String[] ex = line.split("\\n");
				for(String text2 : ex) {
					lore.add(plugin.getUtils().color(setPlaceholders(viewer, text2, isMainPlot, true)));
				}
			}
		}
		if(type == MenuType.Owned && (ownerUUID.equals(viewer.getUniqueId().toString()) || viewer.hasPermission("nekoplots.admin"))) {
			for(String line : plugin.config.getStringList("PlotsMenu.Items.PlotSelector.owner_extra_lore")) {
				lore.add(plugin.getUtils().color(setPlaceholders(viewer, line, isMainPlot, true)));
			}
		}
		meta.lore(lore);
		item.setItemMeta(meta);	
		ItemUtils.setData(item, "plot_item", plot.getArea().getWorldName()+"_"+plot.getId().toString());
		return item;
	}
	
	public void setItem(ItemStack item) {
		FileConfiguration config = plugin.getData().getConfiguration(ownerUUID);
		config.set("PlotsData."+path+".item",  encodeItem(item));
		plugin.getData().saveConfig(ownerUUID);
	}
	
	private ItemStack decodeItem(String str) {
		if(str == null) {
			ItemStack item = new ItemStack(plugin.getUtils().getRandomItem());
			this.setItem(item);
			return item;
		}
		if(str.startsWith("GENERIC;")) {
			return new ItemStack(Material.valueOf(str.split("GENERIC;")[1]));
		}
		ItemStack b = plugin.getDataManager().encodedItems.getOrDefault(str, null);
		if(b != null) {
			return b;
		}
		ItemStack x = ItemUtils.itemFromBase64(str);
		plugin.getDataManager().encodedItems.put(str, x);
		return x;
	}
	public Boolean isMainPlot() {
		NekoPlot np = plugin.getData().getMainPlot(ownerUUID);
		if(np != null && np.path.equals(this.path)) return true;
		return false;
	}
	private String encodeItem(ItemStack item) {
		item = clearItemData(item);
		if(!item.asOne().hasItemMeta()) return "GENERIC;"+item.getType();
		return ItemUtils.itemToBase64(item);
	}
	public int getLikes() {
		Map<UUID, Boolean> likes = plot.getLikes();
		int i = 0;
		for(UUID u : likes.keySet()) if(likes.get(u)) i++;
		return i;
	}
	
	public String setPlaceholders(Player p, String str, boolean mainPlot, boolean papi) {
		if(str == null) return "";
		if(str.contains("%plot-customname%")) str = str.replaceAll("%plot-customname%", getCustomName(p, mainPlot));
		str = str.replaceAll("%owner-name%", ownerName)
				.replaceAll("%owner-color%", "&"+profileColor.color)
				.replaceAll("%plot-description%", getDescription())
				.replaceAll("%plot-key%", path)
				.replaceAll("%plot-likes%", ""+getLikes())
				.replaceAll("%plot-alias%", plot.getAlias())
				.replaceAll("%plot-id%", plot.getId().toString())
				.replaceAll("%plot-mode%", getPlotMode())
				.replaceAll("%plot-world%", plugin.getDataManager().getWorldAlias(plot.getWorldName()));
		if(str.contains("%plot-mainicon%")) {
			if(mainPlot) str = str.replaceAll("%plot-mainicon%", plugin.config.getString("PlotsMenu.MainPlotIcon"));	
			str = str.replaceAll("%plot-mainicon%", "");
		}
		if(papi) str = PlaceholderAPI.setPlaceholders(p, str);
		return str;
	}
	
	private String getDescription() {
		String desc = plot.getFlag(DescriptionFlag.class).toString();
		if(desc.equals("")) return "default";
		if(PlainTextComponentSerializer.plainText().serialize(plugin.getUtils().color(desc)).length() > 40) return desc.substring(0, 40);
		return desc;
	}
	private ItemStack clearItemData(ItemStack item) {
		if(item.asOne().hasItemMeta()) {
			ItemStack is = null;
			while(is == null) {
				if(item.getType().name().equals("CHEST")) is = new ItemStack(Material.CHEST);
				if(item.getType().name().equals("TRAPPED_CHEST")) is = new ItemStack(Material.TRAPPED_CHEST);
				if(item.getType().name().equals("WRITABLE_BOOK")) is = new ItemStack(Material.WRITABLE_BOOK);
				if(item.getType().name().equals("WRITABLE_BOOK")) is = new ItemStack(Material.WRITTEN_BOOK);
				if(item.getType().name().equals("SHULKER_BOX")) is = new ItemStack(Material.SHULKER_BOX);
				if(item.getType().name().endsWith("_SHULKER_BOX")) is = new ItemStack(Material.valueOf(is.getType().name().split("SHULKER_BOX")[0]+"SHULKER_BOX"));
				if(item.getType().name().equals("DISPENSER")) is = new ItemStack(Material.DISPENSER);
				if(item.getType().name().equals("DROPPER")) is = new ItemStack(Material.DROPPER);
				if(item.getType().name().equals("HOPPER")) is = new ItemStack(Material.HOPPER);
				if(item.getType().name().equals("BARREL")) is = new ItemStack(Material.BARREL);
				if(item.getType().name().equals("FURNACE")) is = new ItemStack(Material.FURNACE);
				if(item.getType().name().equals("OBSERVER")) is = new ItemStack(Material.OBSERVER);
				if(item.getType().name().equals("BLAST_FURNACE")) is = new ItemStack(Material.BLAST_FURNACE);
				if(item.getType().name().equals("SMOKER")) is = new ItemStack(Material.SMOKER);
				break;
			}
			if(is != null) {
				if(item.getItemMeta().hasEnchants()) {
					ItemMeta meta = is.getItemMeta();
					meta.addEnchant(Enchantment.LURE, 1, false);
					is.setItemMeta(meta);
				}
				return is;
			}
		} 
		if(!item.hasItemMeta()) return item.asOne();
		ItemMeta meta = item.getItemMeta();
		meta.lore(null);
		meta.displayName(null);		
		return item.asOne();
	}
	
	public String getCustomName(Player p, boolean isMainPlot) {
		FileConfiguration config = plugin.getData().getConfiguration(ownerUUID);
		String name = config.getString("PlotsData."+path+".customName");
		if(name == null) name = plugin.config.getString("PlotsMenu.DefaultPlotName");
		return setPlaceholders(p, name, isMainPlot, true);
	}
	
	public String getPlotMode() {
		String mode = checkPlotMode();
		if(mode.equals("survival")) return "supervivencia";
		if(mode.equals("creative")) return "creativo";
		if(mode.equals("spectator")) return "espectador";
		if(mode.equals("adventure")) return "aventura";
		return "default";
	}
	
	private String checkPlotMode() {
		int plotModes = 0;
		String guest_gamemode = plot.getFlag(GuestGamemodeFlag.class).toString();
		String gamemode = plot.getFlag(GamemodeFlag.class).toString();
		if(!guest_gamemode.equals("default")) plotModes++;
		if(!gamemode.equals("default")) plotModes++;		
		if(plotModes == 0) return "default";
		if(plotModes == 1) {
			if(!guest_gamemode.equals("default")) return guest_gamemode;
			if(!gamemode.equals("default")) return gamemode;
			
		} 
		if(plotModes == 2) {
			return guest_gamemode;
		}
		return "default";
	}

}
