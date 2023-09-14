package hgk.ecommerce.domain.common.resolver;

import hgk.ecommerce.domain.common.annotation.AuthCheck;
import hgk.ecommerce.domain.common.exceptions.AuthorizationException;
import hgk.ecommerce.domain.common.service.SessionService;
import hgk.ecommerce.domain.owner.Owner;
import hgk.ecommerce.domain.owner.service.OwnerService;
import hgk.ecommerce.domain.user.User;
import hgk.ecommerce.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class AuthCheckResolver implements HandlerMethodArgumentResolver {
    private final SessionService sessionService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthCheck.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        if(parameter.getParameterType().isAssignableFrom(User.class)) {
            return sessionService.getCurrentUser();
        } else if(parameter.getParameterType().isAssignableFrom(Owner.class)) {
            return sessionService.getCurrentOwner();
        }

        throw new AuthorizationException("로그인 정보가 없습니다.", HttpStatus.BAD_REQUEST);
    }
}
