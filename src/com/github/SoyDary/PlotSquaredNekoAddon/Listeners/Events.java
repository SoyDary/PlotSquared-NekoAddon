package com.github.SoyDary.PlotSquaredNekoAddon.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.server.TabCompleteEvent;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.github.SoyDary.PlotSquaredNekoAddon.Gui.PlotsMenu;
import com.github.SoyDary.PlotSquaredNekoAddon.Objects.NekoPlot;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums.MenuType;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.command.Command;
import com.plotsquared.core.database.DBFunc;
import com.plotsquared.core.events.TeleportCause;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.flag.implementations.FlyFlag;
import com.plotsquared.core.plot.flag.implementations.GamemodeFlag;
import com.plotsquared.core.plot.flag.implementations.GuestGamemodeFlag;
import com.plotsquared.core.util.TabCompletions;
import io.papermc.paper.event.player.AsyncChatEvent;

public class Events implements Listener{
	
	PSNA plugin;
	
	public Events(PSNA plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		plugin.getData().saveSkin(p);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent  e) {
		Player p = e.getPlayer();
		plugin.getData().saveSkin(p);
		if(plugin.getDataManager().plotNaming.containsKey(p)) plugin.getDataManager().plotNaming.remove(p);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncChatEvent e) {
		Player p = e.getPlayer();
		if(!plugin.getDataManager().plotNaming.containsKey(p)) return;
		e.setCancelled(true);
		plugin.getUtils().setNaming(p, e.message());
		
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {	
		if(e.getMessage().startsWith("/plots")) {
			Player p = e.getPlayer();	
			if(e.getMessage().equals("/plots")) {
				e.setCancelled(true);	
				PlotsMenu menu = new PlotsMenu(p, p.getUniqueId(), MenuType.Owned, 0);
				if(menu.plots.isEmpty()) {
					p.sendMessage(plugin.getUtils().component(plugin.messages.getString("PLAYER_NO_PLOTS"), p, null));
					return;
				}
				p.openInventory(menu.getInventory());		
				return;
			}
			if(e.getMessage().startsWith("/plots ")) {
				e.setCancelled(true);	
				String[] a = e.getMessage().split("/plots ")[1].split(" ");
				OfflinePlayer of = plugin.getUtils().getUser(a[0]);
				if(of == null) {
	            	p.sendMessage(plugin.getUtils().component(plugin.messages.getString("PLAYER_NOT_FOUND").replaceAll("%player%", a[0]), p, null));
	            	return;		   
				}
				PlotsMenu menu = new PlotsMenu(p, of.getUniqueId(), MenuType.Owned, 0);
				if(menu.plots.isEmpty()) {
					p.sendMessage(plugin.getUtils().component(plugin.messages.getString("NO_PLOTS"), p, null));
					return;
				}
				p.openInventory(menu.getInventory());	
			}
		}
		if(e.getMessage().matches("^/(p|plot|ps|plotsquared|p2|2|plotme) .*$")) {
			Player p = e.getPlayer();	
			String message = e.getMessage();
			String[] a = message.split(" ");
			if(a[1].toLowerCase().equals("home") | a[1].toLowerCase().equals("h")) {
				if(a.length == 2) {
					PlotPlayer<?> pp = plugin.plotsAPI.wrapPlayer(p.getUniqueId());
					if(pp == null || pp.getPlots().size() == 0) return;
					NekoPlot mainplot = plugin.getData().getMainPlot(pp.getUUID().toString());
					if(mainplot == null || !mainplot.canJoin(p)) return;
					mainplot.teleportPlayer(p, TeleportCause.COMMAND_HOME);
					e.setCancelled(true);
				}
				return;
			}
			if(a[1].toLowerCase().equals("visit") | a[1].toLowerCase().equals("v")) {
				if(a.length == 3) {
					String name = a[2];
					OfflinePlayer of = plugin.getUtils().getUser(name);
					if(of == null) return;
	            	NekoPlot mainplot = plugin.getData().getMainPlot(of.getUniqueId().toString());
						if(mainplot == null || !mainplot.canJoin(p)) return;
						mainplot.teleportPlayer(p, TeleportCause.COMMAND_VISIT);
						e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onTabComplete(TabCompleteEvent e) {
		if(e.getBuffer().startsWith("/plots ")) {
			if(!(e.getSender() instanceof Player p)) return;
			String[] args = e.getBuffer().split("/plots ");
			String a = args.length == 0 ? "": args[1];		
			List<Command> commands = TabCompletions.completePlayers(plugin.plotsAPI.wrapPlayer(p.getUniqueId()), a, List.of("*"));
			List<String> result = new ArrayList<>();
			for(Command c : commands) {
				if(!c.toString().contains("*")) result.add(c.toString());
			}
			 e.setCompletions(result);
		}
	}

	
	
    @EventHandler
	public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
		Player p = e.getPlayer();	
		Plot plot =  BukkitUtil.adapt(p).getCurrentPlot();
		if(plot == null) return;
		if(plot.getOwners().contains(DBFunc.SERVER)) return;
		if(!plot.getFlag(FlyFlag.class).name().equals("DISABLED")) return;
		if(plot.isOwner(p.getUniqueId()) || plot.isAdded(p.getUniqueId()) || p.hasPermission("plots.admin.flight")) return;
	    e.setCancelled(true);
	    p.setAllowFlight(false);	
	}
    
	@EventHandler
	public void onPlayerGameModeChange(PlayerGameModeChangeEvent e) {
		if(!plugin.getDataManager().config.getBoolean("GamemodeSettings.enabled")) return;
		Player p = e.getPlayer();
		Plot plot =  BukkitUtil.adapt(p).getCurrentPlot();
		if(plot == null) return;
		if(plot.getOwners().contains(DBFunc.SERVER)) return;
		int plotModes = 0;
		String guest_gamemode = plot.getFlag(GuestGamemodeFlag.class).toString();
		String gamemode = plot.getFlag(GamemodeFlag.class).toString();
		if(!guest_gamemode.equals("default")) plotModes++;
		if(!gamemode.equals("default")) plotModes++;		
		if(plotModes == 0) return;
		String newmode = e.getNewGameMode().name().toLowerCase();
		if(plotModes == 1) {
			if(!guest_gamemode.equals("default")) {
				checkCancelled(e, p, plot, newmode, guest_gamemode);
				return;
			}
			if(!gamemode.equals("default")) {
				checkCancelled(e, p, plot, newmode, gamemode);
				return;
			}
		} 
		if(plotModes == 2) {
			if(plugin.getDataManager().tempPlayers.contains(p.getUniqueId()) && newmode.equals(gamemode)) return;	
			checkCancelled(e, p, plot, newmode, guest_gamemode);

		}
	}

	public void checkCancelled(PlayerGameModeChangeEvent e, Player p, Plot plot, String mode, String plotMode) {
		if(!mode.equals(plotMode)) {
			if(plot.isOwner(p.getUniqueId()) || plot.isAdded(p.getUniqueId()) || plot.getMembers().contains(p.getUniqueId()) || p.hasPermission("plots.gamemode.bypass")) return;
			p.sendActionBar(plugin.getUtils().color(plugin.getDataManager().config.getString("GamemodeSettings.message")));
			p.
			playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.MASTER,1, 1);
			if(plugin.protocolmanager != null ) plugin.protocolmanager.tempPlayers.add(p.getUniqueId());
			e.setCancelled(true);
		}
	}
	
	
}
