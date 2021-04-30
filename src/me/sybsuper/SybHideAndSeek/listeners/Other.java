/*
 * Copyright (c) 2021 Sybsuper
 * All Rights Reserved
 *
 * Do not use this code without permission from the developer.
 */

package me.sybsuper.SybHideAndSeek.listeners;

import me.sybsuper.SybHideAndSeek.Main;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class Other implements Listener
{
    private static Main plugin;

    public Other(final Main plugin) {
        Other.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(final PlayerDropItemEvent e) {
        if (Other.plugin.disableItemDropping && Other.plugin.inGame.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(final BlockBreakEvent e) {
        if (Other.plugin.disableBlockBreaking && Other.plugin.gameGoing && e.getPlayer() != null && Other.plugin.inGame.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(final BlockPlaceEvent e) {
        if (Other.plugin.disableBlockPlacing && Other.plugin.gameGoing && e.getPlayer() != null && Other.plugin.inGame.contains(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
}
