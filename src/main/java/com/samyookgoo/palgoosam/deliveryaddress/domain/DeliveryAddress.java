package com.samyookgoo.palgoosam.deliveryaddress.domain;

import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressRequestDto;
import com.samyookgoo.palgoosam.deliveryaddress.exception.DeliveryAddressBadRequestException;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;
import com.samyookgoo.palgoosam.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery_address")
public class DeliveryAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String addressLine1;

    private String addressLine2;

    @Column(nullable = false)
    private String zipCode;

    @Builder.Default
    @ColumnDefault("false")
    private Boolean isDefault = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public void removeDefault() {
        this.isDefault = false;
    }

    public void setDefault() {
        this.isDefault = true;
    }

    public void checkDefault() {
        if (this.isDefault) {
            throw new DeliveryAddressBadRequestException(ErrorCode.DELIVERY_ADDRESS_ALREADY_DEFAULT_BAD_REQUEST);
        }
    }

    static public DeliveryAddress from(DeliveryAddressRequestDto requestDto, User user) {
        return DeliveryAddress.builder()
                .user(user)
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .zipCode(requestDto.getZipCode())
                .addressLine1(requestDto.getAddressLine1())
                .addressLine2(requestDto.getAddressLine2())
                .isDefault(requestDto.isDefault())
                .build();
    }
}
