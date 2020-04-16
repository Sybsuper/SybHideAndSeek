/*
 * Copyright (c) 2020 Sybsuper
 * All Rights Reserved
 *
 * Do not use this code without permission of the developer.
 */

package me.sybsuper.SybHideAndSeek;

import me.sybsuper.SybHideAndSeek.listeners.Death;
import me.sybsuper.SybHideAndSeek.listeners.Leave;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JavaPlugin {
	public List<Player> inGame;
	public boolean gameGoing;
	public World world;
	public double startWorldBorderSize;
	public Player seeker;
	public List<BukkitRunnable> tasks;
	public BukkitRunnable freezeSeeker;
	public String worldName;
	public List<Integer> announceTimeAt;
	public Integer hideTime;
	public boolean shrinking;
	public List<Integer> shrinkingAfter;
	public Integer shrinkingDuration;
	public double shrinkingFactor;
	public boolean hideDefaultDeathMessages;
	public Integer minPlayers;
	public List<String> playedSound;
	public boolean sound;
	public Integer soundMinDelay;
	public Integer soundSurviveTime;
	public List<String> soundRewardCommands;
	public FileConfiguration config;
	public File configFile;

	@Override
	public void onEnable() {
		this.gameGoing = false;
		this.config = getConfig();
		this.config.options().copyDefaults(true);
		saveConfig();
		loadConfig();
		this.configFile = new File(getDataFolder(), "config.yml");
		this.world = Bukkit.getWorld(this.worldName);
		Bukkit.getServer().getPluginManager().registerEvents(new Death(this), this);
		Bukkit.getServer().getPluginManager().registerEvents(new Leave(this), this);
	}

	public void loadConfig() {
		this.worldName = this.config.getString("world");

		this.shrinking = this.config.getBoolean("shrinking.enable");
		this.shrinkingAfter = this.config.getIntegerList("shrinking.after");
		this.shrinkingDuration = this.config.getInt("shrinking.duration");
		this.shrinkingFactor = this.config.getDouble("shrinking.factor");

		this.announceTimeAt = this.config.getIntegerList("announceTimeAt");

		this.hideTime = this.config.getInt("hideTime");

		this.hideDefaultDeathMessages = this.config.getBoolean("hideDefaultDeathMessages");

		this.minPlayers = this.config.getInt("minPlayers");

		this.sound = this.config.getBoolean("sound.enable");
		this.soundMinDelay = this.config.getInt("sound.minDelay");
		this.soundSurviveTime = this.config.getInt("sound.surviveTime");
		this.soundRewardCommands = this.config.getStringList("sound.rewardCommands");
	}

	public void ShrinkBorder() {
		for (Player p : inGame) {
			p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "The worldborder will shrink to " + (int) (this.shrinkingFactor * 100) + "% in " + this.shrinkingDuration + " seconds!");
		}
		this.world.getWorldBorder().setSize(this.world.getWorldBorder().getSize() * this.shrinkingFactor, this.shrinkingDuration);
	}

	public void stopGame() {
		this.gameGoing = false;
		for (BukkitRunnable task : this.tasks) {
			task.cancel();
		}
		this.world.getWorldBorder().setSize(this.startWorldBorderSize);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("hideandseek")) {
			if (!(sender.hasPermission("sybhideandseek.start"))) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
				return true;
			}
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("stop")) {
					this.stopGame();
					sender.sendMessage("The game stopped.");
					return true;
				}
				if (args[0].equalsIgnoreCase("shrink")) {
					this.ShrinkBorder();
					return true;
				}
				if (args[0].equalsIgnoreCase("reload")) {
					this.config = YamlConfiguration.loadConfiguration(configFile);
					loadConfig();
					sender.sendMessage("The settings from the config.yml file have been reloaded.");
					return true;
				}
			}
			if (this.gameGoing) {
				sender.sendMessage(ChatColor.RED + "A game has already started.");
				return true;
			}
			this.world = Bukkit.getWorld(this.worldName);
			if (this.world != null) {
				this.inGame = this.world.getPlayers();
				this.world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, !this.hideDefaultDeathMessages);
				if (this.inGame.size() >= this.minPlayers) {
					this.gameGoing = true;
					this.playedSound = new ArrayList<String>();
					this.startWorldBorderSize = this.world.getWorldBorder().getSize();
					for (Player p : this.inGame) {
						p.teleport(this.world.getSpawnLocation());
						p.setGameMode(GameMode.SURVIVAL);
						p.sendMessage("You're now in a Hide & Seek game.");
						if (config.getBoolean("hideNameTags")) {
							p.setCustomNameVisible(false);
						}
					}
					this.seeker = this.inGame.get(new Random().nextInt(this.inGame.size()));
					this.seeker.sendMessage(ChatColor.DARK_GREEN + "" + ChatColor.BOLD + "You're the seeker.");
					for (Player p : this.inGame) {
						if (p.getUniqueId() != this.seeker.getUniqueId()) {
							p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + this.seeker.getDisplayName() + " is the seeker, you have 1 minute to get away.");
							p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, this.hideTime * 20, 0, false, false));
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, false, false));
							p.getInventory().clear();
						} else {
							p.sendMessage(ChatColor.WHITE + "" + ChatColor.BOLD + "Wait " + this.hideTime + " seconds before you can search for the hiders.");
							p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * this.hideTime, 10, false, false));
							p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * this.hideTime, 255, false, false));
							p.setWalkSpeed(0);
							p.getInventory().clear();
							new BukkitRunnable() {
								@Override
								public void run() {
									seeker.setWalkSpeed(0.2F);
									seeker.teleport(world.getSpawnLocation());
									ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
									sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 16);
									sword.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
									seeker.getInventory().setItem(0, sword);
								}
							}.runTaskLater(this, 20 * this.hideTime);
							this.freezeSeeker = new BukkitRunnable() {
								@Override
								public void run() {
									Location spawn = world.getSpawnLocation();
									spawn.setPitch(90);
									seeker.teleport(spawn);
								}
							};
							this.freezeSeeker.runTaskTimer(this, 2, 2);
						}
					}
					for (Integer time : announceTimeAt) {
						new BukkitRunnable() {
							@Override
							public void run() {
								for (Player p : inGame) {
									p.sendMessage(time + " seconds left");
								}
							}
						}.runTaskLater(this, 20 * (this.hideTime - time));
					}
					new BukkitRunnable() {
						@Override
						public void run() {
							for (Player p : inGame) {
								p.sendMessage(ChatColor.BOLD + "The seeker is seeking.");
								freezeSeeker.cancel();
							}
						}
					}.runTaskLater(this, 20 * this.hideTime);
					if (this.shrinking) {
						this.tasks = new ArrayList<BukkitRunnable>();
						for (Integer time : this.shrinkingAfter) {
							this.tasks.add(new BukkitRunnable() {
								@Override
								public void run() {
									if (gameGoing) {
										ShrinkBorder();
									}
								}
							});
							this.tasks.get(this.tasks.size() - 1).runTaskLater(this, 20 * time + 20 * this.hideTime);
						}
					}
				} else {
					sender.sendMessage("There aren't enough players in the '" + this.worldName + "' world.");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "There's no world called '" + this.worldName + "', please create it or change the world in the config.yml file.");
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("sound")) {
			if (!this.sound) {
				sender.sendMessage(ChatColor.RED + "Sounds are not enabled.");
				return true;
			}
			if (!sender.hasPermission("sybhideandseek.sound")) {
				sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
				return true;
			}
			if (!(sender instanceof Player)) {
				sender.sendMessage(ChatColor.RED + "Only players can execute this command.");
				return true;
			}
			if (!gameGoing) {
				sender.sendMessage(ChatColor.RED + "You're not in a Hide and Seek game.");
				return true;
			}
			Player p = (Player) sender;
			if (!inGame.contains(p)) {
				sender.sendMessage(ChatColor.RED + "You're not in a Hide and Seek game.");
				return true;
			}
			if (p.getGameMode() != GameMode.SURVIVAL) {
				sender.sendMessage(ChatColor.RED + "You're not alive anymore.");
				return true;
			}
			if (this.seeker.getUniqueId() == p.getUniqueId()) {
				sender.sendMessage(ChatColor.RED + "The seeker can't get rewards for playing sounds.");
				return true;
			}
			if (playedSound.contains(p.getUniqueId().toString())) {
				sender.sendMessage(ChatColor.RED + "You've already played a sound, please wait before playing another.");
				return true;
			}
			playedSound.add(p.getUniqueId().toString());
			this.world.playSound(p.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 2.0F, 0.1F);
			sender.sendMessage("Survive for " + this.soundSurviveTime + " seconds to get a reward.");
			new BukkitRunnable() {
				@Override
				public void run() {
					if (playedSound.contains(p.getUniqueId().toString())) {
						sender.sendMessage("You survived.");
						playedSound.remove(p.getUniqueId().toString());
						for (String command : soundRewardCommands) {
							Bukkit.getServer().dispatchCommand(getServer().getConsoleSender(), command.replaceAll("\\{player}", p.getName()));
						}
					}
				}
			}.runTaskLater(this, 20 * this.soundMinDelay);
			return true;
		}
		return false;
	}
}
