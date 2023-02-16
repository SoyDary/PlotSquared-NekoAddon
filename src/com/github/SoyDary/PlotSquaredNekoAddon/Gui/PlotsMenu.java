package com.github.SoyDary.PlotSquaredNekoAddon.Gui;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.github.SoyDary.PlotSquaredNekoAddon.Objects.NekoItem;
import com.github.SoyDary.PlotSquaredNekoAddon.Objects.NekoPlot;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums.MenuType;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums.ProfileColor;
import com.google.common.collect.Lists;
import com.plotsquared.core.database.DBFunc;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.util.query.PlotQuery;
import com.plotsquared.core.util.query.SortingStrategy;

public class PlotsMenu implements InventoryHolder {
	
	PSNA plugin = PSNA.getInstance();
	public UUID owner;
	Player viewer;
	Inventory inv;
	List<List<Plot>> sections;
	ProfileColor color;
	public List<Plot> plots;
	public MenuType type;
	public NekoPlot mainPlot;
	public Integer trusted_plots_ammount = 0;
	public Integer page = 0;
	
	public PlotsMenu(Player viewer, UUID owner, MenuType type, int page) {
		this.owner = owner;
		this.viewer = viewer;
		this.page = page;
		this.type = type;
		this.color = plugin.getData().getProfileColor(owner.toString());	
		this.loadPlots();
		if(viewer.getUniqueId().equals(owner)) this.trusted_plots_ammount = this.getTrustedPlotsAmmount();
		this.inv = Bukkit.createInventory(this, invSize(), plugin.getUtils().color(getInvTitle()));
		this.updateInv();
		this.mainPlot = plugin.getData().getMainPlot(owner.toString());
	}
	
	public PlotsMenu(Player p, MenuType type) {
		this.type = type;
		this.inv = Bukkit.createInventory(this, 54, plugin.getUtils().color(plugin.config.getString("PlotsMenu.StarBoardTitle")));
		ItemStack item =  plugin.getDataManager().profileItems.get(ProfileColor.YELLOW);
		for(int slot : getSlots()) {
			if(slot >= inv.getSize() || slot < 0) continue;
			inv.setItem(slot, item);
		}	
		List<Plot> plots = PlotQuery.newQuery().withSortingStrategy(SortingStrategy.SORT_BY_RATING).whereBasePlot().asList();
		int amt = 0;
		for(Plot plot: plots) {
			if(plot.getOwners().contains(DBFunc.SERVER)) continue;
			NekoPlot nekoplot = new NekoPlot(plot);
			if(nekoplot.getLikes() > 0) {
				inv.addItem(nekoplot.getItem(viewer, type, false));
				amt++;
				if(amt >= 28) break;
			}
		}
		for(int i = 10; i < inv.getSize()-10; i++) {
			if(inv.getItem(i) == null) {
				ItemStack fi = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
				ItemMeta meta = fi.getItemMeta();
				meta.displayName(plugin.getUtils().color(""));
				fi.setItemMeta(meta);
				inv.setItem(i, fi);
			}
		}
		inv.setItem(this.inv.getSize()-1, NekoItem.OWNED_PLOTS);
		
	}
	
	@Override
	public Inventory getInventory() {	
		if(type == MenuType.StarBoard) return this.inv;
		if(type == MenuType.Owned && this.mainPlot != null) {
			if(viewer.hasPermission("multiverse.access."+mainPlot.plot.getWorldName()) && (canJoin(mainPlot.plot) || viewer.hasPermission("plots.visit.denied"))) {
			    inv.addItem(mainPlot.getItem(viewer, type, true));
				plots.remove(mainPlot.plot);
			}
		}
		for(Plot p : sections.get(page)) {
			NekoPlot plot = new NekoPlot(p);
			ItemStack item = plot.getItem(viewer, type, false);		
			inv.addItem(item);
		}
		for(int i = 10; i < inv.getSize()-10; i++) {
			if(inv.getItem(i) == null) {
				ItemStack fi = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
				ItemMeta meta = fi.getItemMeta();
				meta.displayName(plugin.getUtils().color(""));
				fi.setItemMeta(meta);
				inv.setItem(i, fi);
			}
		}
		return inv;
	}
	
	public void loadPlots() {
		if(type == MenuType.Owned) loadOwnedPlots();
		if(type == MenuType.Trusted) loadTustedPlots();
		
	}
	
	private String getInvTitle() {
		if(type == MenuType.Trusted) return setPlaceholders(plugin.config.getString("PlotsMenu.TrustedPlotsTitle"));
		if(!viewer.getUniqueId().equals(owner)) return setPlaceholders(plugin.config.getString("PlotsMenu.OtherPlayerPlotsTitle"));
		return setPlaceholders(plugin.config.getString("PlotsMenu.PlayerPlotsTitle"));
	}
	
	public Integer getTrustedPlotsAmmount() {
		int i = 0;
		for(String world : plugin.getDataManager().worldSorter) {
			if(!viewer.hasPermission("multiverse.access."+world)) continue;
			List<Plot> queryplots = PlotQuery.newQuery().withMember(owner)
					.inWorld(world)
					.whereBasePlot().asList();
			for(Plot plot : queryplots) {
				if(plot.isOwner(owner) || plot.getOwners().contains(DBFunc.SERVER)) continue;	
				i++;
			}		
		}
		return i;
	}
	public String setPlaceholders(String str) {
		if(str == null) return "";
		str = str.replaceAll("%owner-name%", plugin.getData().getPlayerName(owner));
		return str;
		
	}
	
	private void loadOwnedPlots() {		
		this.plots = new ArrayList<Plot>();
		boolean is_admin = viewer.hasPermission("plots.visit.denied");
		for(String world : plugin.getDataManager().worldSorter) {
			if(!viewer.hasPermission("multiverse.access."+world)) continue;
			List<Plot> queryplots = PlotQuery.newQuery().ownedBy(owner)
					.inWorld(world)
					.whereBasePlot()
					.withSortingStrategy(SortingStrategy.SORT_BY_CREATION).asList();
			for(Plot plot : queryplots) {
				if((!is_admin && !canJoin(plot)) || plot.getOwners().contains(DBFunc.SERVER)) continue;	
				plots.add(plot);
			}			
		}	
		sections = Lists.partition(plots, 28);
	}
	
	private void loadTustedPlots() {
		this.plots = new ArrayList<Plot>();
		for(String world : plugin.getDataManager().worldSorter) {
			if(!viewer.hasPermission("multiverse.access."+world)) continue;	
			List<Plot> queryplots = PlotQuery.newQuery().withMember(owner)
					.inWorld(world)
					.whereBasePlot()
					.withSortingStrategy(SortingStrategy.SORT_BY_CREATION).asList();
			for(Plot plot : queryplots) {
				if(plot.isOwner(owner) || plot.getOwners().contains(DBFunc.SERVER)) continue;	
				plots.add(plot);
			}
			
		}	
		sections = Lists.partition(plots, 28);
	}
	
	
	private boolean canJoin(Plot plot) {
		if(plot.isOwner(viewer.getUniqueId())) return true;
		if(plot.isDenied(viewer.getUniqueId())) return false;
		if(plot.getDenied().contains(DBFunc.EVERYONE) && !plot.isAdded(viewer.getUniqueId()))  return false;
		return true;
	}
	
	public void updateInv() {
		ItemStack item =  plugin.getDataManager().profileItems.get(color);
		for(int slot : getSlots()) {
			if(slot >= inv.getSize() || slot < 0) continue;
			inv.setItem(slot, item);
		}	
		if(type == MenuType.Owned || type == MenuType.Trusted) {	
			if(sections.size() > page+1) inv.setItem(inv.getSize()-4, NekoItem.NEXT_PAGE);
			if(page > 0) inv.setItem(inv.getSize()-6, NekoItem.PREV_PAGE);
		}
		if(viewer.getUniqueId().equals(owner)) {	
			extraItems();
		}
	}
	
	private int[] getSlots() {
		int s = inv.getSize()-1;
		int[] slots = new int[] {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 54, s, s-1, s-2, s-3, s-4, s-5, s-6, s-7, s-8};
		return slots;
	}
	
	private void extraItems() {
		if(type == MenuType.Owned && this.trusted_plots_ammount > 0) inv.setItem(inv.getSize()-1, NekoItem.TRUSTED_PLOTS);		
		if(type == MenuType.Trusted) inv.setItem(inv.getSize()-1, NekoItem.OWNED_PLOTS);
	}
	
	private int invSize() {
		if(plots.size() >= 22) return 54;
		if(plots.size() >= 15) return 45;
		if(plots.size() >= 8) return 36;
		return 27;		
	}
	

}
