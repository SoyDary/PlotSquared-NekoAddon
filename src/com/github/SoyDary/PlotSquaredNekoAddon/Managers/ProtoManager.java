package com.github.SoyDary.PlotSquaredNekoAddon.Managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;

import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class ProtoManager {

	PSNA plugin;
	com.comphenix.protocol.ProtocolManager manager;
	public Collection<UUID> tempPlayers;
	
	public ProtoManager(PSNA plugin) {
		this.plugin = plugin;
		this.manager = ProtocolLibrary.getProtocolManager();
		tempPlayers = new ArrayList<UUID>();
		chatManager();
	}
	
	void chatManager() {
	    manager.addPacketListener(new PacketAdapter(plugin, PacketType.Play.Server.SYSTEM_CHAT)
	    {
	        @Override
	        public void onPacketSending(PacketEvent event)
	        {     
	        	PacketContainer  packet = event.getPacket();
	        	if(packet.getStrings() == null) return;
	        	if(packet.getStrings().size() == 0) return;
	        	String text = packet.getStrings().read(0);
	        	if(text == null) return;
	        	Player p = event.getPlayer();
	        	String msg = ChatColor.stripColor(BaseComponent.toLegacyText(ComponentSerializer.parse(text)));
	        	if(msg.contains("Modo de juego de "+p.getName()+" cambiado") && tempPlayers.contains(p.getUniqueId())) {
	        		event.setCancelled(true);
	        		tempPlayers.remove(p.getUniqueId());
	        	}
	        }
	    });
		
	}
	

	
	

}
