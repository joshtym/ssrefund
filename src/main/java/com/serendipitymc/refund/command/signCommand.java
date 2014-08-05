package com.serendipitymc.refund.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.serendipitymc.refund.korik.SubCommandExecutor;
import com.serendipitymc.refund.managers.RefundHandler;
import com.serendipitymc.refund.refund.refund;
import com.serendipitymc.refund.util.SSUtil;

public class signCommand implements CommandExecutor {
	private refund plugin;
	private RefundHandler refunds;
	private SSUtil utils;

	public signCommand(refund instance) {
		plugin = instance;
		utils = plugin.getUtil();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		if (sender instanceof Player) {
			refunds = plugin.getRH();
			utils = plugin.getUtil();
			Player player = (Player) sender;
			try {
				String server = Bukkit.getServerName();
				Integer refundAmount = refunds.countRefunds(sender.getName().toString().toLowerCase(), server);
				if (!(refundAmount.equals(1))) {
					utils.sendMessageGG((Player) sender, "I can't find an active refund request for you");
					return true;
				}
				Integer refundId = refunds.getLatestRefundId(player.getName().toLowerCase(), server);
				Integer amountOfItems = refunds.getItemCount(refundId);
				if (amountOfItems < 1) {
					utils.sendMessageGG(player, "Can't sign off on an empty request!");
					return true;
				}
				refunds.signRefund(refundId);
				utils.sendMessageGG(player, "Thank you for confirming refund request #" + refundId.toString());
				utils.notifyOnlineAdminsGB(player.getName().toLowerCase() + " has signed off on refund id " + refundId.toString());
				utils.notifyOnlineAdminsGB("To review, please do /refund detail <id>  or /refund approve|deny|test <id>");
				utils.sendMessageGG(player, "It is now in queue to be verified by staff members");
			} catch (Exception e) {
				utils.sendMessageGG((Player) sender, "Something went wrong, please report error ssr103");
				return true;
			}
			
		}
		return true;
	}
}
