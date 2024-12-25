package interconnection.config;

import interconnection.interceptor.AdminInterceptor;
import interconnection.interceptor.TecnicoInterceptor;
import interconnection.interceptor.ComercioInterceptor;
import interconnection.interceptor.TecnicoOrAdminInterceptor;
import interconnection.interceptor.AuthInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration to register interceptors.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminInterceptor adminInterceptor;

    @Autowired
    private TecnicoInterceptor tecnicoInterceptor;

    @Autowired
    private ComercioInterceptor comercioInterceptor;

    @Autowired
    private TecnicoOrAdminInterceptor tecnicoOrAdminInterceptor;

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Apply AdminInterceptor to /registrados and /registrados/**
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/general/**");

        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**");

        registry.addInterceptor(tecnicoInterceptor)
                .addPathPatterns("/api/tecnico/**");

        registry.addInterceptor(comercioInterceptor)
                .addPathPatterns("/api/comercio/**");

        registry.addInterceptor(tecnicoOrAdminInterceptor)
                .addPathPatterns("/api/tecnico-or-admin/**");
    }
}

