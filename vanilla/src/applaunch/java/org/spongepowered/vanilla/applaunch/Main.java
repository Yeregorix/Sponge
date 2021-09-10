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
package org.spongepowered.vanilla.applaunch;

import com.google.common.collect.Lists;
import cpw.mods.modlauncher.Launcher;
import org.fusesource.jansi.AnsiConsole;
import org.spongepowered.common.applaunch.AppLaunch;
import org.spongepowered.common.applaunch.CorePlatform;
import org.spongepowered.common.applaunch.plugin.PluginPlatformConstants;
import org.spongepowered.plugin.blackboard.Keys;
import org.spongepowered.plugin.builtin.StandardEnvironment;
import org.spongepowered.plugin.builtin.jvm.JVMKeys;
import org.spongepowered.vanilla.applaunch.util.ArgumentList;

public final class Main {

    static {
        AnsiConsole.systemInstall();
    }

    public static void main(final String[] args) throws Exception {
        AppCommandLine.configure(args);

        final String implementationVersion = StandardEnvironment.class.getPackage().getImplementationVersion();

        // Build the environment
        final StandardEnvironment standardEnvironment = new StandardEnvironment();
        standardEnvironment.blackboard().getOrCreate(Keys.BASE_DIRECTORY, () -> AppCommandLine.gameDirectory);
        standardEnvironment.blackboard().getOrCreate(Keys.VERSION, () -> implementationVersion == null ? "dev" : implementationVersion);
        standardEnvironment.blackboard().getOrCreate(JVMKeys.METADATA_FILE_PATH, () -> PluginPlatformConstants.METADATA_FILE_LOCATION);

        final CorePlatform corePlatform = AppLaunch.setCorePlatform(new VanillaCorePlatform(standardEnvironment));
        standardEnvironment.blackboard().getOrCreate(Keys.PLUGIN_DIRECTORIES, () -> Lists.newArrayList(corePlatform.paths().pluginsDirectory()));

        AppLaunch.logger().info("Transitioning to ModLauncher, please wait...");
        final ArgumentList lst = ArgumentList.from(AppCommandLine.RAW_ARGS);
        Launcher.main(lst.getArguments());
    }
}
