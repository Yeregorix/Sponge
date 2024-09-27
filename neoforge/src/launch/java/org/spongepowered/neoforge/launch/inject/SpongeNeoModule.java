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
package org.spongepowered.neoforge.launch.inject;

import com.google.inject.AbstractModule;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.api.Platform;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.common.command.manager.SpongeCommandManager;
import org.spongepowered.neoforge.launch.NeoPlatform;
import org.spongepowered.neoforge.launch.command.NeoCommandManager;
import org.spongepowered.neoforge.launch.event.NeoEventManager;

public final class SpongeNeoModule extends AbstractModule {

    @Override
    protected void configure() {
        this.bind(Platform.class).to(NeoPlatform.class);
        this.bind(EventManager.class).toProvider(() -> (NeoEventManager) NeoForge.EVENT_BUS);
        this.bind(SpongeCommandManager.class).to(NeoCommandManager.class);
    }

}
