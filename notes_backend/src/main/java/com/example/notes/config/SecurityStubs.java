package com.example.notes.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.lang.annotation.*;

/**
 * PUBLIC_INTERFACE
 * Simple RBAC and RequestContext stubs leveraging headers:
 * X-Role (USER/ADMIN) and X-User-Id for audit attribution.
 */
public final class SecurityStubs {

    private SecurityStubs() {}

    /**
     * PUBLIC_INTERFACE
     * ThreadLocal RequestContext storing user id and role for current request.
     */
    public static final class RequestContext {
        private static final ThreadLocal<String> USER = new ThreadLocal<>();
        private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

        private RequestContext() {}

        public static void setUser(String uid) { USER.set(uid); }
        public static String getUser() { return USER.get() != null ? USER.get() : "system"; }
        public static void setRole(String role) { ROLE.set(role); }
        public static String getRole() { return ROLE.get() != null ? ROLE.get() : "USER"; }
        public static void clear() { USER.remove(); ROLE.remove(); }
    }

    /**
     * PUBLIC_INTERFACE
     * Annotation to require a role on controller/service methods.
     */
    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface RequiresRole {
        String value() default "USER";
    }

    /**
     * PUBLIC_INTERFACE
     * Interceptor that:
     * - Populates RequestContext from headers
     * - Enforces @RequiresRole
     */
    @Configuration
    public static class RoleInterceptorConfig implements WebMvcConfigurer {

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(new RoleInterceptor());
        }
    }

    static class RoleInterceptor implements HandlerInterceptor {
        @Override
        public boolean preHandle(@NonNull HttpServletRequest request,
                                 @NonNull jakarta.servlet.http.HttpServletResponse response,
                                 @NonNull Object handler) throws Exception {
            String role = StringUtils.hasText(request.getHeader("X-Role")) ? request.getHeader("X-Role") : "USER";
            String userId = StringUtils.hasText(request.getHeader("X-User-Id")) ? request.getHeader("X-User-Id") : "system";
            RequestContext.setRole(role.toUpperCase());
            RequestContext.setUser(userId);

            if (handler instanceof HandlerMethod hm) {
                RequiresRole methodAnn = hm.getMethodAnnotation(RequiresRole.class);
                RequiresRole typeAnn = hm.getBeanType().getAnnotation(RequiresRole.class);
                String required = "USER";
                if (typeAnn != null) required = typeAnn.value();
                if (methodAnn != null) required = methodAnn.value();

                if ("ADMIN".equalsIgnoreCase(required) && !"ADMIN".equalsIgnoreCase(RequestContext.getRole())) {
                    response.sendError(HttpStatus.FORBIDDEN.value(), "Insufficient role: ADMIN required");
                    return false;
                }
            }
            return true;
        }

        @Override
        public void afterCompletion(@NonNull HttpServletRequest request,
                                    @NonNull jakarta.servlet.http.HttpServletResponse response,
                                    @NonNull Object handler, Exception ex) {
            RequestContext.clear();
        }
    }
}
