/*
 * Copyright (C) 2011-2020 lishid. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.lishid.openinv.listeners;

import com.lishid.openinv.IOpenInv;
import com.lishid.openinv.util.InventoryAccess;
import com.lishid.openinv.util.Permissions;
import org.bukkit.GameMode;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;

/**
 * Listener for inventory-related events to prevent modification of inventories where not allowed.
 *
 * @author Jikoo
 */
public class InventoryListener implements Listener {

    private final IOpenInv plugin;

    public InventoryListener(final IOpenInv plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();

        if (this.plugin.getPlayerSilentChestStatus(player)) {
            this.plugin.getAnySilentContainer().deactivateContainer(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryClick(InventoryClickEvent event) {
        onInventoryInteract(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        onInventoryInteract(event);
    }

    private void onInventoryInteract(InventoryInteractEvent event) {
        HumanEntity entity = event.getWhoClicked();

        if (Permissions.SPECTATE.hasPermission(entity) && entity.getGameMode() == GameMode.SPECTATOR) {
            event.setCancelled(false);
        }

        if (event.isCancelled()) {
            return;
        }

        Inventory inventory = event.getInventory();

        if (InventoryAccess.isPlayerInventory(inventory)) {
            if (!Permissions.EDITINV.hasPermission(entity)) {
                event.setCancelled(true);
            }
        } else if (InventoryAccess.isEnderChest(inventory)) {
            if (!Permissions.EDITENDER.hasPermission(entity)) {
                event.setCancelled(true);
            }
        }
    }

}
