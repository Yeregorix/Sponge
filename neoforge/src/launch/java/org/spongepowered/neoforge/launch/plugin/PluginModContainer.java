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
package org.spongepowered.neoforge.launch.plugin;

import com.google.inject.Injector;
import net.neoforged.bus.EventBusErrorMessage;
import net.neoforged.bus.api.BusBuilder;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.Logging;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.ModLoadingException;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.event.IModBusEvent;
import net.neoforged.fml.javafmlmod.AutomaticEventSubscriber;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.common.inject.plugin.PluginModule;
import org.spongepowered.common.launch.Launch;
import org.spongepowered.neoforge.launch.event.NeoEventManager;

import java.lang.reflect.InvocationTargetException;

// Spongified FMLModContainer
public final class PluginModContainer extends ModContainer {
    private static final Logger LOGGER = LogManager.getLogger();

    private final ModFileScanData scanResults;
    private final IEventBus eventBus;
    private final Class<?> modClass;
    private final Module module;

    public PluginModContainer(IModInfo info, String className, ModFileScanData modFileScanResults, ModuleLayer gameLayer) {
        super(info);
        LOGGER.debug(Logging.LOADING, "Creating PluginModContainer instance for {}", className);
        this.scanResults = modFileScanResults;
        this.eventBus = BusBuilder.builder()
            .setExceptionHandler(this::onEventFailed)
            .markerType(IModBusEvent.class)
            .allowPerPhasePost()
            .build();
        this.module = gameLayer.findModule(info.getOwningFile().moduleName()).orElseThrow();

        ModLoadingContext context = ModLoadingContext.get();
        try {
            context.setActiveContainer(this);
            this.modClass = Class.forName(this.module, className);
            LOGGER.trace(Logging.LOADING, "Loaded plugin class {} with {}", this.modClass.getName(), this.modClass.getClassLoader());
        } catch (Throwable e) {
            LOGGER.error(Logging.LOADING, "Failed to load class {}", className, e);
            throw new ModLoadingException(ModLoadingIssue.error("fml.modloadingissue.failedtoloadmodclass").withCause(e).withAffectedMod(info));
        } finally {
            context.setActiveContainer(null);
        }
    }

    private void onEventFailed(IEventBus iEventBus, Event event, EventListener[] iEventListeners, int i, Throwable throwable) {
        LOGGER.error(new EventBusErrorMessage(event, i, iEventListeners, throwable));
    }

    @Override
    protected void constructMod() {
        try {
            LOGGER.trace(Logging.LOADING, "Loading plugin instance {} of type {}", getModId(), this.modClass.getName());

            final NeoPluginContainer pluginContainer = NeoPluginContainer.of(this);
            final Injector childInjector = Launch.instance().lifecycle().platformInjector().createChildInjector(new PluginModule(pluginContainer, this.modClass));
            final Object modInstance = childInjector.getInstance(this.modClass);
            ((NeoEventManager) NeoForge.EVENT_BUS).registerListeners(pluginContainer, modInstance);
            pluginContainer.setInstance(modInstance);

            LOGGER.trace(Logging.LOADING, "Loaded plugin instance {} of type {}", getModId(), this.modClass.getName());
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = e.getCause();
            }
            LOGGER.error(Logging.LOADING, "Failed to create plugin instance. PluginID: {}, class {}", getModId(), this.modClass.getName(), e);
            throw new ModLoadingException(ModLoadingIssue.error("fml.modloadingissue.failedtoloadmod").withCause(e).withAffectedMod(modInfo));
        }

        try {
            LOGGER.trace(Logging.LOADING, "Injecting Automatic event subscribers for {}", getModId());
            AutomaticEventSubscriber.inject(this, this.scanResults, this.module);
            LOGGER.trace(Logging.LOADING, "Completed Automatic event subscribers for {}", getModId());
        } catch (Throwable e) {
            LOGGER.error(Logging.LOADING, "Failed to register automatic subscribers. ModID: {}", getModId(), e);
            throw new ModLoadingException(ModLoadingIssue.error("fml.modloadingissue.failedtoloadmod").withCause(e).withAffectedMod(modInfo));
        }
    }

    @Override
    public IEventBus getEventBus() {
        return this.eventBus;
    }
}
