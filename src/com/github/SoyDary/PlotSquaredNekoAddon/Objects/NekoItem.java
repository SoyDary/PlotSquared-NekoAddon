package com.github.SoyDary.PlotSquaredNekoAddon.Objects;

import org.bukkit.inventory.ItemStack;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;

public class NekoItem {
	
	private static PSNA plugin = PSNA.getInstance();
	
	public static ItemStack TRUSTED_PLOTS = plugin.getDataManager().guiItems.get(ItemKey.TRUSTED_PLOTS);  
	public static ItemStack OWNED_PLOTS = plugin.getDataManager().guiItems.get(ItemKey.OWNED_PLOTS); 
	public static ItemStack STARBOARD = plugin.getDataManager().guiItems.get(ItemKey.STARBOARD);
	public static ItemStack NEXT_PAGE = plugin.getDataManager().guiItems.get(ItemKey.NEXT_PAGE);
	public static ItemStack PREV_PAGE = plugin.getDataManager().guiItems.get(ItemKey.PREV_PAGE);
	  
	public enum ItemKey {
		TRUSTED_PLOTS,
		OWNED_PLOTS,
		STARBOARD,
		NEXT_PAGE,
		PREV_PAGE;
	}
}
