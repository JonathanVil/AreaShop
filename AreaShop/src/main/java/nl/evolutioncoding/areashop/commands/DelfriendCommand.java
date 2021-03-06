package nl.evolutioncoding.areashop.commands;

import nl.evolutioncoding.areashop.AreaShop;
import nl.evolutioncoding.areashop.Utils;
import nl.evolutioncoding.areashop.regions.BuyRegion;
import nl.evolutioncoding.areashop.regions.GeneralRegion;
import nl.evolutioncoding.areashop.regions.RentRegion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DelfriendCommand extends CommandAreaShop {

	public DelfriendCommand(AreaShop plugin) {
		super(plugin);
	}
	
	@Override
	public String getCommandStart() {
		return "areashop delfriend";
	}

	@Override
	public String getHelp(CommandSender target) {
		if(target.hasPermission("areashop.delfriendall")) {
			return "help-delFriendAll";
		} else if(target.hasPermission("areashop.delfriend")) {
			return "help-delFriend";
		}
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission("areashop.delfriend") && !sender.hasPermission("areashop.delfriendall")) {
			plugin.message(sender, "delfriend-noPermission");
			return;
		}		
		if(args.length < 2) {
			plugin.message(sender, "delfriend-help");
			return;
		}
		GeneralRegion region;
		if(args.length <= 2) {
			if (sender instanceof Player) {
				// get the region by location
				List<GeneralRegion> regions = Utils.getAllApplicableRegions(((Player) sender).getLocation());
				if (regions.isEmpty()) {
					plugin.message(sender, "cmd-noRegionsAtLocation");
					return;
				} else if (regions.size() > 1) {
					plugin.message(sender, "cmd-moreRegionsAtLocation");
					return;
				} else {
					region = regions.get(0);
				}
			} else {
				plugin.message(sender, "cmd-automaticRegionOnlyByPlayer");
				return;
			}	
		} else {
			region = plugin.getFileManager().getRegion(args[2]);
			if(region == null) {
				plugin.message(sender, "cmd-notRegistered", args[2]);
				return;
			}
		}
		if(sender.hasPermission("areashop.delfriendall")) {
			if((region.isRentRegion() && !((RentRegion)region).isRented())
					|| (region.isBuyRegion() && !((BuyRegion)region).isSold())) {
				plugin.message(sender, "delfriend-noOwner", region);
				return;
			}		
			OfflinePlayer friend = Bukkit.getOfflinePlayer(args[1]);
			if(!region.getFriends().contains(friend.getUniqueId())) {
				plugin.message(sender, "delfriend-notAdded", friend.getName(), region);
				return;
			}
			region.deleteFriend(friend.getUniqueId());
			region.update();
			plugin.message(sender, "delfriend-successOther", friend.getName(), region);
		} else {
			if(sender.hasPermission("areashop.delfriend") && sender instanceof Player) {
				if(region.isOwner((Player)sender)) {
					OfflinePlayer friend = Bukkit.getOfflinePlayer(args[1]);
					if(!region.getFriends().contains(friend.getUniqueId())) {
						plugin.message(sender, "delfriend-notAdded", friend.getName(), region);
						return;
					}
					region.deleteFriend(friend.getUniqueId());
					region.update();
					plugin.message(sender, "delfriend-success", friend.getName(), region);
				} else {
					plugin.message(sender, "delfriend-noPermissionOther", region);
				}
			} else {
				plugin.message(sender, "delfriend-noPermission", region);
			}
		}
	}
	
	@Override
	public List<String> getTabCompleteList(int toComplete, String[] start, CommandSender sender) {
		ArrayList<String> result = new ArrayList<>();
		if(toComplete == 2) {
			for(Player player : Utils.getOnlinePlayers()) {
				result.add(player.getName());
			}
		} else if(toComplete == 3) {
			result.addAll(plugin.getFileManager().getRegionNames());
		}
		return result;
	}
}








