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
package org.spongepowered.common.mixin.core.inventory.bridge;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import org.spongepowered.api.event.item.inventory.CraftItemEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.query.QueryTypes;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.bridge.inventory.LensProviderBridge;
import org.spongepowered.common.bridge.inventory.TrackedContainerBridge;
import org.spongepowered.common.bridge.inventory.TrackedInventoryBridge;
import org.spongepowered.common.event.SpongeCommonEventFactory;
import org.spongepowered.common.inventory.adapter.InventoryAdapter;
import org.spongepowered.common.inventory.adapter.impl.slots.CraftingOutputAdapter;
import org.spongepowered.common.inventory.fabric.Fabric;
import org.spongepowered.common.inventory.lens.Lens;
import org.spongepowered.common.inventory.lens.impl.comp.CraftingInventoryLens;
import org.spongepowered.common.inventory.lens.impl.comp.PrimaryPlayerInventoryLens;
import org.spongepowered.common.inventory.lens.impl.minecraft.container.ContainerLens;
import org.spongepowered.common.inventory.lens.impl.slot.CraftingOutputSlotLens;
import org.spongepowered.common.inventory.lens.impl.slot.SlotLensCollection;
import org.spongepowered.common.inventory.lens.impl.slot.SlotLensProvider;
import org.spongepowered.common.item.util.ItemStackUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(WorkbenchContainer.class)
public abstract class ContainerWorkbenchMixin implements LensProviderBridge {

    @Override
    public Lens bridge$rootLens(final Fabric fabric, final InventoryAdapter adapter) {
        final List<Lens> lenses = new ArrayList<>();
        SlotLensProvider slotLensProvider = adapter.bridge$getSlotProvider();
        lenses.add(new CraftingInventoryLens(0, 1, 3, 3, slotLensProvider));
        lenses.add(new PrimaryPlayerInventoryLens(3 * 3 + 1, slotLensProvider, true));
        return new ContainerLens(adapter.bridge$getFabric().fabric$getSize(), (Class<? extends Inventory>) adapter.getClass(),
                slotLensProvider, lenses);
    }

    @Override
    public SlotLensProvider bridge$slotProvider(final Fabric fabric, final InventoryAdapter adapter) {
        final SlotLensCollection.Builder builder = new SlotLensCollection.Builder()
                .add(1, CraftingOutputAdapter.class, (i) -> new CraftingOutputSlotLens(i, (t) -> false, (t) -> false))
                .add(9)
                .add(36);
        builder.add(((WorkbenchContainer) (Object) this).inventorySlots.size() - 46);
        return builder.build();
    }
    /**
     * Captures the change in the crafting output slot
     *
     * old method name: Container#slotChangedCraftingGrid
     */
    @Inject(method = "func_217066_a", cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/CraftResultInventory;setInventorySlotContents(ILnet/minecraft/item/ItemStack;)V"))
    private static void beforeSlotChangedCraftingGrid(int p_217066_0_,
            final World world, final PlayerEntity player, final net.minecraft.inventory.CraftingInventory craftingInventory, final CraftResultInventory output,
            final CallbackInfo ci, ServerPlayerEntity serverPlayerEntity, ItemStack itemStack, Optional<ICraftingRecipe> optional) {
        TrackedInventoryBridge trackedContainer = (TrackedInventoryBridge) player.openContainer;
        TrackedContainerBridge container = (TrackedContainerBridge) player.openContainer;

        // Capture Inventory is true when caused by a vanilla inventory packet
        // This is to prevent infinite loops when a client mod re-requests the recipe result after we modified/cancelled it
        if (trackedContainer.bridge$capturingInventory()) {
            List<SlotTransaction> craftPreviewTransactions = container.bridge$getPreviewTransactions();
            craftPreviewTransactions.clear();
            final ItemStackSnapshot orig = ItemStackUtil.snapshotOf(output.getStackInSlot(0));
            final ItemStackSnapshot repl = ItemStackUtil.snapshotOf(itemStack);
            Slot slot = ((InventoryAdapter) player.openContainer).bridge$getSlot(0).get();
            craftPreviewTransactions.add(new SlotTransaction(slot, orig, repl));
        }
    }

    /**
     * Fires {@link CraftItemEvent.Preview} if active
     *
     * old method name: Container#slotChangedCraftingGrid
     */
    @Inject(method = "func_217066_a", cancellable = true,
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/play/ServerPlayNetHandler;sendPacket(Lnet/minecraft/network/IPacket;)V"))
    private static void afterSlotChangedCraftingGrid(int p_217066_0_,
            final World world, final PlayerEntity player, final net.minecraft.inventory.CraftingInventory craftingInventory, final CraftResultInventory output, final CallbackInfo ci) {

        TrackedContainerBridge container = (TrackedContainerBridge) player.openContainer;

        List<SlotTransaction> craftPreviewTransactions = container.bridge$getPreviewTransactions();
        if (container.bridge$firePreview() && !craftPreviewTransactions.isEmpty()) {
            // TODO can we just check the craftingInventory variable?
            final Inventory inv = ((Inventory) player.openContainer).query(QueryTypes.INVENTORY_TYPE.of(CraftingInventory.class));
            if (!(inv instanceof CraftingInventory)) {
                SpongeImpl.getLogger().warn("Detected crafting but Sponge could not get a CraftingInventory for " + player.openContainer.getClass().getName());
                return;
            }
            final SlotTransaction previewTransaction = craftPreviewTransactions.get(craftPreviewTransactions.size() - 1);

            final ICraftingRecipe recipe = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, craftingInventory, world).orElse(null);
            SpongeCommonEventFactory.callCraftEventPre(
                    player, ((CraftingInventory) inv), previewTransaction, ((CraftingRecipe) recipe),
                    player.openContainer, craftPreviewTransactions);
            craftPreviewTransactions.clear();
        }
    }

}
