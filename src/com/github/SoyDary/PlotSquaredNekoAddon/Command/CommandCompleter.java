package com.github.SoyDary.PlotSquaredNekoAddon.Command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.plotsquared.core.util.TabCompletions;

public class CommandCompleter implements TabCompleter{
	
	PSNA plugin;
	
	public CommandCompleter(PSNA plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String l, String[] a) {
		if(cmd.getName().equals("plotsquared-nekoaddon")) {
			if (a.length == 1) {
		        List<String> commandsList = new ArrayList<>();
		        List<String> preCommands = new ArrayList<>();
		        if(s.hasPermission("nekoplots.admin")) {
		        	commandsList.add("reload");
		        	commandsList.add("test");
		        	commandsList.add("getskull");
		        }
		        for (String text : commandsList) {
		          if (text.toLowerCase().startsWith(a[0].toLowerCase()))
		            preCommands.add(text); 
		        } 
		        return preCommands;	
			} 
			if (a.length == 2) {
				if(a[0].equalsIgnoreCase("getskull")) {
					if(!(s instanceof Player p)) return null;
			        List<String> commandsList = new ArrayList<>();
			        List<String> preCommands = new ArrayList<>();
					
			        if(p.hasPermission("nekoplots.admin")) {
						for(com.plotsquared.core.command.Command c : TabCompletions.completePlayers(plugin.plotsAPI.wrapPlayer(p.getUniqueId()), a[1], List.of("*"))) 
							if(!c.toString().contains("*")) commandsList.add(c.toString());
			        }
			        for (String text : commandsList) {
			          if (text.toLowerCase().startsWith(a[1].toLowerCase()))
			            preCommands.add(text); 
			        } 
			        return preCommands;	
				}
			} 
			
		}
		if(cmd.getName().equals("starboard")) {
			if (a.length == 1) {
		        List<String> commandsList = new ArrayList<>();
		        List<String> preCommands = new ArrayList<>();
		        commandsList.add("alerts");
		        for (String text : commandsList) {
		          if (text.toLowerCase().startsWith(a[0].toLowerCase()))
		            preCommands.add(text); 
		        } 
		        return preCommands;	
			}
		}

		return null;
	}

}
