package com.samyookgoo.palgoosam.auth.service;

import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest,OAuth2User> delegate =
                new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String provider    = userRequest.getClientRegistration().getRegistrationId(); // "google"
        Map<String, Object> attrs = oAuth2User.getAttributes();
        String providerId  = attrs.get("sub").toString();
        String email       = attrs.get("email").toString();
        String name        = attrs.get("name").toString();
        String picture     = attrs.get("picture").toString();

        // DB에 저장 또는 업데이트
        User user = userRepository
                .findByProviderAndProviderId(provider, providerId)
                .map(u -> {
                    u.setName(name);
                    u.setProfileImage(picture);
                    return userRepository.save(u);
                })
                .orElseGet(() ->
                        userRepository.save(User.builder()
                                .name(name)
                                .email(email)
                                .profileImage(picture)
                                .provider(provider)
                                .providerId(providerId)
                                .build()
                        )
                );

        return new CustomOauth2UserDetails(user, attrs);
    }
}