/*
 * Copyright (c) 2020 Sybsuper
 * All Rights Reserved
 *
 * Do not use this code without permission of the developer.
 */

package me.sybsuper.SybHideAndSeek.listeners;

import me.sybsuper.SybHideAndSeek.Main;
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
		Death.plugin = plugin;
	}

	@EventHandler
	public void OnPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		boolean isInGame = false;
		boolean isHiderAlive = false;
		if (plugin.inGame != null && plugin.gameGoing) {
			for (Player p2 : plugin.inGame) {
				if (p2.getUniqueId() == p.getUniqueId()) {
					isInGame = true;
				}
			}
			if (isInGame) {
				if (p.getUniqueId() != plugin.seeker.getUniqueId()) {
					if (plugin.playedSound.contains(p.getUniqueId().toString())) {
						p.sendMessage(ChatColor.RED + "You didn't get a reward because you were found by the seeker.");
						plugin.playedSound.remove(p.getUniqueId().toString());
					}
					p.setGameMode(GameMode.SPECTATOR);
					p.teleport(plugin.seeker.getLocation());
					for (Player p2 : plugin.inGame) {
						p2.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + p.getDisplayName() + ChatColor.RESET + "" + ChatColor.WHITE + " was found by seeker.");
					}
				} else {
					p.setGameMode(GameMode.SPECTATOR);
					for (Player p2 : plugin.inGame) {
						p2.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "The seeker got killed.");
					}
				}
				for (Player p2 : plugin.inGame) {
					if (p2.getGameMode() == GameMode.SURVIVAL && p2.getUniqueId() != plugin.seeker.getUniqueId()) {
						isHiderAlive = true;
					}
				}
				if (!(isHiderAlive)) {
					for (Player p2 : plugin.inGame) {
						p2.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "The seeker found everyone. Well done " + plugin.seeker.getDisplayName());
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							plugin.stopGame();
							for (Player p : plugin.inGame) {
								p.teleport(plugin.world.getSpawnLocation());
								p.setGameMode(GameMode.SURVIVAL);
							}
						}
					}.runTaskLater(plugin, 20 * 10);
				}
			}
		}
	}
}
