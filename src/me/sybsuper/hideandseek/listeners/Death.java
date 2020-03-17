package me.sybsuper.hideandseek.listeners;

import me.sybsuper.hideandseek.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class Death implements Listener {
	private static Main plugin;
	public Death(Main plugin) {
		this.plugin = plugin;
	}
	@EventHandler
	public void OnPlayerDeath(PlayerDeathEvent e) {
		Player p = (Player) e.getEntity();
		boolean isInGame = false;
		boolean isHiderAlive = false;
		if (this.plugin.inGame != null && this.plugin.gameGoing) {
			for (Player p2:this.plugin.inGame) {
				if (p2.getUniqueId() == p.getUniqueId()) {
					isInGame = true;
				}
			}
			if (isInGame) {
				if (p.getUniqueId() != this.plugin.seeker.getUniqueId()) {
					if (this.plugin.playedSound.contains(p.getUniqueId().toString())) {
						p.sendMessage(ChatColor.RED + "You didn't get a reward because you were found by the seeker.");
						this.plugin.playedSound.remove(p.getUniqueId().toString());
					}
					p.setGameMode(GameMode.SPECTATOR);
					p.teleport(this.plugin.seeker.getLocation());
					for (Player p2 : this.plugin.inGame) {
						p2.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + p.getDisplayName() + ChatColor.RESET + "" + ChatColor.WHITE + " was found by seeker.");
					}
				} else {
					p.setGameMode(GameMode.SPECTATOR);
					for (Player p2 : this.plugin.inGame) {
						p2.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "The seeker got killed.");
					}
				}
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
}
