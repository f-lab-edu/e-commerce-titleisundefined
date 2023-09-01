package hgk.ecommerce.domain.common.resolver;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.common.exception.AuthorizationException;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.service.OwnerService;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.service.UserService;
import jdk.jshell.spi.ExecutionControl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static jdk.jshell.spi.ExecutionControl.*;

@RequiredArgsConstructor
public class AuthCheckResolver implements HandlerMethodArgumentResolver {
    private final UserService userService;
    private final OwnerService ownerService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthCheck.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        if (parameter.getParameterType().isAssignableFrom(User.class)) {
            return userService.getCurrentUser();
        } else if(parameter.getParameterType().isAssignableFrom(Owner.class)) {
            return ownerService.getCurrentOwner();
        }

        throw new NotImplementedException("지원하지 않는 파라미터 타입입니다.");
    }
}
