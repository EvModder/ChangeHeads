package net.evmodder;

import net.evmodder.EvLib.EvCommand;
import net.evmodder.EvLib.EvPlugin;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import net.evmodder.DropHeads.DropHeads;

/**
*
* @author EvModder/EvDoc (evdoc at altcraft.net)
*/
public class ChangeHeads extends EvPlugin{
	public class CommandChangeTexture extends EvCommand{
		public CommandChangeTexture(JavaPlugin pl){super(pl);}

		@Override final public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args){
			if(s instanceof Player){
				final URL skinURL = ((Player)s).getPlayerProfile().getTextures().getSkin();
				if(skinURL != null) return List.of(skinURL.toString().substring(skinURL.toString().lastIndexOf('/')+1));
			}
			return List.of();
		}

		@Override final public boolean onCommand(CommandSender sender, Command command, String label, String args[]){
			if(sender instanceof Player == false){
				sender.sendMessage(ChatColor.RED+"This command can only be run by in-game players");
				return true;
			}
			final ItemStack item = ((Player)sender).getInventory().getItemInMainHand();
			if(item == null || item.getType() != Material.PLAYER_HEAD){
				sender.sendMessage(ChatColor.RED+"You need to hold a player head to use this command");
				return false;
			}
			if(args.length == 0){
				sender.sendMessage(ChatColor.RED+"Please provide a texture URL");
				return false;
			}
			if(args.length > 1){
				sender.sendMessage(ChatColor.RED+"Too many arguments");
				return false;
			}
			final String urlCode = args[0].substring(args[0].lastIndexOf('/')+1);
			if(!urlCode.matches("[0-9a-f]{64}")){
				sender.sendMessage(ChatColor.RED+"Invalid texture code format");
				return false;
			}
			// Method 1:
			final String json = "{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/"+urlCode+"\"}}}";
			final String base64 = Base64.getEncoder().encodeToString(json.getBytes(StandardCharsets.ISO_8859_1));
//			final UUID uuid = UUID.nameUUIDFromBytes(base64.getBytes());
//			final String name = null;
//			final GameProfile profile = new GameProfile(uuid, name);
//			profile.getProperties().put("textures", new Property("textures", base64));
//			SkullMeta meta = (SkullMeta)item.getItemMeta();
//			HeadUtils.setGameProfile(meta, profile);
//			item.setItemMeta(meta);
//			((Player)sender).getInventory().setItemInMainHand(item);

			// Method 2: (Doesn't work because profilename is still "dropheads:COW", so it reverts when the item is dropped
//			final SkullMeta meta = (SkullMeta)item.getItemMeta();
//			final PlayerProfile pp = meta.getOwnerProfile();
//			try{pp.getTextures().setSkin(new URL("http://textures.minecraft.net/texture/"+urlCode));}
//			catch(MalformedURLException e){e.printStackTrace();}
//			meta.setOwnerProfile(pp);
//			item.setItemMeta(meta);

			// Method 3:
			final DropHeads dh = (DropHeads)getServer().getPluginManager().getPlugin("DropHeads");
			final ItemStack newHead = dh.getAPI().getHead(base64.getBytes());
			newHead.setAmount(item.getAmount());
			((Player)sender).getInventory().setItemInMainHand(newHead);

			sender.sendMessage(ChatColor.GREEN+"Texture updated!");
			return true;
		}
	}

	@Override public void onEvEnable(){
		new CommandChangeTexture(this);
	}
}