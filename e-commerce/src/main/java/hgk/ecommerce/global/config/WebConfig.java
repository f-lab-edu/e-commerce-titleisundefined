package hgk.ecommerce.global.config;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.common.resolver.AuthCheckResolver;
import hgk.ecommerce.domain.owner.service.OwnerService;
import hgk.ecommerce.domain.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    UserService userService;
    @Autowired
    OwnerService ownerService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthCheckResolver(userService, ownerService));
    }
}
