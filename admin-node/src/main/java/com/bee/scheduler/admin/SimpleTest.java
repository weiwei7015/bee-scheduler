package com.bee.scheduler.admin;


import com.bee.scheduler.core.Constants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import sun.swing.BakedArrayList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SimpleTest {
    private static final String PATH = "/example/queue";

    public static void main(String[] args) throws Exception {

        String shell = "cmd /c ping baidu.com";

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(shell);
        InputStream stderr = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(stderr);
        BufferedReader br = new BufferedReader(isr);
        String line;
        StringBuilder back = new StringBuilder();
        while ((line = br.readLine()) != null) {
            back.append(line);
        }


        System.out.println(back.toString());
    }
}
