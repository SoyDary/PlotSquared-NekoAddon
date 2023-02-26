package com.github.SoyDary.PlotSquaredNekoAddon.Command;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.github.SoyDary.PlotSquaredNekoAddon.Gui.PlotsMenu;
import com.github.SoyDary.PlotSquaredNekoAddon.Objects.NekoPlot;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.Enums.MenuType;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.configuration.adventure.text.minimessage.Template;
import com.plotsquared.core.configuration.caption.TranslatableCaption;
import com.plotsquared.core.events.TeleportCause;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.Rating;

public class Commands implements CommandExecutor {
	
	PSNA plugin;
	
	public Commands(PSNA plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd,  String l, String[] a) {
		if(cmd.getName().equals("plotsquared-nekoaddon")) {
			if(a.length > 0) {
				switch(a[0].toLowerCase()) {
				case "reload":{
					return reload(s, a);
				}
				case "test":{
					return test(s, a);
				}
				case "getskull":{
					return getSkull(s, a, l);
				}
				case "plotname":{
					return plotName(s, a);
				}
				case "like":{
					return remoteLike(cmd, s, a);
				}
				}
			}	
			return true;
		}
		if(cmd.getName().equals("star")) {
			if(!(s instanceof Player p)) return true; 
			remoteLike(cmd, s, a);
			return true;
		}
		if(cmd.getName().equals("starboard")) {
			if(!(s instanceof Player p)) return true; 
			if(a.length > 0) {
				switch(a[0].toLowerCase()) {
				case "alerts":{
					return starboardAlerts(s, a);
				}
				}
			}
			PlotsMenu menu = new PlotsMenu(p, MenuType.StarBoard);
			p.openInventory(menu.getInventory());
			return true;
		}
		if(cmd.getName().equals("togglefollow")) {
			if(!(s instanceof Player p)) return true; 
			return toggleFollow(p);
		}
		if(cmd.getName().equals("follow")) {
			return follow(s, a);
		}
		return false;
	}
	
	private boolean test(CommandSender s, String[] a) {
		s.sendMessage(plugin.getUtils().color(plugin.prefix+" &f"+plugin.getDescription().getVersion()));
		return true;
	}
	
	private boolean follow(CommandSender s, String[] a) {
		if(!(s instanceof Player p)) return true; 
		if(!p.hasPermission("nekoplots.follow")) {
			p.sendMessage("§cError: Permisos insuficientes.");
			return true;
		}
		if(a.length == 0) {
			p.sendMessage(plugin.getUtils().color("&fIntroduce el nombre de un jugador."));
			return true;
		}
		Player op = Bukkit.getPlayer(a[0]);
		if(op == null) {
			p.sendMessage(plugin.getUtils().color("&cError: &4Jugador no encontrado."));
			return true;
		}
		Boolean state = plugin.getData().getFollowState(op.getUniqueId().toString());
		if(!state && !p.hasPermission("nekoplots.follow.bypass")) {
			p.sendMessage(plugin.getUtils().component(plugin.messages.getString("FOLLOW_PLAYER_NOT_ALLOWED").replaceAll("%player%", op.getName()), op, null));
			return true;
		}
		Plot plot = BukkitUtil.adapt(op).getCurrentPlot();
		if(plot == null) {
			p.sendMessage(plugin.getUtils().component(plugin.messages.getString("FOLLOW_PLAYER_NOT_IN_PLOT").replaceAll("%player%", op.getName()), op, plot));
			return true;
		}
		NekoPlot nekoplot = new NekoPlot(plot);
		nekoplot.teleportPlayer(p, TeleportCause.COMMAND_VISIT);
		return true;
	}
	private boolean toggleFollow(Player p) {
		Boolean state = plugin.getData().getFollowState(p.getUniqueId().toString());
		if(state) {
			plugin.getData().setFollowState(p.getUniqueId().toString(), false);
			p.sendMessage(plugin.getUtils().component(plugin.messages.getString("FOLLOW_STATE_DISABLED"), p, null));
		} else {
			plugin.getData().setFollowState(p.getUniqueId().toString(), true);
			p.sendMessage(plugin.getUtils().component(plugin.messages.getString("FOLLOW_STATE_ENABLED"), p, null));
		}
		return true;
	}
	private boolean getSkull(CommandSender s, String[] a, String label) {
		if(!(s instanceof Player p)) return true;
		if(!p.hasPermission("nekoplots.admin")) {
			p.sendMessage("§cError: Permisos insuficientes.");
			return true;
		}
		if(a.length < 2) {
			s.sendMessage(plugin.getUtils().color(plugin.prefix+" &e/"+label+" getSkull <nombre>"));
			return true;
		}
		OfflinePlayer of = plugin.getUtils().getUser(a[1]);
		if(of == null) {
			s.sendMessage(plugin.getUtils().color(plugin.prefix+" &cJugador inválido: &7"+a[1]));
			return true;
		}
		ItemStack item = plugin.getData().getPlayerHead(of.getUniqueId().toString());
		p.getInventory().addItem(item);
		p.sendMessage(plugin.getUtils().color(plugin.prefix+" &aRecibiste la cabeza de &f"+of.getName()));
		return true;
	}
	
	private boolean starboardAlerts(CommandSender s, String[] a) {
		if(!(s instanceof Player p)) return true; 
		Boolean state = plugin.getData().getLikeMessageState(p.getUniqueId().toString());
		if(state) {
			plugin.getData().setLikeMessgeState(p.getUniqueId().toString(), false);
			p.sendMessage(plugin.getUtils().component(plugin.messages.getString("LIKE_MESSAGE_STATE_DISABLED"), p, null));
		} else {
			plugin.getData().setLikeMessgeState(p.getUniqueId().toString(), true);
			p.sendMessage(plugin.getUtils().component(plugin.messages.getString("LIKE_MESSAGE_STATE_ENABLED"), p, null));
		}
		return true;
	}
	
	private boolean remoteLike(Command command, CommandSender s, String[] a) {
		if(!(s instanceof Player p) || !p.hasPermission("plots.rate")) return true; 	
		boolean current = command.getName().equals("star") ? a.length == 0 : a.length == 1;
		PlotPlayer<?> player = plugin.plotsAPI.wrapPlayer(p.getUniqueId());	
		NekoPlot nekoplot = current ? new NekoPlot(player.getCurrentPlot()) : new NekoPlot(command.getName().equals("star") ? a[0] : a[1]);	

		if(nekoplot.plot == null) {
			player.sendMessage(TranslatableCaption.of("invalid.found_no_plots"), new Template[0]);
			return true;
		}
	    if (!nekoplot.plot.hasOwner()) {
	        player.sendMessage(TranslatableCaption.of("ratings.rating_not_owned"), new Template[0]);
	        return true;
	    }
	    if (nekoplot.plot.isOwner(player.getUUID())) {
	        player.sendMessage(TranslatableCaption.of("ratings.rating_not_your_own"), new Template[0]);
	        return true; 
	    }
	    if (nekoplot.plot.getLikes().containsKey(p.getUniqueId())) {
	        player.sendMessage(TranslatableCaption.of("ratings.rating_already_exists"), new Template[] { Template.of("plot", nekoplot.plot.getId().toString()) });
	        return true; 
	    }
	    player.sendMessage(TranslatableCaption.of("ratings.rating_applied"), new Template[] { Template.of("plot", nekoplot.plot.getId().toString()) });
	    Rating rating = new Rating(10);
	    plugin.plotsAPI.getPlotSquared().getEventDispatcher().callRating(player, nekoplot.plot, rating);
	    nekoplot.plot.addRating(p.getUniqueId(), rating);
		return true;
		
	}
	
	private boolean plotName(CommandSender s, String[] a) {
		if(!(s instanceof Player p)) return true;
		if(a.length < 2) return true;
		if(a[1].equalsIgnoreCase("accept")) {
			if(!plugin.getDataManager().plotNaming.containsKey(p)) return true;
			NekoPlot np = plugin.getDataManager().plotNaming.get(p);
			np.setCustomName(np.tempNaming);
			p.sendMessage("");
			p.sendMessage(plugin.getUtils().component(plugin.messages.getString("PLOT_NAMING_SUCCESFULL"), p, null));
			plugin.getDataManager().plotNaming.remove(p);
			return true;		
		}
		if(a[1].equalsIgnoreCase("cancel")) {
			if(!plugin.getDataManager().plotNaming.containsKey(p)) return true;
			p.sendMessage("");
			p.sendMessage(plugin.getUtils().component(plugin.messages.getString("PLOT_NAMING_CANCELLED"), p, null));
			plugin.getDataManager().plotNaming.remove(p);
			return true;
		}
		return true;
	}


	private boolean reload(CommandSender s, String[] a) {
		if(!s.hasPermission("nekoplots.admin")) {
			s.sendMessage("§cError: Permisos insuficientes.");
			return true;
		}
		s.sendMessage(plugin.getUtils().color(plugin.prefix+" &aPlugin recargado!"));
		plugin.reloadPlugin();
		return true;
	}
}

