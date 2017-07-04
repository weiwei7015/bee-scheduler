![BeeScheduler](admin-node/src/main/resources/public/app/img/logo.png "BeeScheduler")
#Bee-Scheduler
##运行前准备
- JAVA 1.8+
- Mysql

##开始
创建一个数据库用于存储任务数据（库名随意、UTF-8字符集），下文的bee-scheduler就是库名
##单机运行模式:
```shell
java -jar admin-node-xxx.jar --server.port=8080 --dburl="127.0.0.1:3306/bee-scheduler?user=root&password=root&useSSL=false"
```
浏览器访问：http://ip:port

##集群运行模式：
###1、运行一个管理节点（使用--clusterMode开启集群）：
```shell
java -jar admin-node-xxx.jar --server.port=8080 --dburl="127.0.0.1:3306/bee-scheduler?user=root&password=root&useSSL=false" --clusterMode
```

浏览器访问管理节点：http://ip:port **（注意：管理节点自身也是一个普通的调度节点）** 


###2、使用cluster-node扩展节点
```shell
java -jar cluster-node-xxx.jar --dburl="127.0.0.1:3306/bee-scheduler?user=root&password=root&useSSL=false"
```

启动调度节点后，会自动加入集群（基于db做注册），访问管理节点能看到集群信息