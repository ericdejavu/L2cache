package com.abyss.l2cache.common.aspect;

import com.abyss.l2cache.common.annotation.ClearCache;
import io.micrometer.core.instrument.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author wujx37877
 * @Description 清除缓存的切面
 * @Date 13:15 2021/5/17
 */
@Aspect
@Order(20)
@Component
public class CacheAspect {
    @Autowired
    private CacheManager cacheManager;

    @Pointcut("@annotation(com.abyss.l2cache.common.annotation.ClearCache)")
    public void annotationPointCut() {

    }

    @After("annotationPointCut()")
    public void after(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String[] params = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        // 表达式的上下文
        EvaluationContext context = new StandardEvaluationContext();

        for (int i = 0; i < args.length; i++) {
            // 为了让表达式可以访问该对象, 先把对象放到上下文中
            context.setVariable(params[i], args[i]);
        }

        ClearCache annotation = method.getAnnotation(ClearCache.class);
        String condition = annotation.condition();
        //条件判断
        if (StringUtils.isNotBlank(condition)) {
            ExpressionParser parser = new SpelExpressionParser();
            Boolean flag = parser.parseExpression(condition).getValue(context, Boolean.class);
            if (!flag) return;
        }


        //缓存清除操作
        clear(annotation);
    }

    /**
     * 清空所有缓存
     */
    public void clear(ClearCache annotation) {


        //是否清除所有键
        boolean b = annotation.allEntries();

        //缓存区
        String[] cacheNames = annotation.value();

        List<String> list = new ArrayList<>();

        for(String str : cacheNames){
            if(StringUtils.isNotEmpty(str)){
                list.addAll(Arrays.asList(str.split(",")));
            }
        }


        if (list.isEmpty() && b) {
            list = new ArrayList<>(cacheManager.getCacheNames());
            for (String cn : list) {
                cacheManager.getCache(cn).clear();
            }
            return;
        }

        for (String name : list) {
            cacheManager.getCache(name).clear();
        }
    }
}
