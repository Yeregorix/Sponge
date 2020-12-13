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
package org.spongepowered.common.util;

import net.minecraft.potion.EffectInstance;
import org.spongepowered.api.effect.potion.PotionEffect;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class PotionEffectUtil {

    public static EffectInstance copy(final EffectInstance instance) {
        return new EffectInstance(instance.getEffect(), instance.getDuration(), instance.getAmplifier(),
                instance.isAmbient(), instance.isVisible(), instance.showIcon());
    }

    public static EffectInstance copyAsEffectInstance(final PotionEffect instance) {
        return PotionEffectUtil.copy((EffectInstance) instance);
    }

    public static List<EffectInstance> copyAsEffectInstances(final Collection<PotionEffect> effects) {
        return effects.stream().map(effect -> PotionEffectUtil.copy((EffectInstance) effect)).collect(Collectors.toList());
    }

    public static List<PotionEffect> copyAsPotionEffects(final Collection<EffectInstance> effects) {
        return effects.stream().map(effect -> (PotionEffect) PotionEffectUtil.copy(effect)).collect(Collectors.toList());
    }

    private PotionEffectUtil() {
    }
}
