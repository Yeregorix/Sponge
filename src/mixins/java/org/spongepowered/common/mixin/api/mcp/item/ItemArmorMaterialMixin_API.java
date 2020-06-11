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
package org.spongepowered.common.mixin.api.mcp.item;

import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.ArmorMaterial;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

@Mixin(net.minecraft.item.ArmorMaterial.class)
@Implements(@Interface(iface = ArmorMaterial.class, prefix = "apiArmor$"))
public abstract class ItemArmorMaterialMixin_API implements ArmorMaterial {
    // TODO support modded using IArmorMaterial

    @Shadow @Final private String name;
    @Shadow public abstract net.minecraft.item.crafting.Ingredient shadow$getRepairMaterial();

    private CatalogKey impl$key;

    @Override
    public CatalogKey getKey() {
        if (this.impl$key == null) {
            this.impl$key = CatalogKey.minecraft(this.name);
        }
        return this.impl$key;
    }

    @Override
    public Optional<Ingredient> getRepairIngredient() {
        final net.minecraft.item.crafting.Ingredient repairMaterial = this.shadow$getRepairMaterial();
        return Optional.ofNullable(((Ingredient) (Object) repairMaterial));
    }

}
