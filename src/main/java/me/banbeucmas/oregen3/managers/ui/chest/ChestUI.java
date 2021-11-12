/*
 * This file is part of FeatherPowders, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2021 MangoPlex
 * Copyright (c) Just Mango (JustMangoT), nahkd (nahkd123), GalaxyVN (Galaxy-VN)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.banbeucmas.oregen3.managers.ui.chest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class ChestUI {
    protected Player player;
    protected Inventory inventory;
    private SlotHandler[] handlers;

    private boolean cancelDragEvent = false;

    public ChestUI(Player targetedPlayer, String title, int rows) {
        player = targetedPlayer;
        inventory = Bukkit.createInventory(null, rows * 9, title);
        handlers = new SlotHandler[rows * 9];
    }

    public void setCancelDragEvent(boolean b) {
        this.cancelDragEvent = b;
    }

    public boolean isDragEventCanceled() {
        return this.cancelDragEvent;
    }

    public Inventory getInventory() { return inventory; }

    public void clearSlot(int x, int y) {
        inventory.setItem(y * 9 + x, null);
        handlers[y * 9 + x] = null;
    }

    public void set(int x, int y, ItemStack item, SlotHandler handler) {
        inventory.setItem(y * 9 + x, item);
        handlers[y * 9 + x] = handler;
    }

    public abstract void failback(InventoryClickEvent clickEvent);

    public void passClickEvent(InventoryClickEvent event) {
        if (event.getClickedInventory() != inventory) {
            event.setCancelled(true);
            return;
        }

        int slot = event.getSlot();
        if (handlers[slot] == null) failback(event);
        else handlers[slot].onClick(event);
    }

}
