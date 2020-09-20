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
package org.spongepowered.common.mixin.inventory.api;

import com.google.common.base.Preconditions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.Hand;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.item.inventory.ArmorEquipable;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.common.bridge.inventory.InventoryBridge;
import org.spongepowered.common.inventory.adapter.InventoryAdapter;
import org.spongepowered.common.inventory.lens.impl.comp.EquipmentInventoryLens;
import org.spongepowered.common.item.util.ItemStackUtil;

import java.util.Optional;

import javax.annotation.Nullable;

// All living implementors of ArmorEquipable
@Mixin({PlayerEntity.class,
        ArmorStandEntity.class,
        MobEntity.class})
public abstract class TraitMixin_ArmorEquipable_Inventory_API implements ArmorEquipable {

    // TODO can we implement canEquip?
    // We might want to allow plugins to set any item
    // but we should least expose checks if an item can be equipped normally

    @Override
    public boolean canEquip(final EquipmentType type) {
        return true;
    }

    @Override
    public boolean canEquip(final EquipmentType type, @Nullable final ItemStack equipment) {
        return true;
    }

    @Override
    public Optional<ItemStack> getEquipped(final EquipmentType type) {
        final InventoryAdapter inv = ((InventoryBridge) this).bridge$getAdapter();
        final EquipmentInventoryLens lens = (EquipmentInventoryLens) inv.inventoryAdapter$getRootLens();
        return Optional.of(ItemStackUtil.fromNative(lens.getStack(inv.inventoryAdapter$getFabric(), ((EquipmentSlotType) (Object) type).getSlotIndex())));
    }

    @Override
    public boolean equip(final EquipmentType type, @Nullable final ItemStack equipment) {
        final InventoryAdapter inv = ((InventoryBridge) this).bridge$getAdapter();
        final EquipmentInventoryLens lens = (EquipmentInventoryLens) inv.inventoryAdapter$getRootLens();
        return lens.setStack(inv.inventoryAdapter$getFabric(), ((EquipmentSlotType) (Object) type).getSlotIndex(), ItemStackUtil.toNative(equipment));
    }

    @Override
    public ItemStack getItemInHand(HandType handType) {
        Preconditions.checkNotNull(handType);
        final net.minecraft.item.ItemStack nmsItem = ((LivingEntity) (Object)this).getHeldItem((Hand) (Object) handType);
        return ItemStackUtil.fromNative(nmsItem);
    }

    @Override
    public void setItemInHand(HandType handType, @Nullable ItemStack itemInHand) {
        Preconditions.checkNotNull(handType);
        ((LivingEntity) (Object)this).setHeldItem((Hand) (Object) handType, ItemStackUtil.toNative(itemInHand).copy());
    }

    @Override
    public ItemStack getHead() {
        return this.getEquipped(EquipmentTypes.HEAD).get();
    }

    @Override
    public void setHead(ItemStack head) {
        this.equip(EquipmentTypes.HEAD, head);
    }

    @Override
    public ItemStack getChest() {
        return this.getEquipped(EquipmentTypes.CHEST).get();
    }

    @Override
    public void setChest(ItemStack chest) {
        this.equip(EquipmentTypes.CHEST, chest);
    }

    @Override
    public ItemStack getLegs() {
        return this.getEquipped(EquipmentTypes.LEGS).get();
    }

    @Override
    public void setLegs(ItemStack legs) {
        this.equip(EquipmentTypes.LEGS, legs);
    }

    @Override
    public ItemStack getFeet() {
        return this.getEquipped(EquipmentTypes.FEET).get();
    }

    @Override
    public void setFeet(ItemStack feet) {
        this.equip(EquipmentTypes.FEET, feet);
    }
}