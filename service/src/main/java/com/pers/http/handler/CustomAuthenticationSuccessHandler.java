package com.pers.http.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler {//implements AuthenticationSuccessHandler {

//    private final ClientService clientService;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException {
//        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
//        UUID clientId = clientService.getIdFromSuccessAuth(oauthUser.getAttributes());
////        request.getSession().setAttribute("clientId", clientId);
//
//        boolean hasAdminRole = authentication.getAuthorities().stream()
//                .anyMatch(a -> a.getAuthority().equals("ADMIN") || a.getAuthority().equals("SUPER_ADMIN"));
//        if (hasAdminRole) {
//            response.sendRedirect("/admin/main");
//        } else {
//            response.sendRedirect("/clients/home");
//        }
//
//    }
}