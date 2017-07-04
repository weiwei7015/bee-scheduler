#bee-scheduler
##单机运行模式:
```
java -jar admin-xxx.jar --server.port=8080 --dburl="127.0.0.1:3306/bee-scheduler?user=root&password=root&useSSL=false"
```

##集群运行模式：
###1、运行一个管理节点（使用--clusterMode开启集群）：
```
java -jar admin-1.0-RELEASE.jar --server.port=8080 --dburl="127.0.0.1:3306/bee-scheduler?user=root&password=root&useSSL=false" --clusterMode
```

###2、使用cluster-node扩展节点
```
java -jar cluster-node-1.0-RELEASE.jar --dburl="127.0.0.1:3306/bee-scheduler?user=root&password=root&useSSL=false"
```