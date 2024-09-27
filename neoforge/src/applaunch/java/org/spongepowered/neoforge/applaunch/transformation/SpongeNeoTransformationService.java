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
package org.spongepowered.neoforge.applaunch.transformation;

import cpw.mods.jarhandling.SecureJar;
import cpw.mods.modlauncher.Environment;
import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.IModuleLayerManager;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.TypesafeMap;
import joptsimple.OptionSpec;
import joptsimple.OptionSpecBuilder;
import net.neoforged.fml.loading.LoadingModList;
import net.neoforged.fml.loading.moddiscovery.ModFileInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.transformers.modlauncher.AccessWidenerTransformationService;
import org.spongepowered.transformers.modlauncher.SuperclassChanger;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.jar.Attributes;

public class SpongeNeoTransformationService implements ITransformationService {
    private static final Logger LOGGER = LogManager.getLogger();

    private OptionSpec<Boolean> checkHashes;
    private OptionSpec<String> librariesDirectoryName;

    @NonNull
    @Override
    public String name() {
        return "spongeneo";
    }

    @Override
    public void arguments(final BiFunction<String, String, OptionSpecBuilder> argumentBuilder) {
        this.checkHashes = argumentBuilder.apply("checkHashes", "Whether to check hashes when downloading libraries")
            .withOptionalArg()
            .ofType(Boolean.class)
            .defaultsTo(true);
        this.librariesDirectoryName = argumentBuilder.apply("librariesDir", "The directory to download SpongeNeo libraries to")
            .withOptionalArg()
            .ofType(String.class)
            .defaultsTo("sponge-libraries");
    }

    @Override
    public void argumentValues(final OptionResult option) {
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Keys.CHECK_LIBRARY_HASHES.get(), $ -> option.value(this.checkHashes));
        Launcher.INSTANCE.environment().computePropertyIfAbsent(Keys.LIBRARIES_DIRECTORY.get(),
            $ -> Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.GAMEDIR.get())
            .orElseThrow(() -> new IllegalStateException("No game directory was available"))
            .resolve(option.value(this.librariesDirectoryName)));
    }

    @Override
    public void initialize(final IEnvironment environment) {
    }

    @Override
    public void onLoad(final IEnvironment env, final Set<String> otherServices) {
    }

    @Override
    public List<Resource> completeScan(IModuleLayerManager layerManager) {
        final Environment env = Launcher.INSTANCE.environment();
        final AccessWidenerTransformationService accessWidener = env.getProperty(AccessWidenerTransformationService.INSTANCE.get()).orElse(null);
        final SuperclassChanger superclassChanger = env.getProperty(SuperclassChanger.INSTANCE.get()).orElse(null);

        if (accessWidener != null || superclassChanger != null) {
            for (ModFileInfo fileInfo : LoadingModList.get().getModFiles()) {
                final SecureJar jar = fileInfo.getFile().getSecureJar();
                final Attributes attributes = jar.moduleDataProvider().getManifest().getMainAttributes();

                if (accessWidener != null) {
                    final String awPaths = attributes.getValue("Access-Widener");
                    if (awPaths != null) {
                        final String jarFileName = fileInfo.getFile().getFileName();
                        LOGGER.debug("Registering access wideners from {} ...", jarFileName);

                        for (final String awPath : awPaths.split(",")) {
                            try {
                                accessWidener.offerResource(jar.getPath(awPath).toUri().toURL(), awPath);
                            } catch (final MalformedURLException e) {
                                LOGGER.warn("Failed to register access widener {} from {}", awPath, jarFileName, e);
                            }
                        }
                    }
                }

                if (superclassChanger != null) {
                    final String scPaths = attributes.getValue("Superclass-Transformer");
                    if (scPaths != null) {
                        final String jarFileName = fileInfo.getFile().getFileName();
                        LOGGER.debug("Registering superclass changers from {} ...", jarFileName);

                        for (final String scPath : scPaths.split(",")) {
                            try {
                                superclassChanger.offerResource(jar.getPath(scPath).toUri().toURL(), scPath);
                            } catch (final MalformedURLException e) {
                                LOGGER.warn("Failed to register superclass changer {} from {}", scPath, jarFileName, e);
                            }
                        }
                    }
                }
            }
        }

        return List.of();
    }

    @NonNull
    @Override
    public List<? extends ITransformer<?>> transformers() {
        return List.of(new ListenerTransformer());
    }

    public static final class Keys {

        public static final Supplier<TypesafeMap.Key<Boolean>> CHECK_LIBRARY_HASHES = IEnvironment.buildKey("sponge:check_library_hashes", Boolean.class);
        public static final Supplier<TypesafeMap.Key<Path>> LIBRARIES_DIRECTORY = IEnvironment.buildKey("sponge:libraries_directory", Path.class);

        private Keys() {
        }

    }
}
