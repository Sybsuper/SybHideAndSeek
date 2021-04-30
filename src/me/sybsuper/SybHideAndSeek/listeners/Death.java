package me.sybsuper.SybHideAndSeek.listeners;

import me.sybsuper.SybHideAndSeek.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.bukkit.scheduler.*;
import java.util.*;
import org.bukkit.plugin.*;
import org.bukkit.event.*;

public class Death implements Listener
{
	private static Main plugin;

	public Death(final Main plugin) {
		Death.plugin = plugin;
	}

	@EventHandler
	public void OnPlayerDeath(final PlayerDeathEvent e) {
		final Player p = e.getEntity();
		boolean isInGame = false;
		boolean isHiderAlive = false;
		if (Death.plugin.inGame != null && Death.plugin.gameGoing) {
			for (final Player p2 : Death.plugin.inGame) {
				if (p2.getUniqueId() == p.getUniqueId()) {
					isInGame = true;
				}
			}
			if (isInGame) {
				if (plugin.hideDefaultDeathMessages) {
					e.setDeathMessage("");
				}
				if (p.getUniqueId() != Death.plugin.seeker.getUniqueId()) {
					if (Death.plugin.playedSound.contains(p.getUniqueId().toString())) {
						p.sendMessage(Death.plugin.messageSoundFound);
						Death.plugin.playedSound.remove(p.getUniqueId().toString());
					}
					p.setGameMode(GameMode.SPECTATOR);
					p.teleport(Death.plugin.seeker.getLocation());
					for (final Player p2 : Death.plugin.inGame) {
						p2.sendMessage(Death.plugin.messagePlayerFound.replaceAll("\\{player}", p.getDisplayName()));
					}
				}
				else {
					p.setGameMode(GameMode.SPECTATOR);
					for (final Player p2 : Death.plugin.inGame) {
						p2.sendMessage(Death.plugin.messageSeekerKilled);
					}
					Death.plugin.stopGame();
					for (final Player p3 : Death.plugin.inGame) {
						p3.teleport(Death.plugin.world.getSpawnLocation());
						p3.setGameMode(Main.gamemode);
					}
				}
				for (final Player p2 : Death.plugin.inGame) {
					if (p2.getGameMode() == Main.gamemode && p2.getUniqueId() != Death.plugin.seeker.getUniqueId()) {
						isHiderAlive = true;
					}
				}
				if (!isHiderAlive) {
					for (final Player p2 : Death.plugin.inGame) {
						p2.sendMessage(Death.plugin.messageSeekerFoundEveryone.replaceAll("\\{seeker}", Death.plugin.seeker.getDisplayName()));
						p2.getInventory().clear();
						if (Death.plugin.config.getBoolean("hideNameTags")) {
							p2.setCustomNameVisible(true);
						}
					}
					new BukkitRunnable() {
						public void run() {
							Death.plugin.stopGame();
							for (final Player p : Death.plugin.inGame) {
								p.teleport(Death.plugin.world.getSpawnLocation());
								p.setGameMode(Main.gamemode);
							}
						}
					}.runTaskLater((Plugin)Death.plugin, 200L);
				}
			}
		}
	}
}
