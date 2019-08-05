package com.bee.scheduler.context;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author weiwei
 */
public class ExpressionPlaceholderHandler {
    private ExpressionParser elParser = new SpelExpressionParser();
    private Pattern elSegPattern = Pattern.compile("<el>.+?</el>");
    private static final int EL_PREFIX_LENGTH = 4;
    private static final int EL_SUFFIX_LENGTH = 5;

    public String handle(String originText, JSONObject variables) {
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        //全局参数
        evaluationContext.setVariable("time", new Date());
        evaluationContext.setVariable("jsonObject", new JSONObject());
        evaluationContext.setVariable("jsonArray", new JSONArray());
        //上下文参数
        if (variables != null) {
            variables.keySet().forEach(key -> {
                evaluationContext.setVariable(key, variables.get(key));
            });
        }
        Matcher matcher = elSegPattern.matcher(originText);


        StringBuilder resolvedText = new StringBuilder();
        int pos = 0;
        while (matcher.find()) {
            int start = matcher.start(), end = matcher.end();
            String wrapped = matcher.group();
            String el = wrapped.substring(EL_PREFIX_LENGTH, wrapped.length() - EL_SUFFIX_LENGTH);
            String value = elParser.parseExpression(el).getValue(evaluationContext, String.class);
            if (start > pos) {
                resolvedText.append(originText, pos, start);
            }
            resolvedText.append(value);
            pos = end;
        }
        if (pos < originText.length()) {
            resolvedText.append(originText, pos, originText.length());
        }

        return resolvedText.toString();
    }

    public boolean containsExpression(String originText) {
        Matcher matcher = elSegPattern.matcher(originText);
        return matcher.find();
    }

//    public static void main(String[] args) {
//        ExpressionParser parser = new SpelExpressionParser();
//        EvaluationContext context = new StandardEvaluationContext();
//        Expression expression = parser.parseExpression("#Json.parseObject('{a:1,b:2}')");
//        Object result = expression.getValue(context);
//        System.out.println("result = " + result);
//    }

}
