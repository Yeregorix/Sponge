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
package org.spongepowered.common.data.processor.data.entity;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableActiveItemData;
import org.spongepowered.api.data.manipulator.mutable.entity.ActiveItemData;
import org.spongepowered.api.data.value.Value.Immutable;
import org.spongepowered.api.data.value.Value.Mutable;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.common.data.manipulator.mutable.entity.SpongeActiveItemData;
import org.spongepowered.common.data.processor.common.AbstractSingleDataSingleTargetProcessor;
import org.spongepowered.common.data.value.immutable.ImmutableSpongeValue;
import org.spongepowered.common.data.value.mutable.SpongeValue;
import org.spongepowered.common.item.util.ItemStackUtil;
import java.util.Optional;

public class ActiveItemDataProcessor extends AbstractSingleDataSingleTargetProcessor<LivingEntity, ItemStackSnapshot,
        Mutable<ItemStackSnapshot>, ActiveItemData, ImmutableActiveItemData> {

    public ActiveItemDataProcessor() {
        super(Keys.ACTIVE_ITEM, LivingEntity.class);
    }

    @Override
    protected boolean set(LivingEntity dataHolder, ItemStackSnapshot value) {
        if (value == null || value.isEmpty()) {
            dataHolder.stopActiveHand();
            return true;
        }
        return false;
    }

    @Override
    protected Optional<ItemStackSnapshot> getVal(LivingEntity dataHolder) {
        return Optional.of(ItemStackUtil.snapshotOf(dataHolder.getActiveItemStack()));
    }

    @Override
    protected Immutable<ItemStackSnapshot> constructImmutableValue(ItemStackSnapshot value) {
        return new ImmutableSpongeValue<>(Keys.ACTIVE_ITEM, value);
    }

    @Override
    protected Mutable<ItemStackSnapshot> constructValue(ItemStackSnapshot actualValue) {
        return new SpongeValue<>(Keys.ACTIVE_ITEM, actualValue);
    }

    @Override
    protected ActiveItemData createManipulator() {
        return new SpongeActiveItemData();
    }

    @Override
    public DataTransactionResult removeFrom(ValueContainer<?> container) {
        return DataTransactionResult.failNoData();
    }

}
