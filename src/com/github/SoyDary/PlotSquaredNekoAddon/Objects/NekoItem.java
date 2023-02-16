package com.github.SoyDary.PlotSquaredNekoAddon.Objects;

import org.bukkit.inventory.ItemStack;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;

public class NekoItem {
	
	private static PSNA plugin = PSNA.getInstance();
	
	public static ItemStack TRUSTED_PLOTS = plugin.getDataManager().guiItems.get(ItemKey.TRUSTED_PLOTS);  
	public static ItemStack OWNED_PLOTS = plugin.getDataManager().guiItems.get(ItemKey.OWNED_PLOTS); 
	public static ItemStack STARBOARD = plugin.getDataManager().guiItems.get(ItemKey.STARBOARD);
	  
	public enum ItemKey {
		TRUSTED_PLOTS,
		OWNED_PLOTS,
		STARBOARD;
	}
}
