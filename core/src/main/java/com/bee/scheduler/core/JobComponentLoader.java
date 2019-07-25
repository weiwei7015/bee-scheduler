package com.bee.scheduler.core;

import com.bee.scheduler.core.job.AbstractJobComponent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Manifest;

public class JobComponentLoader<T extends AbstractJobComponent> {

    private URLClassLoader classLoader;
    private String jobComponentClassName;

    public JobComponentLoader(String jarFilePath) throws IOException {
        classLoader = new URLClassLoader(new URL[]{new URL("file:" + jarFilePath)}, Thread.currentThread().getContextClassLoader());
        InputStream manifestIs = classLoader.getResourceAsStream("META-INF/MANIFEST.MF");
        Manifest manifest = new Manifest(manifestIs);
        this.jobComponentClassName = manifest.getMainAttributes().getValue("JobComponent-Impl-Class");
        if (manifestIs != null) {
            manifestIs.close();
        }
    }

    @SuppressWarnings("unchecked")
    public T load() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return (T) classLoader.loadClass(jobComponentClassName).newInstance();
    }
}