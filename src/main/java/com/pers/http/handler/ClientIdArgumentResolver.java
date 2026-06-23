package com.pers.http.handler;

import com.pers.exception.BusinessException;
import com.pers.exception.ErrorCode;
import com.pers.http.config.ClientId;
import com.pers.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;
import java.util.UUID;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class ClientIdArgumentResolver implements HandlerMethodArgumentResolver {

    private final ClientService clientService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ClientId.class) && parameter.getParameterType().equals(UUID.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Map<String, Object> clientIdClaim = switch (principal) {
            case Jwt jwt -> jwt.getClaims();
            case OidcUser oidcUser -> oidcUser.getClaims();
            case OAuth2User oauth2User -> oauth2User.getAttributes();
            default -> throw new BusinessException(
                    INTERNAL_SERVER_ERROR,
                    ErrorCode.AUTH_PRINCIPAL_UNSUPPORTED
            );
        };
        return clientService.getIdFromSuccessAuth(clientIdClaim);
    }
}
