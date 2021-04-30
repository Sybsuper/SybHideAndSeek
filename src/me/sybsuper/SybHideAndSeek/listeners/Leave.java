package me.sybsuper.SybHideAndSeek.listeners;

import me.sybsuper.SybHideAndSeek.*;
import org.bukkit.event.player.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.*;
import java.util.*;
import org.bukkit.plugin.*;
import org.bukkit.event.*;

public class Leave implements Listener
{
	private static Main plugin;

	public Leave(Main plugin) {
		Leave.plugin = plugin;
	}

	@EventHandler
	public void OnPlayerQuitEvent(PlayerQuitEvent e) {
		if (Leave.plugin.gameGoing && Leave.plugin.inGame.contains(e.getPlayer())) {
			if (Leave.plugin.seeker.getUniqueId() == e.getPlayer().getUniqueId()) {
				for (Player p : Leave.plugin.inGame) {
					p.getInventory().clear();
					p.sendMessage(Leave.plugin.messageSeekerLeft);
					if (Leave.plugin.config.getBoolean("hideNameTags")) {
						p.setCustomNameVisible(true);
					}
				}
				new BukkitRunnable() {
					public void run() {
						Leave.plugin.stopGame();
						for (Player p : Leave.plugin.inGame) {
							p.teleport(Leave.plugin.world.getSpawnLocation());
							p.setGameMode(Main.gamemode);
						}
					}
				}.runTaskLater((Plugin)Leave.plugin, 200L);
			}
			else {
				boolean isHiderAlive = false;
				for (Player p2 : Leave.plugin.inGame) {
					if (p2.getGameMode() == Main.gamemode && p2.getUniqueId() != Leave.plugin.seeker.getUniqueId()) {
						isHiderAlive = true;
						break;
					}
				}
				if (!isHiderAlive) {
					for (Player p2 : Leave.plugin.inGame) {
						p2.getInventory().clear();
						if (Leave.plugin.config.getBoolean("hideNameTags")) {
							p2.setCustomNameVisible(true);
						}
						p2.sendMessage(Leave.plugin.messageSeekerFoundEveryone.replaceAll("\\{seeker}", Leave.plugin.seeker.getDisplayName()));
					}
					new BukkitRunnable() {
						public void run() {
							Leave.plugin.stopGame();
							for (Player p : Leave.plugin.inGame) {
								p.teleport(Leave.plugin.world.getSpawnLocation());
								p.setGameMode(Main.gamemode);
							}
						}
					}.runTaskLater((Plugin)Leave.plugin, 200L);
				}
			}
		}
	}
}
