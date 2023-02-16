package com.github.SoyDary.PlotSquaredNekoAddon.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.github.SoyDary.PlotSquaredNekoAddon.PSNA;

public class ItemUtils {
	
	private static PSNA plugin = PSNA.getInstance();
	
	public static String getData(ItemStack item, Object data) {
		if(item == null || !item.hasItemMeta()) return "null";
		NamespacedKey key = data instanceof NamespacedKey ? (NamespacedKey)data : new NamespacedKey(plugin, (String)data);
		ItemMeta itemMeta = item.getItemMeta();
		PersistentDataContainer tagContainer = itemMeta.getPersistentDataContainer();
		if(tagContainer.has(key, PersistentDataType.STRING)) {
			return tagContainer.get(key, PersistentDataType.STRING);
		} else {
			return "null";
		}
	}
	
	public static void setData(ItemStack item, String data, Object value) {
		ItemMeta im = item.getItemMeta();
		NamespacedKey spaceKey = new NamespacedKey(plugin, data);
		im.getPersistentDataContainer().set(spaceKey, PersistentDataType.STRING, value+"");	
		item.setItemMeta(im);
	}
	
	
	public static String itemToBase64(ItemStack item) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeObject(item);
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			return null;
		}

	}
	
	public static ItemStack itemFromBase64(String data) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack item  = (ItemStack) dataInput.readObject();
			return item;
		} catch (Exception e) {
			return new ItemStack(Material.GRASS_BLOCK);
		}
	}


}
