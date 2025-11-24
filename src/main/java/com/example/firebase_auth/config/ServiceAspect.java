package com.example.firebase_auth.config;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceAspect {
   private static final Logger log = LoggerFactory.getLogger(ServiceAspect.class);

    // Pointcuts for each layer
    @Pointcut("execution(* com.example.firebase_auth.controller..*(..))")
    public void controllerLayer() {}

    @Pointcut("execution(* com.example.firebase_auth.service..*(..))")
    public void serviceLayer() {}

    @Pointcut("execution(* com.example.firebase_auth.repo..*(..))")
    public void repositoryLayer() {}

    // Around advice for controller, service, and repository layers
    @Around("controllerLayer() || serviceLayer() || repositoryLayer()")
    public Object logAppFlow(ProceedingJoinPoint joinPoint) throws Throwable {

        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("ENTER → {}.{}()", className, methodName);

        if (args.length > 0) {
            for (Object arg : args) {
                log.info("   Arg: {}", arg);
            }
        }

        Object result = null;
        long startTime = System.currentTimeMillis();

        try {
            result = joinPoint.proceed();

            long timeTaken = System.currentTimeMillis() - startTime;
            if (result != null) {
                log.info(" EXIT  ← {}.{}() | Result: {} | Time: {} ms", className, methodName, result, timeTaken);
            } else {
                log.info("EXIT  ← {}.{}() | Returned null | Time: {} ms", className, methodName, timeTaken);
            }

        } catch (Exception ex) {
            log.error(" ERROR in {}.{}() : {}", className, methodName, ex.getMessage(), ex);
            throw ex;
        }

        return result;
    }
}
