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
package org.spongepowered.neoforge.applaunch.loading.metadata;

import net.neoforged.neoforgespi.language.IConfigurable;
import net.neoforged.neoforgespi.language.IModInfo;
import org.spongepowered.plugin.metadata.PluginMetadata;
import org.spongepowered.plugin.metadata.model.PluginDependency;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// ModVersion
public final class PluginDependencyConfigurable implements IConfigurable {

    private final PluginMetadata metadata;
    private final PluginDependency dependency;

    public PluginDependencyConfigurable(final PluginMetadata metadata, final PluginDependency dependency) {
        this.metadata = metadata;
        this.dependency = dependency;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> getConfigElement(final String... key) {
        if (key.length != 1) {
            return Optional.empty();
        }

        return switch (key[0]) {
            case "modId" -> (Optional<T>) Optional.of(this.dependency.id());
            case "mandatory" -> (Optional<T>) Optional.of(!this.dependency.optional());
            case "versionRange" -> (Optional<T>) Optional.of(this.dependency.version().toString());
            case "ordering" -> (Optional<T>) Optional.of(this.loadToOrdering(this.dependency.loadOrder()).toString());
            case "side" -> (Optional<T>) Optional.of(IModInfo.DependencySide.BOTH.toString());
            default -> Optional.empty();
        };
    }

    @Override
    public List<? extends IConfigurable> getConfigList(final String... key) {
        return Collections.emptyList();
    }

    private IModInfo.Ordering loadToOrdering(final PluginDependency.LoadOrder order) {
        if (order == PluginDependency.LoadOrder.AFTER) {
            return IModInfo.Ordering.AFTER;
        }

        return IModInfo.Ordering.NONE;
    }
}
