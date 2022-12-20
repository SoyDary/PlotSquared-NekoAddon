package com.github.SoyDary.PlotSquaredNekoAddon.Command;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;

public class CommandCompleter implements TabCompleter{
	
	PSNA plugin;
	
	public CommandCompleter(PSNA plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String l, String[] a) {
		return null;
	}

}
