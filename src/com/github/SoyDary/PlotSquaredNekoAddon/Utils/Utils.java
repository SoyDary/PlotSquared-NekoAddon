package com.github.SoyDary.PlotSquaredNekoAddon.Utils;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Utils {
	PSNA plugin;
	LegacyComponentSerializer lcs;
	
	public Utils(PSNA plugin) {
		this.plugin = plugin;
		lcs = LegacyComponentSerializer.builder().character('&').hexCharacter('#').extractUrls().build();
	}
	public TextComponent color(String str) {
		return lcs.deserialize(str);
	}

}