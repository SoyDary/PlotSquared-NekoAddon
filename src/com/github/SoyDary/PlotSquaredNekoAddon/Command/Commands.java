package com.github.SoyDary.PlotSquaredNekoAddon.Command;

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
import com.plotsquared.core.configuration.adventure.text.minimessage.Template;
import com.plotsquared.core.configuration.caption.TranslatableCaption;
import com.plotsquared.core.player.PlotPlayer;
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
					return remoteLike(s, a);
				}
				}
			}	
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
		return false;
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
		Boolean state = plugin.getData().getLikeMessageState(p.getUniqueId().toString());;
		if(state) {
			plugin.getData().setLikeMessgeState(p.getUniqueId().toString(), false);
			p.sendMessage(plugin.getUtils().component(plugin.messages.getString("LIKE_MESSAGE_STATE_ENABLED"), p, null));
		} else {
			plugin.getData().setLikeMessgeState(p.getUniqueId().toString(), true);
			p.sendMessage(plugin.getUtils().component(plugin.messages.getString("LIKE_MESSAGE_STATE_DISABLED"), p, null));
		}
		return true;
	}
	private boolean remoteLike(CommandSender s, String[] a) {
		if(!(s instanceof Player p) || !p.hasPermission("plots.rate")) return true; 
		String arg = a[1];
		PlotPlayer<?> player = plugin.plotsAPI.wrapPlayer(p.getUniqueId());	
		NekoPlot nekoplot = new NekoPlot(arg);	
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
	    nekoplot.plot.addRating(p.getUniqueId(), rating);
		return true;
		
	}
	private boolean test(CommandSender s, String[] a) {
		Player p = (Player)s;
		PlotsMenu menu = new PlotsMenu(p, p.getUniqueId(),MenuType.Owned, 1);
		p.openInventory(menu.getInventory());
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

