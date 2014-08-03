package com.serendipitymc.refund.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.serendipitymc.refund.refund.refund;

public class SSUtil {

	private Player findOnlinePlayerByName(String playerName) {
        Player[] onlinePlayers = Bukkit.getOnlinePlayers();
        Player playerFound = null;
        int foundPlayers = 0;

        if (onlinePlayers.length > 0) {
            for (Player op : onlinePlayers) {
                if (op != null) {
                    if (op.getName().toLowerCase().equals(playerName.toLowerCase())) {
                        foundPlayers++;
                        playerFound = op;
                    }
                }
            }

            if (foundPlayers != 1) {
                playerFound = null;
            }
        }
        return playerFound;
    }
	
	public void sendMessageGG(Player target, String message) {
		target.sendMessage(ChatColor.GOLD + "[Refunds] " + ChatColor.GRAY + message);
	}
	
	public void notifyIfOnline(String target, String sender) {
		Player toSendto;
		toSendto = findOnlinePlayerByName(target);
		if (!(toSendto == null)) {
			sendMessageGG(toSendto, "A new refund request for you has been opened by " + sender);
			sendMessageGG(toSendto, "To add items, please use the following format:");
			sendMessageGG(toSendto, "/refund-add <QUANTITY> <ITEMID> (METAID)");
			sendMessageGG(toSendto, "When list is complete, please do /refund-sign");
		}
	}
	
	public void sendNotificationMessage(refund plugin, CommandSender sender, boolean realPlayer, String message) {
		if (realPlayer) {
			sender.sendMessage(message);
		} else {
			plugin.getLogger().info(message);
		}
	}
	
	public boolean isNumeric(String str) {
	  try  {
	    double d = Double.parseDouble(str);  
	  }  catch(NumberFormatException nfe)  {  
	    return false;  
	  }  
	  return true;  
	}
}
