package com.samyookgoo.palgoosam.deliveryaddress.repository;

import com.samyookgoo.palgoosam.deliveryaddress.domain.DeliveryAddress;
import com.samyookgoo.palgoosam.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
        UPDATE DeliveryAddress d
           SET d.isDefault = false
         WHERE d.user.id = :userId
    """)
    int unsetAllDefaults(@Param("userId") Long userId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("""
    UPDATE DeliveryAddress d
      SET d.isDefault = true
    WHERE d.id = :id AND d.user.id = :userId
""")
    int updateDefault(@Param("id") Long id, @Param("userId") Long userId);

    List<DeliveryAddress> findAllByUser_Id(Long userId);
    Optional<DeliveryAddress> findByUserAndId(User user, Long addressId);
}