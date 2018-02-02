//package com.bee.scheduler.core.job;
//
//import com.alibaba.fastjson.JSONObject;
//import com.bee.scheduler.core.JobExecutionContextUtil;
//import com.bee.scheduler.core.TaskExecutionContext;
//import com.bee.scheduler.core.TaskExecutionLog;
//import org.quartz.JobExecutionException;
//
///**
// * @author weiwei 该组件提供远程调用方法的功能
// */
//public class RMIJobComponent extends JobComponent {
//    @Override
//    public String getName() {
//        return "RMIJob";
//    }
//
//    @Override
//    public String getVersion() {
//        return "1.0";
//    }
//
//    @Override
//    public String getAuthor() {
//        return "weiwei";
//    }
//
//    @Override
//    public String getDescription() {
//        return "该组件提供远程调用方法的功能(暂不可用)";
//    }
//
//    @Override
//    public String getParamTemplate() {
//        StringBuilder t = new StringBuilder();
//        t.append("{\r");
//        t.append("    address:'',\r");
//        t.append("    method:''\r");
//        t.append("}");
//        return t.toString();
//    }
//
//    @Override
//    public boolean run(TaskExecutionContext context) throws JobExecutionException {
//        JSONObject taskParam = context.getTaskParam();
//        TaskExecutionLog taskLogger = context.getLogger();
//        taskLogger.info("RMIJob.execute()");
//        return true;
//    }
//
//}
