package com.samyookgoo.palgoosam.user.repository;

import com.samyookgoo.palgoosam.user.domain.UserOauthToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserOauthTokenRepository extends JpaRepository<UserOauthToken, Long> {

}
