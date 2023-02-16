package com.github.SoyDary.PlotSquaredNekoAddon.Utils;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.github.SoyDary.PlotSquaredNekoAddon.Objects.NekoPlot;
import com.plotsquared.core.plot.Plot;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;

public class ComponentUtil {
	
	private static PSNA plugin = PSNA.getInstance();
	
	public static ArrayList<ComponentSection> breakText(String text) {
		ArrayList<ComponentSection> vars = new ArrayList<ComponentSection>();
		String cacheText = "";
		String cacheVar = "";
		Boolean runningVar = false;
		for(int i = 0; i < text.length(); i++) {
			String c = text.charAt(i)+"";
			if(c.contains("%") && runningVar == false) {
				runningVar = true;
				if(cacheText != null && cacheText != "") {
					ComponentSection section = new ComponentSection(cacheText);
					vars.add(section);
					cacheText = "";
				}
				continue;
			}
			if(runningVar) {
				cacheVar += text.charAt(i);
				if(text.charAt(i) == '%') {
					runningVar = false;
					String vv = "%"+cacheVar;
					ComponentSection section = new ComponentSection(vv);
					section.setVar();
					vars.add(section);
					cacheVar = "";
					continue;
				} 
				if(i == text.length()-1) {
					String vv = "%"+cacheVar;
					ComponentSection section = new ComponentSection(vv);
					vars.add(section);
				}
				
			}else {
				cacheText += text.charAt(i);
				if(i == text.length()-1) {
					ComponentSection section = new ComponentSection(cacheText);
					vars.add(section);
				}
			}
		}
		
		return vars;
	}
	
	

	
	public static class ComponentSection {
		
		String text;
		Boolean isVar = false;
		
		public ComponentSection(String text) {
			this.text = text;
		}

		public boolean isVar() {
			return this.isVar;
		}

		public String getText() {
			return this.text;
		}
		
		public void setText(String text) {
			this.text = text;
		}

		public void setVar() {
			this.isVar = true;
		}
	}
	
	public static class CustomComponent {
		
		public String key;
		public String label;
		public String clickAction = null;
		public String action;
		public String hover = null;
		
		public CustomComponent(String key, String label, String clickAction, String action, String hover) {		
			this.key = key;
			this.label = label;
			this.clickAction = clickAction;
			this.action = action;
			this.hover = hover;
		}
		
		public Component getComponent(Player p, Plot plot) {
			Builder builder = Component.text();
			Component component = plugin.getUtils().color(label, p, plot);
			String action = PlaceholderAPI.setPlaceholders(p, this.action);
			if(plot != null) {
				NekoPlot nekoplot = new NekoPlot(plot);
				action = nekoplot.setPlaceholders(p, action, nekoplot.isMainPlot(), false);
			}
			builder.append(component);	
			if(clickAction != null) {		
				switch(clickAction) {
					case "SUGGEST_COMMAND" : {
						builder.clickEvent(ClickEvent.suggestCommand(action));	
						break;
					}
					case "RUN_COMMAND" : {
						builder.clickEvent(ClickEvent.runCommand(action));	
						break;
					}
					case "COPY_TO_CLIPBOARD" : {
						builder.clickEvent(ClickEvent.copyToClipboard(action));	
						break;
					}
					case "SUGGEST_TEXT" : {
						builder.insertion(action);	
						break;
					}
					default : break;
				}	
			}	
			if(hover != null) builder.hoverEvent(plugin.getUtils().color(hover, p, plot));
					
			return builder.build();
		}
	}
}


