package by.tms.tmsmyproject.config.aop;

import by.tms.tmsmyproject.utils.currentuser.CurrentUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* by.tms.tmsmyproject.services.impl.*.*(..))")
    public void services() {
    }

    @Pointcut("execution(* by.tms.tmsmyproject.services.impl.*.is*(..))")
    public void servicesIncludeMethod() {
    }

    @Pointcut(value = "execution(* by.tms.tmsmyproject.services.impl.*.create*(..))")
    public void servicesCreate() {
    }

    @Pointcut(value = "execution(* by.tms.tmsmyproject.services.impl.*.update*(..))")
    public void servicesUpdate() {
    }

    @Pointcut(value = "execution(* by.tms.tmsmyproject.services.impl.*.delete*(..))")
    public void servicesDelete() {
    }

    @Pointcut("execution(* by.tms.tmsmyproject.controllers.*.*(..))")
    public void controllers() {
    }

    @Before("services() && !servicesIncludeMethod() && !servicesDelete() && !servicesCreate() && !servicesUpdate()")
    public void beforeServices(JoinPoint joinPoint) {
        String login = CurrentUserUtils.getLoginInAspect();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.debug("user:{} call method:{} in class:{} with arguments:{}", login, methodName, className, args);
    }

    @AfterReturning(value = "servicesCreate() || servicesUpdate() || servicesDelete()", returning = "result")
    public void afterServicesCreate(JoinPoint joinPoint, Object result) {
        String login = CurrentUserUtils.getLoginInAspect();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.debug("user:{} call method:{} in class:{} with arguments:{} result:{}", login, methodName, className, args, result);
    }
}
