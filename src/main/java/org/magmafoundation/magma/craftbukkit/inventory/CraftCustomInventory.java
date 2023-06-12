/*
 * Magma Server
 * Copyright (C) 2019-2023.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.magmafoundation.magma.craftbukkit.inventory;

import net.minecraft.world.Container;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.*;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryCustom;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * CraftCustomInventory
 *
 * @author Hexeption admin@hexeption.co.uk
 * @since 06/10/2020 - 04:23 am
 */
public class CraftCustomInventory implements InventoryHolder {

    private final CraftInventory container;

    public CraftCustomInventory(Container inventory) {
        this.container = new CraftInventory(inventory);
    }

    public CraftCustomInventory(ItemStackHandler handler) {
//        this.container = new CraftInventoryCustom(this, handler.getStacks());
        this.container = null;
    }

    @Nullable
    public static InventoryHolder getHolder(IItemHandler handler) {
        if (handler == null) {
            return null;
        }
        if (handler instanceof ItemStackHandler) {
            return new CraftCustomInventory((ItemStackHandler) handler);
        }
        if (handler instanceof SlotItemHandler) {
            return new CraftCustomInventory(((SlotItemHandler) handler).container);
        }
        if (handler instanceof InvWrapper) {
            return new CraftCustomInventory(((InvWrapper) handler).getInv());
        }
        if (handler instanceof SidedInvWrapper) {
//            return new CraftCustomInventory(((SidedInvWrapper) handler).getInventory());
        }
        if (handler instanceof PlayerInvWrapper) {
            IItemHandlerModifiable[] playerInventoryWrapper = ObfuscationReflectionHelper.getPrivateValue(CombinedInvWrapper.class, (PlayerInvWrapper) handler, "itemHandler");
            for (IItemHandlerModifiable itemHandler : playerInventoryWrapper) {
                if (itemHandler instanceof PlayerMainInvWrapper) {
                    return new CraftCustomInventory(((PlayerMainInvWrapper) itemHandler).getInventoryPlayer());
                }
                if (itemHandler instanceof PlayerArmorInvWrapper) {
                    return new CraftCustomInventory(((PlayerArmorInvWrapper) itemHandler).getInventoryPlayer());
                }
            }
        }
        return null;
    }

    @Nullable
    public static Inventory getCustomInventory(IItemHandler handler) {
        InventoryHolder holder = getHolder(handler);
        return holder != null ? holder.getInventory() : null;
    }

    @Override
    public Inventory getInventory() {
        return this.container;
    }

    public static List<HumanEntity> getViewers(Container inventory) {
        try {
            return inventory.getViewers();
        } catch (AbstractMethodError e) {
            return new ArrayList<HumanEntity>();
        }
    }
}
