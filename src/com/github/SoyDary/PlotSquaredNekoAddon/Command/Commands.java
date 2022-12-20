package com.github.SoyDary.PlotSquaredNekoAddon.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;

public class Commands implements CommandExecutor {
	
	PSNA plugin;
	
	public Commands(PSNA plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd,  String l, String[] a) {
		if(a.length > 0) {
			switch(a[0].toLowerCase()) {
			case "reload":{
				return reload(s, a);
			}
			}
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

