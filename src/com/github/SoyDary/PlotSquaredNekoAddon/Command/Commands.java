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
		return true;
	}

}
