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
package org.spongepowered.vanilla.server.packs;

import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.apache.commons.io.FilenameUtils;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.common.launch.Launch;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.PluginResource;
import org.spongepowered.plugin.builtin.jvm.locator.JVMPluginResource;
import org.spongepowered.vanilla.bridge.server.packs.repository.PackRepositoryBridge_Vanilla;
import org.spongepowered.vanilla.launch.plugin.VanillaPluginManager;

import java.nio.file.FileSystem;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class PluginRepositorySource implements RepositorySource {

    private final PackRepository repository;

    public PluginRepositorySource(final PackRepository repository) {
        this.repository = repository;
    }

    @Override
    public void loadPacks(final Consumer<Pack> callback, final Pack.PackConstructor constructor) {
        final VanillaPluginManager pluginManager = (VanillaPluginManager) Launch.instance().pluginManager();

        // For each plugin, we create a pack. That pack might be empty.
        for (final PluginContainer pluginContainer : pluginManager.plugins()) {
            // The pack ID is prepended with "plugin-", as this will be the namespace we have to use a valid
            // character as a separator
            final String id = "plugin-" + pluginContainer.metadata().id();
            final PluginResource resource = pluginManager.resource(pluginContainer);
            // TODO: provide hook in the resource to return the file system for all resource types?
            //  Also -- can we fake a FileSystem for a non-Jar (needs thinking about)....
            @Nullable Supplier<FileSystem> fileSystemSupplier = null;
            if (resource instanceof JVMPluginResource) {
                final String extension = FilenameUtils.getExtension(resource.path().getFileName().toString());
                if ("jar".equals(extension)) {
                    fileSystemSupplier = ((JVMPluginResource) resource)::fileSystem;
                }
            }

            final PluginPackResources packResources = new PluginPackResources(id, pluginContainer, fileSystemSupplier);
            final Pack pack = Pack.create(id, true, () -> packResources, constructor, Pack.Position.BOTTOM, PackSource.DEFAULT);
            ((PackRepositoryBridge_Vanilla) this.repository).bridge$registerResourcePack(pluginContainer, pack);
            callback.accept(pack);
        }
    }
}
