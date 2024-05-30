/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.common.mixin.inventory.api.world.inventory;


import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.common.inventory.adapter.InventoryAdapter;
import org.spongepowered.common.inventory.fabric.Fabric;
import org.spongepowered.common.inventory.lens.impl.comp.CraftingGridInventoryLens;

@Mixin(CrafterBlockEntity.class)
public abstract class CrafterBlockBlockEntityMixin_Inventory_API implements CraftingGridInventory {

    private GridInventory api$gridAdapter;

    @Override
    public GridInventory asGrid() {
        // override with caching
        final CraftingGridInventoryLens lens = (CraftingGridInventoryLens) ((InventoryAdapter) this).inventoryAdapter$getRootLens();
        if (this.api$gridAdapter == null) {
            this.api$gridAdapter = (GridInventory) lens.getGrid().getAdapter(((Fabric) this), this);
        }
        return this.api$gridAdapter;
    }

}