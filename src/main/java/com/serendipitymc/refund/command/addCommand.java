package com.serendipitymc.refund.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.serendipitymc.refund.managers.RefundHandler;
import com.serendipitymc.refund.refund.refund;
import com.serendipitymc.refund.util.SSUtil;


public class addCommand  implements CommandExecutor {
	private refund plugin;
	private RefundHandler refunds;
	private SSUtil utils;

	public addCommand(refund instance) {
		plugin = instance;
		utils = plugin.getUtil();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (sender instanceof Player) {
			refunds = plugin.getRH();
			utils = plugin.getUtil();
			try {
				Integer refundAmount = refunds.countRefunds(sender.getName().toString().toLowerCase());
				if (!(refundAmount.equals(1))) {
					utils.sendMessageGG((Player) sender, "I can't find an active refund request for you");
					return true;
				}
			} catch (Exception e) {
				utils.sendMessageGG((Player) sender, "I can't find an active refund request for you");
				return true;
			}
			
				
			if (args.length > 3 || args.length < 2) {
				utils.sendMessageGG((Player) sender, "You've specified invalid number of options. Syntax is /refund-add AMOUNT ITEMID METAID");
				return true;
			}
			parseCommand(sender, args);
		}
		return true;
	}
	
	public void parseCommand(CommandSender player, String[] args) {
		refunds = plugin.getRH();
		utils = plugin.getUtil();
		Integer quantity = 0;
		Integer itemid = 0;
		short metaid = 0;
		Player playerobject = (Player) player;
		int i = 0;
		while (i < args.length) {
			switch(i) {
			case 0:
				if (utils.isNumeric(args[i]))
					quantity = Integer.parseInt(args[i]); 
				break;
			case 1:
				if (utils.isNumeric(args[i]))
					itemid = Integer.parseInt(args[i]);
				break;
			case 2:
				if (utils.isNumeric(args[i]))
					metaid = (short) Integer.parseInt(args[i]);
				break;
			}
			i++;
		}
		
		if (quantity.equals(0)) {
			utils.sendMessageGG((Player) player, "Do you really want 0 of that?");
			return;
		}
		
		if (itemid.equals(0)) {
			utils.sendMessageGG((Player) player, "Really? Item 0?");
			return;
		}
		
		Material material = Material.matchMaterial(itemid.toString());
		if (material != null) {
			// playerobject.getInventory().addItem(new ItemStack(material, quantity, metaid)); // To be removed
			try {
				Integer refundId = refunds.getLatestRefundId(playerobject.getName().toLowerCase());
				refunds.addRefund(playerobject.getName().toLowerCase(), quantity, itemid, metaid, refundId);
			} catch (Exception e) {
				e.printStackTrace();
				utils.sendMessageGG(playerobject, "Something went wrong, please notify staff with error ssr102");
				return;
			}
			String materialname = "";
			materialname = "x" + itemid.toString() + ":" + metaid;
			utils.sendMessageGG((Player) player, "Added " + quantity.toString() + " " + materialname + " to your refund list");
			utils.sendMessageGG((Player) player, "To sign off the list, do /refund-sign. Use /refund-add to add more items");
		}
		return;
	}
}
