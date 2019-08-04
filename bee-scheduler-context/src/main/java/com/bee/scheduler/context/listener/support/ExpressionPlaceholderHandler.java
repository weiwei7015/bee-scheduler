package com.bee.scheduler.context.listener.support;

import com.alibaba.fastjson.JSONObject;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author weiwei
 */
public class ExpressionPlaceholderHandler {
    private ExpressionParser elParser = new SpelExpressionParser();
    private Pattern elSegPattern = Pattern.compile("\\$el\\(.+?\\)");
    public static final int EL_PREFFIX_LENGTH = 4;
    public static final int EL_SUFFIX_LENGTH = 1;

    public String handle(String originText, JSONObject variables) {
        EvaluationContext evaluationContext = new StandardEvaluationContext();
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
            String el = wrapped.substring(EL_PREFFIX_LENGTH, wrapped.length() - EL_SUFFIX_LENGTH);
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

//    public static void main(String[] args) {
//        ExpressionPlaceholderHandler handler = new ExpressionPlaceholderHandler();
//        JSONObject var = new JSONObject();
//        var.put("a", 12);
//        String result = handler.handle("=====$el( 1 + #a*2 )$el(2+3)asd", var);
//
//        System.out.println("result = " + result);
//    }
}
