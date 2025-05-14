// 추후 필요하면 수정
//package com.samyookgoo.palgoosam.auth;
//
//import com.samyookgoo.palgoosam.user.domain.User;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.Map;
//
//public class CustomOauth2UserDetails implements UserDetails, OAuth2User {
//    private final User user;
//    private final Map<String, Object> attributes;
//
//    public CustomOauth2UserDetails(User user, Map<String, Object> attributes) {
//        this.user = user;
//        this.attributes = attributes;
//    }
//
//    // OAuth2User
//    @Override
//    public Map<String, Object> getAttributes() {
//        return Map.of();
//    }
//
//    @Override
//    public <A> A getAttribute(String name) {
//        return OAuth2User.super.getAttribute(name);
//    }
//
//    @Override
//    public String getName() {
//        return user.getProviderId();
//    }
//
//    // UserDetails
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of();
//    }
//
//    @Override
//    public String getPassword() {
//        return "";
//    }
//
//    @Override
//    public String getUsername() {
//        return user.getName();
//    }
//
//    // 내가 추가한거
//    public String getUserEmail() {
//        return user.getEmail();
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return UserDetails.super.isAccountNonExpired();
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return UserDetails.super.isAccountNonLocked();
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return UserDetails.super.isCredentialsNonExpired();
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return UserDetails.super.isEnabled();
//    }
//}
