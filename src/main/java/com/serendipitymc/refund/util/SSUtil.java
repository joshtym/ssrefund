package com.serendipitymc.refund.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.serendipitymc.refund.refund.refund;

public class SSUtil {

	public Player findOnlinePlayerByName(String playerName) {
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
	
	public List<Player> getOnlinePlayers(HashMap<String, Integer> lookFor) {
        Player[] onlinePlayers = Bukkit.getOnlinePlayers();
        List<Player> onlineRefundPlayers = new ArrayList<Player>();
        
        if (onlinePlayers.length > 0) {
            for (Player op : onlinePlayers) {
                if (op != null) {
                    if (lookFor.containsKey(op.getName().toLowerCase())) {
                    	//onlineRefundPlayers.add(op.getName().toLowerCase());
                    	onlineRefundPlayers.add(op);
                    }
                }
            }
        }
        return onlineRefundPlayers;
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
	
	public void notifyOnlineAdminsGB(String sendMessage) {
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.length > 0) {
            for (Player op : onlinePlayers) {
                if (op != null) {
                    if (op.hasPermission("ssrefund.execute")) {
                        op.sendMessage(ChatColor.GOLD + "[Refunds] " + ChatColor.BLUE + sendMessage);
                    }
                }
            }
        }
        onlinePlayers = null;
        return;
	}
	
	public void sendDeniedMessage(String player, String status) {
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.length > 0) {
            for (Player op : onlinePlayers) {
                if (op != null) {
                    if (op.getName().toLowerCase().equals(player.toLowerCase())) {
                        op.sendMessage(ChatColor.GOLD + "[Refunds] " + ChatColor.BLUE + "Your refund has been " + ChatColor.DARK_BLUE + status);
                    }
                }
            }
        }
        onlinePlayers = null;
        return;
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
