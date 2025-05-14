package com.samyookgoo.palgoosam.auth;

import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOidcUserService
        implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserRepository userRepo;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {

        OidcUser oidcUser = new OidcUserService().loadUser(userRequest);

        Map<String, Object> attrs = oidcUser.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId(); // google
        String providerId = attrs.get("sub").toString();
        String email      = attrs.get("email").toString();
        String name       = attrs.get("name").toString();
        String picture     = attrs.get("picture").toString();

        // DB에 User가 없으면 생성, 있으면 업데이트
        User user = userRepo.findByProviderAndProviderId(provider, providerId)
                .map(u -> {
                    u.setName(name);
                    u.setProfileImage(picture);
                    return userRepo.save(u);
                })
                .orElseGet(() ->
                        userRepo.save(User.builder()
                                .provider(provider)
                                .providerId(providerId)
                                .email(email)
                                .name(name)
                                .profileImage(picture)
                                .build())
                );

        return oidcUser;
    }
}
