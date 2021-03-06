package nl.evolutioncoding.areashop.commands;

import com.sk89q.worldedit.bukkit.selections.Selection;
import nl.evolutioncoding.areashop.AreaShop;
import nl.evolutioncoding.areashop.Utils;
import nl.evolutioncoding.areashop.regions.GeneralRegion;
import nl.evolutioncoding.areashop.regions.RegionGroup;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class GroupaddCommand extends CommandAreaShop {

	public GroupaddCommand(AreaShop plugin) {
		super(plugin);
	}
	
	@Override
	public String getCommandStart() {
		return "areashop groupadd";
	}
	
	@Override
	public String getHelp(CommandSender target) {
		if(target.hasPermission("areashop.groupadd")) {
			return "help-groupadd";
		}
		return null;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(!sender.hasPermission("areashop.groupadd")) {
			plugin.message(sender, "groupadd-noPermission");
			return;
		}		
		if(args.length < 2 || args[1] == null) {
			plugin.message(sender, "groupadd-help");
			return;
		}		
		RegionGroup group = plugin.getFileManager().getGroup(args[1]);
		if(group == null) {
			group = new RegionGroup(plugin, args[1]);
			plugin.getFileManager().addGroup(group);
		}
		if(args.length == 2) {
			if(!(sender instanceof Player)) {
				plugin.message(sender, "cmd-weOnlyByPlayer");
				return;
			}
			Player player = (Player)sender;
			Selection selection = plugin.getWorldEdit().getSelection(player);
			if(selection == null) {
				plugin.message(player, "cmd-noSelection");
				return;
			}
			List<GeneralRegion> regions = Utils.getASRegionsInSelection(selection);
			if(regions.size() == 0) {
				plugin.message(player, "cmd-noRegionsFound");
				return;
			}
			TreeSet<GeneralRegion> regionsSuccess = new TreeSet<>();
			TreeSet<GeneralRegion> regionsFailed = new TreeSet<>();
			for(GeneralRegion region : regions) {
				if(group.addMember(region)) {
					regionsSuccess.add(region);
				} else {
					regionsFailed.add(region);
				}
			}
			if(regionsSuccess.size() != 0) {
				plugin.message(player, "groupadd-weSuccess", group.getName(), Utils.regionListMessage(regionsSuccess));
			}
			if(regionsFailed.size() != 0) {
				plugin.message(player, "groupadd-weFailed", group.getName(), Utils.regionListMessage(regionsFailed));
			}
			// Update all regions, this does it in a task, updating them without lag
			plugin.getFileManager().updateRegions(new ArrayList<>(regionsSuccess), player);
		} else {
			GeneralRegion region = plugin.getFileManager().getRegion(args[2]);
			if(region == null) {
				plugin.message(sender, "groupadd-noRegion", args[2]);
				return;
			}
			if(group.addMember(region)) {
				region.update();
				plugin.message(sender, "groupadd-success", group.getName(), group.getMembers().size(), region);
			} else {
				plugin.message(sender, "groupadd-failed", group.getName(), region);
			}
		}
	}
	
	@Override
	public List<String> getTabCompleteList(int toComplete, String[] start, CommandSender sender) {
		List<String> result = new ArrayList<>();
		if(toComplete == 2) {
			result = plugin.getFileManager().getGroupNames();
		} else if(toComplete == 3) {
			result = plugin.getFileManager().getRegionNames();	
		}
		return result;
	}

}










