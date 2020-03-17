package me.sybsuper.hideandseek.listeners;

import me.sybsuper.hideandseek.Main;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Leave implements Listener {
	private static Main plugin;
	public Leave(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void OnPlayerQuitEvent(PlayerQuitEvent e) {
		if (this.plugin.gameGoing && this.plugin.inGame.contains(e.getPlayer())){
			if (this.plugin.seeker.getUniqueId() == e.getPlayer().getUniqueId()) {
				for (Player p:this.plugin.inGame) {
					p.sendMessage(ChatColor.WHITE + "The seeker left the game. The game stopped.");
					new BukkitRunnable() {
						@Override
						public void run() {
							plugin.stopGame();
							for (Player p:plugin.inGame) {
								p.teleport(plugin.world.getSpawnLocation());
							}
						}
					}.runTaskLater(this.plugin, 20 * 10);
				}
			}
			boolean isHiderAlive = false;
			for (Player p2:this.plugin.inGame) {
				if (p2.getGameMode() == GameMode.SURVIVAL && p2.getUniqueId() != this.plugin.seeker.getUniqueId()) {
					isHiderAlive = true;
				}
			}
			if (!(isHiderAlive)) {
				for (Player p2 : this.plugin.inGame) {
					p2.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "The seeker found everyone. Well done " + this.plugin.seeker.getDisplayName());
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						plugin.stopGame();
						for (Player p:plugin.inGame) {
							p.teleport(plugin.world.getSpawnLocation());
						}
					}
				}.runTaskLater(this.plugin, 20 * 10);
			}
		}
	}
}
