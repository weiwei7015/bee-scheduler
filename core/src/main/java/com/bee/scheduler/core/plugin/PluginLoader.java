package com.bee.scheduler.core.plugin;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;

public class PluginLoader {
    private URLClassLoader pluginClassLoader;

    public PluginLoader(String jarFilePath, String libDir) {
        try {
            URL[] urls = new URL[2];
            urls[0] = ResourceUtils.getURL(jarFilePath);
            urls[1] = ResourceUtils.getURL(jarFilePath + File.separator + libDir);
            pluginClassLoader = new URLClassLoader(urls);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T load(String className) {
        try {
            return (T) pluginClassLoader.loadClass(className).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream loadResource(String name) {
        return pluginClassLoader.getResourceAsStream(name);
    }
}