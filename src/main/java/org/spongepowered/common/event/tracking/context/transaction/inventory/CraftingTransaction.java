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
package org.spongepowered.common.event.tracking.context.transaction.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;

import java.util.Optional;

public class CraftingTransaction extends ContainerBasedTransaction {

    final Player player;
     final ItemStack craftedStack;
    final CraftingInventory craftingInventory;
    final RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe> recipe;

    public CraftingTransaction(
        final Player player, @Nullable final ItemStack craftedStack, final CraftingInventory craftInv,
        @Nullable final RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe> lastRecipe
    ) {
        super(player.containerMenu);
        this.player = player;
        this.craftedStack = craftedStack;
        this.craftingInventory = craftInv;
        this.recipe = lastRecipe;
    }

    @Override
    public Optional<AbsorbingFlowStep> parentAbsorber() {
        return Optional.of((ctx, tx) -> tx.acceptCrafting(ctx, this));
    }

    @Override
    public boolean shouldHaveBeenAbsorbed() {
        return true;
    }
}
