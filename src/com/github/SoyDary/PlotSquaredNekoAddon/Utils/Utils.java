package com.github.SoyDary.PlotSquaredNekoAddon.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.earth2me.essentials.User;
import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import  com.github.SoyDary.PlotSquaredNekoAddon.Objects.NekoPlot;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.ComponentUtil.ComponentSection;
import com.github.SoyDary.PlotSquaredNekoAddon.Utils.ComponentUtil.CustomComponent;
import com.plotsquared.core.plot.Plot;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.format.TextDecoration.State;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class Utils {
	PSNA plugin;
	private LegacyComponentSerializer lcs;
	private TextReplacementConfig italic;
	private List<Material> validItems;
	private Pattern hex_parse_pattern = Pattern.compile("&x(&[A-Fa-f0-9]){6}");
	
	public Utils(PSNA plugin) {
		this.plugin = plugin;
		this.lcs = LegacyComponentSerializer.builder().character('&').hexCharacter('#').extractUrls().build();
		this.italic = TextReplacementConfig.builder().match("&<ITALITC> ").replacement("").build();
		loadItems();
	}
	
	public Component color(String text) {
		if(text == null) return Component.text("");
		text = fixColors(text);
		if(text.split(" ").length > 1 && text.split(" ")[0].contains("&o")) text = text.replaceAll("&o", "&<ITALITC> &o");
		return lcs.deserialize(text).decoration(TextDecoration.ITALIC, false).replaceText(italic);
	}
	
	public List<Component> color(List<String> list) {
		List<Component> components = new ArrayList<Component>();
		for(String str : list) {
			components.add(color(str));
		}
		return components;	
	}
	public Component color(String text, Player p) {
		if(text == null) return Component.text("");
		text = text.replaceAll("§", "&");
		text = fixColors(text);
		text = parseHex(text);	
		text = PlaceholderAPI.setPlaceholders(p, text);
		return lcs.deserialize(text);
		
	}
	public Component color(String text, Player p, Plot plot) {
		if(text == null) return Component.text("");
		text = text.replaceAll("§", "&");
		text = fixColors(text);
		text = parseHex(text);	
		text = PlaceholderAPI.setPlaceholders(p, text);
		if(plot != null) {
			NekoPlot nekoplot = new NekoPlot(plot);
			text = nekoplot.setPlaceholders(p, text, nekoplot.isMainPlot(), false);
		}
		return lcs.deserialize(text);
		
	}
	public Component component(String text, Player p, Plot plot) {
		if(text == null) return Component.text("");
		text = text.replaceAll("§", "&");
		text = fixColors(text);
		text = parseHex(text);	
		if(plot != null) {
			NekoPlot nekoplot = new NekoPlot(plot);
			text = nekoplot.setPlaceholders(p, text, nekoplot.isMainPlot(), false);
		}
		Builder builder = Component.text();
		Boolean postBar = false;
		TextColor lastColor = null;
		Map<TextDecoration, State> lastdecorations = new HashMap<TextDecoration, State>();
		for(ComponentSection section : ComponentUtil.breakText(text)) {
			text = PlaceholderAPI.setPlaceholders(p, section.getText());
			Component component = lcs.deserialize(text);
			List<Component> subComponents = component.children();
			Component colorComponent = component;
			TextColor color = component.color();
			if(color == null) {
				for(int i = subComponents.size()-1; i >= 0; i--) {
					Component sc = subComponents.get(i);
					if(sc.color() == null) continue;
					color = sc.color(); 
					colorComponent = sc;
					break;
				}
			}
			String msg = colorComponent.toString();
			Map<TextDecoration, State> decorations = new HashMap<TextDecoration, State>();
			for(TextDecoration decoration : TextDecoration.class.getEnumConstants()) {
				if(msg.contains(decoration.toString()+"=true")) decorations.put(decoration, State.TRUE);
				if(msg.contains(decoration.toString()+"=false")) decorations.put(decoration, State.FALSE);
			}
			if(color != null) {
				lastColor = color;
				lastdecorations = decorations;
			} else {
				for(TextDecoration decoration : decorations.keySet()) {
					lastdecorations.put(decoration, decorations.get(decoration));
				}
			}
			Builder b = Component.text();
			if(section.isVar()) {
				CustomComponent custom = plugin.getDataManager().customComponents.get(section.getText());
				if(custom != null) component = custom.getComponent(p, plot);		
				if(color == null) b.color(lastColor);
				if(color == null && decorations.isEmpty()) b.decorations(lastdecorations);
				postBar = true;
			} else {
				if(postBar) {				
					if(color == null) {
						b.color(lastColor);
						b.decorations(lastdecorations);
					}
					postBar = false;			
				}			
			}
			b.append(component);
			builder.append(b.build());		
		}
		return builder.build();

	}

	public OfflinePlayer getUser(String str) {	
		if(str == null || str.equals("")) return null;
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(p.getName().equalsIgnoreCase(str)) return Bukkit.getOfflinePlayer(p.getUniqueId());
		}
		OfflinePlayer of = Bukkit.getOfflinePlayer(str);
		if(plugin.essentials == null && !of.hasPlayedBefore()) {
			return of;
		}
		
		if(str.length() == 36) {
			User user = plugin.essentials.getUser(UUID.fromString(str));
			if(user != null) {
				return Bukkit.getPlayer(user.getUUID());
			}
		}
		User user = plugin.essentials.getUser(str);
		if(user != null) {
			return Bukkit.getOfflinePlayer(user.getUUID());
		}
		return null;
		
	}
	
	private String fixColors(String text) {
		return 	text = text
					.replaceAll("&A", "&a")
					.replaceAll("&B", "&b")
					.replaceAll("&C", "&c")
					.replaceAll("&D", "&d")
					.replaceAll("&E", "&e")
					.replaceAll("&F", "&f")
					.replaceAll("&L", "&l")
					.replaceAll("&M", "&m")
					.replaceAll("&N", "&n")
					.replaceAll("&O", "&o")
					.replaceAll("&K", "&K");
	}
	
	public ItemStack getRandomItem() {
		return new ItemStack(validItems.get((int) ((Math.random() * (validItems.size() - 1)) + 1)));
	}

    private String parseHex(String text) {
        String nText = "";
        int index = 0;
        Matcher matcher = hex_parse_pattern.matcher(text);   
         while(matcher.find()) {
             nText+= text.substring(index, matcher.start())+matcher.group().replaceAll("&x", "<HeX>").replaceAll("&", "").replaceAll("<HeX>", "&#");
             index = matcher.end();
         }
         return nText += text.substring(index, text.length());
    }
	
	private void loadItems() {
		this.validItems = new ArrayList<Material>();
		for(Material mat : Material.class.getEnumConstants()) {
			String name = mat.name();
			if(!mat.isItem() || name.startsWith("LEGACY_") || name.contains("GLASS_PANE")) continue;		
			if(name.contains("BAMBOO_") || name.contains("HANGING_SIGN") || name.equals("PIGLIN_HEAD") || name.contains("CAMEL")) continue;
			this.validItems.add(mat);
		}
	}
	
	public String getSkinID(Player p) {
		PlayerProfile profile = p.getPlayerProfile(); 
		if(profile == null || !profile.hasTextures()) return null;
        String id = new String(profile.getTextures().getSkin().toString()).split("texture/")[1];
        return id;    
    }
	public ItemStack getHead(String texture, UUID uuid) {
		if(texture.length() < 80) texture = "http://textures.minecraft.net/texture/"+texture;
		ItemStack head = new ItemStack(Material.PLAYER_HEAD);
	    SkullMeta headMeta = (SkullMeta) head.getItemMeta();
	    PlayerProfile profile = Bukkit.createProfile(uuid);
	    String encodedData = Base64Coder.encodeString(String.format("{textures:{SKIN:{url:\"%s\"}}}", texture));
	    profile.setProperty(new ProfileProperty("textures", encodedData));
	    headMeta.setPlayerProfile(profile);
	    head.setItemMeta(headMeta);    
	    return head;
	}
	
	public ItemStack getHeadfromUrl(String url) {
	    ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
	    if(url.isEmpty()) return head;	   
	    SkullMeta headMeta = (SkullMeta) head.getItemMeta();
	    PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
	    String encodedData = Base64Coder.encodeString(String.format("{textures:{SKIN:{url:\"%s\"}}}", url));
	    profile.setProperty(new ProfileProperty("textures", encodedData));
	    headMeta.setPlayerProfile(profile);
	    head.setItemMeta(headMeta);    
	    return head;
	}
	
	public void setNaming(Player p, Component message) {
		String name = parseHex(LegacyComponentSerializer.legacyAmpersand().serialize(message));
	    if (PlainTextComponentSerializer.plainText().serialize(message).length() >= 35) {
		       p.sendMessage(plugin.getUtils().color("&8[&6P2&8] &7Introduce un nombre más corto."));
		       return;
	    }
	    NekoPlot np = plugin.getDataManager().plotNaming.get(p);
	    np.tempNaming = name;
	    if(name.equals("-remove")) {
	    	plugin.getDataManager().plotNaming.remove(p);
	    	np.setCustomName(null);
			p.sendMessage(plugin.getUtils().component(plugin.messages.getString("PLOT_NAMING_DELETED"), p, null));
			return;
	    }
	    Builder builder = Component.text().append(plugin.getUtils().color("&#ff8000&l› &#ff9933&l› &#ffbf80&l› &7\""));
	    builder.append(plugin.getUtils().color(np.setPlaceholders(p, plugin.config.getString("PlotsMenu.Items.PlotSelector.name").replaceAll("%plot-customname%", name), false, false)).hoverEvent(Component.text(name).color(NamedTextColor.GRAY)));
	    builder.append(plugin.getUtils().color("&7\" "));
	    builder.append(plugin.getUtils().color("&8[&#33ff33✔&8]").hoverEvent(plugin.getUtils().color("&a&oAceptar")).clickEvent(ClickEvent.runCommand("/psna plotname accept")));
	    builder.append(plugin.getUtils().color(" &8[&#ff1a1a✖&8]").hoverEvent(plugin.getUtils().color("&cCancelar")).clickEvent(ClickEvent.runCommand("/psna plotname cancel")));
	    p.sendMessage(builder.build());    
	}
	
}