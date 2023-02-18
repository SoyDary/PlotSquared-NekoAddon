
package com.github.SoyDary.PlotSquaredNekoAddon.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.github.SoyDary.PlotSquaredNekoAddon.Gui.PlotsMenu;
import com.github.SoyDary.PlotSquaredNekoAddon.Objects.NekoPlot;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.ItemUtils;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums.MenuType;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums.ProfileColor;
import com.plotsquared.core.events.TeleportCause;

public class InventoryListener implements Listener {
	
	PSNA plugin;
	
	public InventoryListener(PSNA plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(!(e.getInventory().getHolder() instanceof PlotsMenu menu)) return;
		if(e.getClickedInventory() == null) return;
		Inventory inv = e.getView().getTopInventory();
		ItemStack item = e.getCurrentItem();
		Player p = (Player) e.getWhoClicked();
		if(e.getClickedInventory().equals(inv)) {			
			e.setCancelled(true);
			if(item == null) return;		
			String tag = ItemUtils.getData(item, "plot_item");
			if(tag.equals("FILLER")) {	
				if(menu.owner == null || !menu.owner.equals(p.getUniqueId())) return;
				ProfileColor color = Enums.getNext(ProfileColor.valueOf(inv.getItem(0).getType().name().split("_STAINED_GLASS_PANE")[0]));
			    plugin.getData().setProfileColor(menu.owner.toString(), color);
				PlotsMenu nm = new PlotsMenu(p, menu.owner, menu.type, menu.page);
				p.openInventory(nm.getInventory());	
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 4, 1);
				return;		
			}
			if(tag.equals("OWNED_PLOTS")) {	
				menu = new PlotsMenu(p, p.getUniqueId(), MenuType.Owned, 0);
				p.openInventory(menu.getInventory());	
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
				return;
			}
			if(tag.equals("TRUSTED_PLOTS")) {	
				menu = new PlotsMenu(p, p.getUniqueId(), MenuType.Trusted, 0);
				p.openInventory(menu.getInventory());	
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
				return;
			}
			if(tag.equals("NEXT_PAGE")) {	
				menu = new PlotsMenu(p, menu.owner, menu.type, menu.page+1);
				p.openInventory(menu.getInventory());	
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
				return;
			}
			if(tag.equals("PREV_PAGE")) {	
				menu = new PlotsMenu(p, menu.owner, menu.type, menu.page-1);
				p.openInventory(menu.getInventory());	
				p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
				return;
			}
			if(tag.equals("null")) return;
			NekoPlot nekoplot = new NekoPlot(tag);
			if(nekoplot.plot == null) return;
			ItemStack cursor = e.getCursor();				
			if(menu.type == MenuType.Owned && (menu.owner.equals(p.getUniqueId()) || p.hasPermission("nekoplots.admin"))) {
				if(e.getClick().name().equals("SHIFT_RIGHT")) {	
					String mainPlot = menu.mainPlot == null ? "" : menu.mainPlot.path;
					if(!mainPlot.equals(tag)) {
						plugin.getData().setMainPlot(menu.owner.toString(), tag);
						PlotsMenu nm = new PlotsMenu(p, menu.owner, MenuType.Owned, menu.page);
						p.openInventory(nm.getInventory());	
						p.sendMessage(plugin.getUtils().color("&8[&6P2&8] &7Parcela seleccionada como principal."));
						p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
					} else {
						plugin.getData().setMainPlot(menu.owner.toString(), null);
						PlotsMenu nm = new PlotsMenu(p, menu.owner, MenuType.Owned, menu.page);
						p.openInventory(nm.getInventory());		
						p.sendMessage(plugin.getUtils().color("&8[&6P2&8] &7Parcela eliminada como principal."));
						p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 2, 1);
					}
					return;
				}
				if(e.getClick().name().startsWith("SHIFT_LEFT")) {
					p.sendMessage(plugin.getUtils().color("&#ff8000&m                                                            "));
					p.sendMessage(plugin.getUtils().color("&#ffff66↓ &#ffbf00&lEscribe un nombre para la parcela &#ffff66↓"));
					p.sendMessage(plugin.getUtils().color("&#ffa64d↓      &7&o(Usa &#e6e6e6&o-remove &7&opara reestablecer)      &#ffa64d↓"));
					p.sendMessage(plugin.getUtils().color(""));
					closeInventory(p);
					plugin.getDataManager().plotNaming.put(p, nekoplot);				
					return;
				}
				if(cursor != null && !cursor.getType().name().equals("AIR")) {	
					nekoplot.setItem(cursor);
					PlotsMenu nm = new PlotsMenu(p, menu.owner, MenuType.Owned, menu.page);
					p.openInventory(nm.getInventory());		
					p.playSound(p.getLocation(), Sound.ITEM_BUNDLE_INSERT, 5, 1);
					return;
				}
			}
			
			TeleportCause cause = TeleportCause.COMMAND_VISIT;
			if(MenuType.Owned == menu.type && menu.owner.equals(p.getUniqueId())) cause = TeleportCause.COMMAND_HOME;
			nekoplot.teleportPlayer(p, cause);
			
			return;
		}
	}	
	
	@EventHandler
	public void swapItem(InventoryClickEvent e) {
		if(!(e.getInventory().getHolder() instanceof PlotsMenu)) return;
		if(e.getClick() != ClickType.SWAP_OFFHAND) return;		
		e.setCancelled(true);	
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {		
		if(!(e.getInventory().getHolder() instanceof PlotsMenu)) return;
		plugin.getData().offhandItems.put((Player) e.getPlayer(), e.getPlayer().getInventory().getItem(40));
		
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {	
		if(!(e.getInventory().getHolder() instanceof PlotsMenu)) return;
		if(!plugin.getData().offhandItems.containsKey(e.getPlayer())) return;
		e.getPlayer().getInventory().setItem(40, plugin.getData().offhandItems.get(e.getPlayer()));
		plugin.getData().offhandItems.remove(e.getPlayer());
	}
	
	private void closeInventory(Player p) {			
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				p.closeInventory();
			}
			
		}, 2L);
	}
}