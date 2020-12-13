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
package org.spongepowered.common.mixin.core.entity.projectile;

import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.projectile.DamagingProjectile;
import org.spongepowered.api.entity.projectile.explosive.fireball.FireballEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.common.bridge.world.WorldBridge;
import org.spongepowered.common.event.SpongeCommonEventFactory;

@Mixin(DamagingProjectileEntity.class)
public abstract class DamagingProjectileEntityMixin extends ProjectileEntityMixin {

    @Redirect(method = "tick()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/DamagingProjectileEntity;onImpact(Lnet/minecraft/util/math/RayTraceResult;)V"))
    private void impl$callCollideImpactEvent(DamagingProjectileEntity projectile, RayTraceResult result) {
        if (result.getType() == RayTraceResult.Type.MISS || ((WorldBridge) this.world).bridge$isFake()) {
            this.shadow$onImpact(result);
            return;
        }

        if (SpongeCommonEventFactory.handleCollideImpactEvent(projectile, ((DamagingProjectile) this).get(Keys.SHOOTER).orElse(null), result)) {
            this.shadow$remove();
        } else {
            this.shadow$onImpact(result);
        }
    }
}
