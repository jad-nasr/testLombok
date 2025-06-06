package com.testApplication.security;

import com.testApplication.model.BusinessObject;
import com.testApplication.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.testApplication.model.BusinessObject;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
@RequiredArgsConstructor
public class LegalEntitySecurityAspect {

    private final SecurityService securityService;

    @Before("@annotation(com.testApplication.security.RequiresLegalEntityAccess)")
    public void checkLegalEntityAccess(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiresLegalEntityAccess annotation = method.getAnnotation(RequiresLegalEntityAccess.class);
        
        String paramName = annotation.legalEntityIdParam();
        Long legalEntityId = extractLegalEntityId(joinPoint, signature.getMethod(), paramName);
        
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();

        if (!securityService.hasAccessToLegalEntity(principal, legalEntityId)) {
            throw new AccessDeniedException("Access denied to legal entity: " + legalEntityId);
        }
    }

    private Long extractLegalEntityId(JoinPoint joinPoint, Method method, String paramPath) {
        if (paramPath == null || paramPath.isEmpty()) {
            // Try to find argument that implements BusinessObject
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof BusinessObject) {
                    return ((BusinessObject) arg).getLegalEntityId();
                }
            }
            throw new IllegalArgumentException("Could not find BusinessObject parameter");
        }

        String[] pathParts = paramPath.split("\\.");
        String paramName = pathParts[0];
        
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();
        
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName)) {
                Object value = args[i];
                // For nested properties (e.g., "dto.legalEntityId")
                for (int j = 1; j < pathParts.length && value != null; j++) {
                    try {
                        java.lang.reflect.Method getter = value.getClass().getMethod(
                            "get" + pathParts[j].substring(0, 1).toUpperCase() + pathParts[j].substring(1)
                        );
                        value = getter.invoke(value);
                    } catch (Exception e) {
                        throw new IllegalArgumentException(
                            "Could not access property " + pathParts[j] + " in path " + paramPath, e);
                    }
                }
                if (value instanceof Long) {
                    return (Long) value;
                }
                throw new IllegalArgumentException(
                    "Property " + paramPath + " is not of type Long");
            }
        }
        
        throw new IllegalArgumentException("Could not find parameter: " + paramName);
    }
}
