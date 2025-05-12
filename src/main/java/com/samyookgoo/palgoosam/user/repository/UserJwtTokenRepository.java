package com.samyookgoo.palgoosam.user.repository;

import com.samyookgoo.palgoosam.user.domain.UserJwtToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJwtTokenRepository extends JpaRepository<UserJwtToken, Long> {

}
