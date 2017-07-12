package com.bee.scheduler.core.job;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache.KeyGenerator;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bee.scheduler.core.Constants;
import com.bee.scheduler.core.TaskExecutionContext;
import com.bee.scheduler.core.TaskExecutionLog;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * @author weiwei Dubbo客户端组件,该组件提供调用dubbo服务的功能
 */
public class DubboInvokerJobComponent extends JobComponent {
    private static Map<String, Class<?>> TYPE_ALIASES = new HashMap<>();

    static {
        TYPE_ALIASES.put("string", String.class);
        TYPE_ALIASES.put("byte", Byte.class);
        TYPE_ALIASES.put("long", Long.class);
        TYPE_ALIASES.put("short", Short.class);
        TYPE_ALIASES.put("int", Integer.class);
        TYPE_ALIASES.put("integer", Integer.class);
        TYPE_ALIASES.put("double", Double.class);
        TYPE_ALIASES.put("float", Float.class);
        TYPE_ALIASES.put("boolean", Boolean.class);

        TYPE_ALIASES.put("string[]", String[].class);
        TYPE_ALIASES.put("byte[]", Byte[].class);
        TYPE_ALIASES.put("long[]", Long[].class);
        TYPE_ALIASES.put("short[]", Short[].class);
        TYPE_ALIASES.put("int[]", Integer[].class);
        TYPE_ALIASES.put("integer[]", Integer[].class);
        TYPE_ALIASES.put("double[]", Double[].class);
        TYPE_ALIASES.put("float[]", Float[].class);
        TYPE_ALIASES.put("boolean[]", Boolean[].class);

        TYPE_ALIASES.put("_byte", byte.class);
        TYPE_ALIASES.put("_long", long.class);
        TYPE_ALIASES.put("_short", short.class);
        TYPE_ALIASES.put("_int", int.class);
        TYPE_ALIASES.put("_integer", int.class);
        TYPE_ALIASES.put("_double", double.class);
        TYPE_ALIASES.put("_float", float.class);
        TYPE_ALIASES.put("_boolean", boolean.class);

        TYPE_ALIASES.put("_byte[]", byte[].class);
        TYPE_ALIASES.put("_long[]", long[].class);
        TYPE_ALIASES.put("_short[]", short[].class);
        TYPE_ALIASES.put("_int[]", int[].class);
        TYPE_ALIASES.put("_integer[]", int[].class);
        TYPE_ALIASES.put("_double[]", double[].class);
        TYPE_ALIASES.put("_float[]", float[].class);
        TYPE_ALIASES.put("_boolean[]", boolean[].class);

        TYPE_ALIASES.put("date", Date.class);
        TYPE_ALIASES.put("decimal", BigDecimal.class);
        TYPE_ALIASES.put("bigdecimal", BigDecimal.class);
        TYPE_ALIASES.put("biginteger", BigInteger.class);
        TYPE_ALIASES.put("object", Object.class);

        TYPE_ALIASES.put("date[]", Date[].class);
        TYPE_ALIASES.put("decimal[]", BigDecimal[].class);
        TYPE_ALIASES.put("bigdecimal[]", BigDecimal[].class);
        TYPE_ALIASES.put("biginteger[]", BigInteger[].class);
        TYPE_ALIASES.put("object[]", Object[].class);

        TYPE_ALIASES.put("map", Map.class);
        TYPE_ALIASES.put("hashmap", HashMap.class);
        TYPE_ALIASES.put("list", List.class);
        TYPE_ALIASES.put("arraylist", ArrayList.class);
        TYPE_ALIASES.put("collection", Collection.class);
        TYPE_ALIASES.put("iterator", Iterator.class);
    }

    public static final KeyGenerator REFERENCE_CONFIG_CACHE_KEY_GENERATOR = new KeyGenerator() {
        @Override
        public String generateKey(ReferenceConfig<?> referenceConfig) {

            String iName = referenceConfig.getInterface();
            if (StringUtils.isBlank(iName)) {
                Class<?> clazz = referenceConfig.getInterfaceClass();
                iName = clazz.getName();
            }
            if (StringUtils.isBlank(iName)) {
                throw new IllegalArgumentException("No interface info in ReferenceConfig" + referenceConfig);
            }
            StringBuilder ret = new StringBuilder();
            if (referenceConfig.getRegistry() == null) {
                if (StringUtils.isNotBlank(referenceConfig.getUrl())) {
                    ret.append(referenceConfig.getUrl()).append("/");
                }
            } else {
                ret.append(referenceConfig.getRegistry()).append("/");
            }
            if (StringUtils.isNotBlank(referenceConfig.getGroup())) {
                ret.append(referenceConfig.getGroup()).append("/");
            }
            ret.append(iName);
            if (StringUtils.isNotBlank(referenceConfig.getVersion())) {
                ret.append(":").append(referenceConfig.getVersion());
            }
            return ret.toString();
        }
    };

    @Override
    public String getName() {
        return "DubboGenericInvoker";
    }

    @Override
    public String getVersion() {
        return "1.1";
    }

    @Override
    public String getAuthor() {
        return "weiwei";
    }

    @Override
    public String getDescription() {
        return "基于dubbo开发，支持多种远程调用协议（dubbo、rmi、hessian）";
    }

    @Override
    public String getParamTemplate() {
        StringBuilder t = new StringBuilder();
        t.append("{\r");
        t.append("    url:'',\r");
        t.append("    registry:'zookeeper://127.0.0.1:2181',\r");
        t.append("    service:'',\r");
        t.append("    version:'',\r");
        t.append("    group:'',\r");
        t.append("    method:'',\r");
        t.append("    timeout:10000,\r");
        t.append("    loadbalance:'random',\r");
        t.append("    params:[],\r");
        t.append("    paramsType:[]\r");
        t.append("}");
        return t.toString();
    }

    @Override
    public boolean run(TaskExecutionContext context) throws Exception {
        JSONObject taskParam = context.getTaskParam();
        TaskExecutionLog taskLogger = context.getLogger();

        String url = taskParam.getString("url");
        String registry = taskParam.getString("registry");
        String service = taskParam.getString("service");
        String version = taskParam.getString("version");
        String group = taskParam.getString("group");
        String method = taskParam.getString("method");
        Integer timeout = taskParam.getInteger("timeout");
        String loadbalance = taskParam.getString("loadbalance");

        JSONArray methodParamsType = JSONArray.parseArray(taskParam.getString("paramsType"));
        JSONArray methodParams = JSONArray.parseArray(taskParam.getString("params"));

        // RegistryConfig registry = new RegistryConfig();
        // registry.setAddress(taskParam.getString("registry"));
        // registry.setUsername("aaa");
        // registry.setPassword("bbb");

        // 泛化引用远程服务
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setApplication(new ApplicationConfig(Constants.SYSNAME));
        referenceConfig.setUrl(url);
        RegistryConfig registryConfig = new RegistryConfig(registry);
        referenceConfig.setRegistry(registryConfig);
        referenceConfig.setInterface(service);
        referenceConfig.setVersion(version);
        referenceConfig.setGroup(group);
        referenceConfig.setGeneric(true);
        referenceConfig.setTimeout(timeout);
        referenceConfig.setLoadbalance(loadbalance);

        ReferenceConfigCache referenceConfigCache = ReferenceConfigCache.getCache("DEFAULT", REFERENCE_CONFIG_CACHE_KEY_GENERATOR);
        GenericService genericService = referenceConfigCache.get(referenceConfig);
//             GenericService genericService = referenceConfig.get();

        // 解析类型别名
        JSONArray paramTypeJsonArray = methodParamsType;
        String[] paramTypeStrArray = new String[paramTypeJsonArray.size()];
        Class<?>[] paramTypeArray = new Class<?>[paramTypeJsonArray.size()];

        for (int i = 0; i < paramTypeJsonArray.size(); i++) {
            String type = paramTypeJsonArray.getString(i);
            String typeLowerCase = type.toLowerCase();
            paramTypeStrArray[i] = TYPE_ALIASES.containsKey(typeLowerCase) ? TYPE_ALIASES.get(typeLowerCase).getName() : type;
            paramTypeArray[i] = TYPE_ALIASES.containsKey(typeLowerCase) ? TYPE_ALIASES.get(typeLowerCase) : Map.class;
        }

        JSONArray argsJsonArray = methodParams;
        Object[] params = new Object[argsJsonArray.size()];
        for (int i = 0; i < argsJsonArray.size(); i++) {
            params[i] = argsJsonArray.getObject(i, paramTypeArray[i]);
        }

        Object result = genericService.$invoke(method, paramTypeStrArray, params);

        taskLogger.info("任务执行成功 -> return:" + result + "");
        return true;
    }
}
