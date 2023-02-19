package com.github.SoyDary.PlotSquaredNekoAddon.Objects;


import org.bukkit.entity.Player;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;
import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.util.query.PlotQuery;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPI_Extension extends PlaceholderExpansion {

    private PSNA plugin;

    public PAPI_Extension(PSNA plugin) {
    	this.plugin = plugin;
    }
 
    @Override
    public boolean persist(){
        return true;
    }
   
    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().get(0);
    }
   
    @Override
    public String getIdentifier(){
        return "nekoplots";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }   
    
	@Override
    public String onPlaceholderRequest(Player p, String id) {
		if(id.equals("follow_state")) {
			return ""+plugin.getData().getFollowState(p.getUniqueId().toString());
		}
		if(id.startsWith("starboard_")) {
			if(p == null) return null;
			String tag = id.split("starboard_")[1];
			switch(tag.toLowerCase()) {
			case "alerts":{
				return ""+plugin.getData().getLikeMessageState(p.getUniqueId().toString());
			}
			case "total":{
				PlotQuery query = PlotQuery.newQuery().ownedBy(p.getUniqueId()).whereBasePlot();
				Integer ammount = 0;
				for(Plot plot : query.asList()) {
					NekoPlot nekoplot = new NekoPlot(plot);
					ammount = ammount + nekoplot.getLikes();
				}
				return ""+ammount;
			}
			}
			return null;
		}
		if(id.startsWith("currentplot_")) {
			if(p == null ) return null;
			String tag = id.split("currentplot_")[1];
			PlotPlayer<?> pp = BukkitUtil.adapt(p);
			Plot plot = pp.getCurrentPlot();
			NekoPlot nekoplot = (plot == null || plot.getOwners().isEmpty()) ? null : new NekoPlot(plot);
			switch(tag.toLowerCase()) {
			case "plot-likes":{
				if(nekoplot == null) return "0";
				return ""+nekoplot.getLikes();
			}

			}
			return null;

		}
		return null;
    }
}