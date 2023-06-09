package me.sybsuper.SybHideAndSeek;

import me.sybsuper.SybHideAndSeek.listeners.Death;
import me.sybsuper.SybHideAndSeek.listeners.Leave;
import me.sybsuper.SybHideAndSeek.listeners.Other;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends JavaPlugin {
    public static GameMode gamemode;
    public List<Player> inGame;
    public boolean gameGoing;
    public boolean shrinking;
    public boolean hideDefaultDeathMessages;
    public boolean sound;
    public boolean disableBlockBreaking;
    public boolean disableBlockPlacing;
    public boolean disableItemDropping;
    public World world;
    public double startWorldBorderSize;
    public Player seeker;
    public List<BukkitRunnable> tasks;
    public BukkitRunnable freezeSeeker;
    public String worldName;
    public String messageSeeker;
    public String messageWelcome;
    public String messageShrink;
    public String messageGameStopped;
    public String messageReloaded;
    public String messageAlreadyStarted;
    public String messageIsTheSeeker;
    public String messageYouAreTheSeeker;
    public String messageTimeLeft;
    public String messageSeekerStarted;
    public String messageNotEnoughPlayers;
    public String messageNoWorld;
    public String messageNoSound;
    public String messageSoundNoPermission;
    public String messageSoundPlayersOnly;
    public String messageNotInAGame;
    public String messageNotAlive;
    public String messageSoundNoSeeker;
    public String messageSoundCooldown;
    public String messageSoundSurvive;
    public String messageSoundSurvived;
    public String messageSoundFound;
    public String messagePlayerFound;
    public String messageSeekerKilled;
    public String messageSeekerFoundEveryone;
    public String messageSeekerLeft;
    public List<Integer> announceTimeAt;
    public List<Integer> shrinkingAfter;
    public double shrinkingFactor;
    public Integer hideTime;
    public Integer shrinkingDuration;
    public Integer minPlayers;
    public Integer soundMinDelay;
    public Integer soundSurviveTime;
    public List<String> playedSound;
    public List<String> soundRewardCommands;
    public List<String> globalStartCommands;
    public List<String> globalShrinkCommands;
    public List<String> globalStopCommands;
    public List<String> playerStartCommands;
    public List<String> playerShrinkCommands;
    public List<String> playerStopCommands;
    public FileConfiguration config;
    public File configFile;

    public Main() {
        this.tasks = new ArrayList<BukkitRunnable>();
    }

    public void onEnable() {
        this.gameGoing = false;
        this.config = this.getConfig();
        this.config.options().copyDefaults(true);
        this.saveConfig();
        this.loadConfig();
        this.configFile = new File(this.getDataFolder(), "config.yml");
        this.world = Bukkit.getWorld(this.worldName);
        Bukkit.getServer().getPluginManager().registerEvents((Listener) new Death(this), (Plugin) this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener) new Leave(this), (Plugin) this);
        Bukkit.getServer().getPluginManager().registerEvents((Listener) new Other(this), (Plugin) this);
    }

    public void loadConfig() {
        this.worldName = this.config.getString("world");
        this.shrinking = this.config.getBoolean("shrinking.enable");
        this.shrinkingAfter = (List<Integer>) this.config.getIntegerList("shrinking.after");
        this.shrinkingDuration = this.config.getInt("shrinking.duration");
        this.shrinkingFactor = this.config.getDouble("shrinking.factor");
        this.announceTimeAt = (List<Integer>) this.config.getIntegerList("announceTimeAt");
        this.hideTime = this.config.getInt("hideTime");
        this.hideDefaultDeathMessages = this.config.getBoolean("hideDefaultDeathMessages");
        this.minPlayers = this.config.getInt("minPlayers");
        this.sound = this.config.getBoolean("sound.enable");
        this.soundMinDelay = this.config.getInt("sound.minDelay");
        this.soundSurviveTime = this.config.getInt("sound.surviveTime");
        this.soundRewardCommands = this.config.getStringList("sound.rewardCommands");
        this.globalStartCommands = this.config.getStringList("commands.global.start");
        this.globalShrinkCommands = this.config.getStringList("commands.global.shrink");
        this.globalStopCommands = this.config.getStringList("commands.global.stop");
        this.playerStartCommands = this.config.getStringList("commands.player.start");
        this.playerShrinkCommands = this.config.getStringList("commands.player.shrink");
        this.playerStopCommands = this.config.getStringList("commands.player.stop");
        this.messageSeeker = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.seeker"));
        this.messageWelcome = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.welcome"));
        this.messageShrink = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.shrink"));
        this.messageGameStopped = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.gameStopped"));
        this.messageReloaded = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.reloaded"));
        this.messageAlreadyStarted = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.alreadyStarted"));
        this.messageIsTheSeeker = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.isTheSeeker"));
        this.messageYouAreTheSeeker = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.youAreTheSeeker"));
        this.messageTimeLeft = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.timeLeft"));
        this.messageSeekerStarted = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.seekerStarted"));
        this.messageNotEnoughPlayers = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.notEnoughPlayers"));
        this.messageNoWorld = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.noWorld"));
        this.messageNoSound = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.noSound"));
        this.messageSoundNoPermission = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.soundNoPermission"));
        this.messageSoundPlayersOnly = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.soundPlayersOnly"));
        this.messageNotInAGame = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.notInAGame"));
        this.messageNotAlive = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.notAlive"));
        this.messageSoundNoSeeker = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.soundNoSeeker"));
        this.messageSoundCooldown = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.soundCooldown"));
        this.messageSoundSurvive = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.soundSurvive"));
        this.messageSoundSurvived = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.soundSurvived"));
        this.messageSoundFound = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.soundFound"));
        this.messagePlayerFound = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.playerFound"));
        this.messageSeekerKilled = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.seekerKilled"));
        this.messageSeekerFoundEveryone = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.seekerFoundEveryone"));
        this.messageSeekerLeft = ChatColor.translateAlternateColorCodes('&', this.config.getString("messages.seekerLeft"));
        try {
            Main.gamemode = GameMode.valueOf(this.config.getString("gamemode").toUpperCase());
        } catch (IllegalArgumentException e) {
            this.getLogger().severe("Could not parse '" + this.config.getString("gamemode") + "' to a gamemode please use: 'SURVIVAL' or 'ADVENTURE'");
        }
        this.disableItemDropping = this.config.getBoolean("disable.itemDropping");
        this.disableBlockBreaking = this.config.getBoolean("disable.blockBreaking");
        this.disableBlockPlacing = this.config.getBoolean("disable.blockPlacing");
    }

    public void ShrinkBorder() {
        for (Player p : this.inGame) {
            p.sendMessage(this.messageShrink.replaceAll("\\{percentage}", String.valueOf((int) (this.shrinkingFactor * 100.0))).replaceAll("\\{time}", String.valueOf(this.shrinkingDuration)));
            for (String command : this.playerShrinkCommands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("\\{player}", p.getName()));
            }
        }
        for (String command2 : this.globalShrinkCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command2);
        }
        this.world.getWorldBorder().setSize(this.world.getWorldBorder().getSize() * this.shrinkingFactor, (long) this.shrinkingDuration);
    }

    public void stopGame() {
        if (!this.freezeSeeker.isCancelled()) {
            this.freezeSeeker.cancel();
        }
        this.gameGoing = false;
        for (BukkitRunnable task : this.tasks) {
            task.cancel();
        }
        for (Player p : this.inGame) {
            for (String command : this.playerStopCommands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("\\{player}", p.getName()));
            }
        }
        this.world.getWorldBorder().setSize(this.startWorldBorderSize);
        for (String command2 : this.globalStopCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command2);
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("hideandseek")) {
            if (!sender.hasPermission("sybhideandseek.start")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to execute this command.");
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("stop")) {
                    for (Player p2 : this.inGame) {
                        p2.getInventory().clear();
                        if (this.config.getBoolean("hideNameTags")) {
                            p2.setCustomNameVisible(true);
                        }
                    }
                    this.stopGame();
                    sender.sendMessage(this.messageGameStopped);
                    return true;
                }
                if (args[0].equalsIgnoreCase("shrink")) {
                    this.ShrinkBorder();
                    return true;
                }
                if (args[0].equalsIgnoreCase("reload")) {
                    this.config = YamlConfiguration.loadConfiguration(this.configFile);
                    this.loadConfig();
                    sender.sendMessage(this.messageReloaded);
                    return true;
                }
            }
            if (this.gameGoing) {
                sender.sendMessage(this.messageAlreadyStarted);
                return true;
            }
            this.world = Bukkit.getWorld(this.worldName);
            if (this.world != null) {
                this.inGame = (List<Player>) this.world.getPlayers();
                if (this.inGame.size() >= this.minPlayers) {
                    this.gameGoing = true;
                    this.playedSound = new ArrayList<String>();
                    this.startWorldBorderSize = this.world.getWorldBorder().getSize();
                    for (Player p3 : this.inGame) {
                        p3.teleport(this.world.getSpawnLocation());
                        p3.setGameMode(Main.gamemode);
                        p3.sendMessage(this.messageWelcome);
                        if (this.config.getBoolean("hideNameTags")) {
                            p3.setCustomNameVisible(false);
                        }
                        for (String command : this.playerStartCommands) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("\\{player}", p3.getName()));
                        }
                    }
                    for (String command2 : this.globalStartCommands) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command2);
                    }
                    (this.seeker = this.inGame.get(new Random().nextInt(this.inGame.size()))).sendMessage(this.messageSeeker);
                    for (Player p3 : this.inGame) {
                        if (p3.getUniqueId() != this.seeker.getUniqueId()) {
                            p3.sendMessage(this.messageIsTheSeeker.replaceAll("\\{seeker}", this.seeker.getDisplayName()));
                            p3.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, this.hideTime * 20, 0, false, false));
                            p3.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 0, false, false));
                            p3.getInventory().clear();
                        } else {
                            p3.sendMessage(this.messageYouAreTheSeeker.replaceAll("\\{time}", String.valueOf(this.hideTime)));
                            p3.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * this.hideTime, 10, false, false));
                            p3.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * this.hideTime, 255, false, false));
                            p3.getInventory().clear();
                            this.tasks.add(new BukkitRunnable() {
                                public void run() {
                                    Main.this.seeker.teleport(Main.this.world.getSpawnLocation());
                                    ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
                                    sword.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 200);
                                    sword.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
                                    Main.this.seeker.getInventory().setItem(0, sword);
                                }
                            });
                            this.tasks.get(this.tasks.size() - 1).runTaskLater((Plugin) this, (long) (20 * this.hideTime));
                            (this.freezeSeeker = new BukkitRunnable() {
                                public void run() {
                                    Location spawn = Main.this.world.getSpawnLocation();
                                    spawn.setPitch(90.0f);
                                    Main.this.seeker.teleport(spawn);
                                }
                            }).runTaskTimer((Plugin) this, 2L, 2L);
                        }
                    }
                    for (Integer time : this.announceTimeAt) {
                        this.tasks.add(new BukkitRunnable() {
                            public void run() {
                                for (Player p : Main.this.inGame) {
                                    p.sendMessage(Main.this.messageTimeLeft.replaceAll("\\{time}", String.valueOf(time)));
                                }
                            }
                        });
                        this.tasks.get(this.tasks.size() - 1).runTaskLater(this, 20L * (this.hideTime - time));
                    }
                    this.tasks.add(new BukkitRunnable() {
                        public void run() {
                            for (Player p : Main.this.inGame) {
                                p.sendMessage(Main.this.messageSeekerStarted);
                                Main.this.freezeSeeker.cancel();
                            }
                        }
                    });
                    this.tasks.get(this.tasks.size() - 1).runTaskLater(this, 20L * this.hideTime);
                    if (this.shrinking) {
                        this.tasks = new ArrayList<BukkitRunnable>();
                        for (Integer time : this.shrinkingAfter) {
                            this.tasks.add(new BukkitRunnable() {
                                public void run() {
                                    if (Main.this.gameGoing) {
                                        Main.this.ShrinkBorder();
                                    }
                                }
                            });
                            this.tasks.get(this.tasks.size() - 1).runTaskLater(this, 20L * time + 20L * this.hideTime);
                        }
                    }
                } else {
                    sender.sendMessage(this.messageNotEnoughPlayers.replaceAll("\\{world}", this.worldName));
                }
            } else {
                sender.sendMessage(this.messageNoWorld.replaceAll("\\{world}", this.worldName));
            }
            return true;
        } else {
            if (!cmd.getName().equalsIgnoreCase("sound")) {
                return false;
            }
            if (!this.sound) {
                sender.sendMessage(this.messageNoSound);
                return true;
            }
            if (!sender.hasPermission("sybhideandseek.sound")) {
                sender.sendMessage(this.messageSoundNoPermission);
                return true;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage(this.messageSoundPlayersOnly);
                return true;
            }
            if (!this.gameGoing) {
                sender.sendMessage(this.messageNotInAGame);
                return true;
            }
            Player p4 = (Player) sender;
            if (!this.inGame.contains(p4)) {
                sender.sendMessage(this.messageNotInAGame);
                return true;
            }
            if (p4.getGameMode() != Main.gamemode) {
                sender.sendMessage(this.messageNotAlive);
                return true;
            }
            if (this.seeker.getUniqueId() == p4.getUniqueId()) {
                sender.sendMessage(this.messageSoundNoSeeker);
                return true;
            }
            if (this.playedSound.contains(p4.getUniqueId().toString())) {
                sender.sendMessage(this.messageSoundCooldown);
                return true;
            }
            this.playedSound.add(p4.getUniqueId().toString());
            this.world.playSound(p4.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 2.0f, 0.1f);
            sender.sendMessage(this.messageSoundSurvive.replaceAll("\\{time}", String.valueOf(this.soundSurviveTime)));
            new BukkitRunnable() {
                public void run() {
                    if (Main.this.playedSound.contains(p4.getUniqueId().toString())) {
                        sender.sendMessage(Main.this.messageSoundSurvived);
                        Main.this.playedSound.remove(p4.getUniqueId().toString());
                        for (String command : Main.this.soundRewardCommands) {
                            Bukkit.getServer().dispatchCommand(Main.this.getServer().getConsoleSender(), command.replaceAll("\\{player}", p4.getName()));
                        }
                    }
                }
            }.runTaskLater(this, 20L * this.soundMinDelay);
            return true;
        }
    }
}
