package de.likeherotozero.config;

import de.likeherotozero.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebSessionConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(new RoleGuardInterceptor(Role.SCIENTIST)).addPathPatterns("/scientist/**");
        registry.addInterceptor(new RoleGuardInterceptor(Role.ADMIN)).addPathPatterns("/admin/**");
    }

    private static class RoleGuardInterceptor implements HandlerInterceptor {

        private final Role requiredRole;

        RoleGuardInterceptor(Role requiredRole) {
            this.requiredRole = requiredRole;
        }

        @Override
        public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler)
                throws Exception {
            HttpSession session = request.getSession(false);
            Object role = session == null ? null : session.getAttribute("role");

            if (!requiredRole.name().equals(role)) {
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
            return true;
        }
    }
}
