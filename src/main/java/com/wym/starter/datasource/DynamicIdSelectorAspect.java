package com.wym.starter.datasource;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 切面
 *
 * @author lxr
 * @create 2018-04-27 17:49
 **/
@Aspect
@Component
public class DynamicIdSelectorAspect {

    @Pointcut("execution(public * com.wym.starter.datasource.spi.DynamicIdSelector(..))")
    public void webLog(){}
}
