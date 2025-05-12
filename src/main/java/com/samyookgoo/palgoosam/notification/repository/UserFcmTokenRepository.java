package com.samyookgoo.palgoosam.notification.repository;

import com.samyookgoo.palgoosam.notification.domain.UserFcmToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

}
