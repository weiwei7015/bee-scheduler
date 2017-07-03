package com.bee.lemon.web;

import com.bee.lemon.model.HttpResponseBodyWrapper;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;

@Controller
@ConditionalOnProperty(name = "cluster-registry")
public class ClusterController {

    @Autowired
    private CuratorFramework curatorFramework;

    @ResponseBody
    @RequestMapping("/cluster/nodes")
    HttpResponseBodyWrapper nodes() throws Exception {
        List<String> instances = curatorFramework.getChildren().forPath("/bee-scheduler/1.0/nodes");
        return new HttpResponseBodyWrapper(instances);
    }
}