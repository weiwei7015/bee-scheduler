package com.bee.scheduler.daemonnode.core;

import com.bee.scheduler.core.ExecutorModule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.ExplodedArchive;
import org.springframework.boot.loader.archive.JarFileArchive;
import org.springframework.boot.loader.jar.JarFile;
import org.springframework.util.ClassUtils;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

public class ClassPathJarArchiveTaskModuleLoader implements TaskModuleLoader {
    private Log logger = LogFactory.getLog(ClassPathJarArchiveTaskModuleLoader.class);

    @Override
    public List<ExecutorModule> load() throws Exception {
        JarFile.registerUrlProtocolHandler();
        Archive sourceCodeArchive = createArchive();
        List<Archive> taskModulesArchive = sourceCodeArchive.getNestedArchives(entry -> {
            if (entry.isDirectory()) {
                return false;
            } else {
                return entry.getName().contains("task_modules/");
            }
        });

        ArrayList<ExecutorModule> taskModules = new ArrayList<>();
        for (Archive archive : taskModulesArchive) {
            ArrayList<URL> urls = new ArrayList<>();
            urls.add(archive.getUrl());
            List<Archive> nestedArchives = archive.getNestedArchives(entry -> !entry.isDirectory() && entry.getName().startsWith("lib/"));
            for (Archive item : nestedArchives) {
                urls.add(item.getUrl());
            }
            String moduleClass = archive.getManifest().getMainAttributes().getValue("TaskModuleClass");
            ClassLoader classLoader = new LaunchedURLClassLoader(urls.toArray(new URL[0]), ClassUtils.getDefaultClassLoader());

            ExecutorModule module = (ExecutorModule) classLoader.loadClass(moduleClass).newInstance();

            taskModules.add(module);
        }
        return taskModules;
    }


    private Archive createArchive() throws Exception {
        ProtectionDomain protectionDomain;
        ClassLoader classLoader = ClassUtils.getDefaultClassLoader();
        if (classLoader instanceof LaunchedURLClassLoader) {
            protectionDomain = ((LaunchedURLClassLoader) classLoader).getClass().getProtectionDomain();
        } else {
            protectionDomain = getClass().getProtectionDomain();
        }
        CodeSource codeSource = protectionDomain.getCodeSource();
        URI location = (codeSource != null) ? codeSource.getLocation().toURI() : null;
        String path = (location != null) ? location.getSchemeSpecificPart() : null;
        if (path == null) {
            throw new IllegalStateException("Unable to determine code source archive");
        }
        File root = new File(path);
        if (!root.exists()) {
            throw new IllegalStateException("Unable to determine code source archive from " + root);
        }
        return (root.isDirectory() ? new ExplodedArchive(root) : new JarFileArchive(root));
    }
}
