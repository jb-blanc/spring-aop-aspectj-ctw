package fr.jibibi.springaspectj.apects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import fr.jibibi.springaspectj.application.LogMe;

@Aspect
public class JibibiAspects {

	@Around("execution(* fr.jibibi.springaspectj..*()) && @annotation(logme)")
	public Object intercept(ProceedingJoinPoint joinPoint, LogMe logme) throws Throwable {
        System.out.println("Before Method : " + logme.value());
		Object result = joinPoint.proceed();
        System.out.println("After Method : " + logme.value());
		return result;
    }
    
}