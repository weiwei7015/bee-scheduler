package com.bee.scheduler.core.plugin;

import com.bee.scheduler.core.job.JobComponent;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Manifest;

public class JobComponentLoader<T extends JobComponent> extends PluginLoader {
    private String jobComponentClassName;

    private static final String LIB_DIR = "META-INF/libs";

    public JobComponentLoader(String jobComponentJarPath) throws IOException {
        super(jobComponentJarPath, LIB_DIR);
        InputStream manifestIs = super.loadResource("META-INF/MANIFEST.MF");
        Manifest manifest = new Manifest(manifestIs);
        this.jobComponentClassName = manifest.getMainAttributes().getValue("JobComponent-Main-Class");
        manifestIs.close();
    }

    public T load() {
        return super.load(jobComponentClassName);
    }
}