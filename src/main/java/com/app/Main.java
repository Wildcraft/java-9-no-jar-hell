package com.app;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Layer;
import java.nio.file.Paths;
import java.util.ServiceLoader;
import java.util.Set;

public class Main {
    /**
     * Create a new {@link java.lang.reflect.Layer} for a module on a path.
     *
     * @param modulePath path where the module is located
     * @param moduleName name of the module
     */
    private static Layer createLayer(String modulePath, String moduleName) {
        final ModuleFinder finder = ModuleFinder.of(Paths.get(modulePath));
        final Layer parent = Layer.boot();
        final Configuration newCfg =
                parent.configuration().resolveRequires(finder, ModuleFinder.of(), Set.of(moduleName));
        return parent.defineModulesWithOneLoader(newCfg, ClassLoader.getSystemClassLoader());
    }

    /**
     * Load the module com.greetings multiple times from the paths specified on the command line. Create a layer for
     * each path. Load a service of type {@link java.lang.Runnable} for each layer and execute each one.
     * <p/>
     * We are using {@link java.util.ServiceLoader} here because it is type safe and easier to use than low level
     * reflection.
     */
    public static void main(String[] args) {
        for (String modulePath : args) {
            final Layer layer = createLayer(modulePath, "com.greetings");
            final ServiceLoader<Runnable> serviceLoader = ServiceLoader.load(layer, Runnable.class);

            System.out.format("Loaded module 'com.greetings' from path '%s'. Will run its service(s) now:\n", modulePath);
            serviceLoader.forEach(Runnable::run);
            System.out.println();
        }
    }
}
