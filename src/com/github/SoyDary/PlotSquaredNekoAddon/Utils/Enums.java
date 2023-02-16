package com.github.SoyDary.PlotSquaredNekoAddon.Utils;

public class Enums {
	
	public enum MenuType {
		Owned,
		Trusted,
		StarBoard
	}
	
	public enum ClickAction {
		SUGGEST_TEXT,
		SUGGEST_COMMAND,
		RUN_COMMAND,
	    COPY_TO_CLIPBOARD;		
	}
	
	public enum ProfileColor {	
		WHITE("#ffffff"),
		//LIGHT_GRAY("#a6a6a6"),
		//GRAY("#666666"),
		BLACK("#404040"),
		BROWN("#c68c53"),
		RED("#ff1a1a"),
		ORANGE("#ffbf00"),
		YELLOW("#ffff4d"),
		LIME("#8cff1a"),
		GREEN("#009900"),
		CYAN("#00cccc"),
		LIGHT_BLUE("#1affff"),
		BLUE("#1a1aff"),
		PURPLE("#c61aff"),
		MAGENTA("#ff4dd2"),
		PINK("#ff99ff");
				
		public final String color;
		
		ProfileColor(String color) {
			this.color = color;
		}
	
	}
	public static ProfileColor getNext(ProfileColor color) {
		boolean next = false;
		ProfileColor finalColor = null;
		while(finalColor == null) {
			for(ProfileColor profile : ProfileColor.class.getEnumConstants()) {
				if(next) {
					finalColor = profile;
					break;
				}
				if(profile.equals(color)) next = true;
			}
		}
		return finalColor;
	}


}
