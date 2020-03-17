/*
 * Copyright (c) 2020 Sybsuper
 * All Rights Reserved
 *
 * Do not use this code without permission from the developer.
 */

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
		Leave.plugin = plugin;
	}

	@EventHandler
	public void OnPlayerQuitEvent(PlayerQuitEvent e) {
		if (plugin.gameGoing && plugin.inGame.contains(e.getPlayer())) {
			if (plugin.seeker.getUniqueId() == e.getPlayer().getUniqueId()) {
				for (Player p : plugin.inGame) {
					p.sendMessage(ChatColor.WHITE + "The seeker left the game. The game stopped.");
					new BukkitRunnable() {
						@Override
						public void run() {
							plugin.stopGame();
							for (Player p : plugin.inGame) {
								p.teleport(plugin.world.getSpawnLocation());
							}
						}
					}.runTaskLater(plugin, 20 * 10);
				}
			}
			boolean isHiderAlive = false;
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
						}
					}
				}.runTaskLater(plugin, 20 * 10);
			}
		}
	}
}
