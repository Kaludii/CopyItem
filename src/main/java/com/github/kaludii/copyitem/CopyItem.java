package com.github.kaludii.copyitem;

import org.bstats.bukkit.Metrics;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.RayTraceResult;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class CopyItem extends JavaPlugin implements CommandExecutor, TabCompleter {
    private FileConfiguration messagesConfig;
    private String prefix;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createMessagesFile();
        this.getCommand("copyitem").setExecutor(this);
        this.getCommand("copyitem").setTabCompleter(this);
        if (getConfig().getBoolean("settings.bStatsEnabled")) {
            int pluginId = 19247;
            Metrics metrics = new Metrics(this, pluginId);
        }
    }

    @Override
    public void onDisable() {
    }

    private void createMessagesFile() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            messagesFile.getParentFile().mkdirs();
            saveResource("messages.yml", false);
        }

        messagesConfig = new YamlConfiguration();
        try {
            messagesConfig.load(messagesFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        prefix = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.prefix"));
    }

    private void setupMessages() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("copyitem") && sender.hasPermission("copyitem.use")) {
            if (args.length == 1) {
                return Arrays.asList("hand", "sight", "help", "reload");
            } else if (args.length == 2 && (args[0].equalsIgnoreCase("hand") || args[0].equalsIgnoreCase("sight"))) {
                return IntStream.rangeClosed(1, getConfig().getInt("settings.maxCopyAmount"))
                        .mapToObj(Integer::toString)
                        .collect(Collectors.toList());
            }
        }

        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String prefix = ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.prefix"));
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("copyitem.reload")) {
                    reloadConfig();
                    setupMessages();
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.configReloaded")));
                } else {
                    sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.noPermission")));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.copyitemHelp")));
                return true;
            }
        }

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("copyitem.use")) {
                if (args.length == 2) {
                    try {
                        int amount = Integer.parseInt(args[1]);
                        int maxCopyAmount = getConfig().getInt("settings.maxCopyAmount");
                        if (amount > maxCopyAmount) {
                            player.sendMessage(prefix + "You can't copy more than " + maxCopyAmount + " items at once.");
                            return true;
                        }
                        if (args[0].equalsIgnoreCase("hand")) {
                            ItemStack itemToCopy = player.getInventory().getItemInMainHand();
                            if (itemToCopy.getType() != Material.AIR) {
                                ItemStack itemClone = itemToCopy.clone(); // Clone the item before modifying it
                                itemClone.setAmount(amount);
                                HashMap<Integer, ItemStack> noRoom = player.getInventory().addItem(itemClone);
                                if (!noRoom.isEmpty()) { // If there was no room for some items, drop them at the player's location
                                    for (ItemStack item : noRoom.values()) {
                                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                                    }
                                }
                                String successMessage = String.format(messagesConfig.getString("messages.copiedSuccessfully"), itemToCopy.getType(), amount);
                                player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', successMessage));
                                if (getConfig().getBoolean("settings.announceCommandUsage")) {
                                    String commandUsedMessage = String.format(messagesConfig.getString("messages.commandUsed"), player.getName(), itemToCopy.getType(), amount);
                                    getLogger().info(ChatColor.stripColor(commandUsedMessage));
                                    for (Player op : getServer().getOnlinePlayers()) {
                                        if (op.isOp() && !op.equals(player)) {
                                            op.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', commandUsedMessage));
                                        }
                                    }
                                }
                            } else {
                                player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.noItemInHand")));
                            }
                        } else if (args[0].equalsIgnoreCase("sight")) {
                            RayTraceResult result = player.getWorld().rayTraceBlocks(player.getEyeLocation(),
                                    player.getLocation().getDirection(), getConfig().getInt("settings.sightMaxDistance"));
                            if (result != null) {
                                Block block = result.getHitBlock();
                                ItemStack itemToCopy = new ItemStack(block.getType(), amount);
                                HashMap<Integer, ItemStack> noRoom = player.getInventory().addItem(itemToCopy);
                                if (!noRoom.isEmpty()) { // If there was no room for some items, drop them at the player's location
                                    for (ItemStack item : noRoom.values()) {
                                        player.getWorld().dropItemNaturally(player.getLocation(), item);
                                    }
                                }
                                String successMessage = String.format(messagesConfig.getString("messages.copiedSuccessfully"), block.getType(), amount);
                                player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', successMessage));
                                if (getConfig().getBoolean("settings.announceCommandUsage")) {
                                    String commandUsedMessage = String.format(messagesConfig.getString("messages.commandUsed"), player.getName(), block.getType(), amount);
                                    getLogger().info(ChatColor.stripColor(commandUsedMessage));
                                    for (Player op : getServer().getOnlinePlayers()) {
                                        if (op.isOp() && !op.equals(player)) {
                                            op.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', commandUsedMessage));
                                        }
                                    }
                                }
                            } else {
                                player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.noBlockInView")));
                            }
                        } else {
                            player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.invalidArgument")));
                        }

                    } catch (NumberFormatException e) {
                        player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.invalidNumberFormat")));
                    }

                } else {
                    player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.invalidCommandUsage")));
                }
            } else {
                player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.noPermission")));
            }
        } else {
            sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', messagesConfig.getString("messages.onlyPlayersCommand")));
        }

        return true;
    }
}