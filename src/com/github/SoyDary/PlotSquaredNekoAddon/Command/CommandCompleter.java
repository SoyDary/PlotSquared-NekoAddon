package com.github.SoyDary.PlotSquaredNekoAddon.Command;

import java.util.ArrayList;
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
		if (a.length == 1) {
	        List<String> commandsList = new ArrayList<>();
	        List<String> preCommands = new ArrayList<>();
	        if(s.hasPermission("nekoplots.admin")) {
	        	commandsList.add("reload");
	        }
	        for (String text : commandsList) {
	          if (text.toLowerCase().startsWith(a[0].toLowerCase()))
	            preCommands.add(text); 
	        } 
	        return preCommands;
			
		}
		return null;
	}

}
