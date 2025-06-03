package com.samyookgoo.palgoosam.deliveryaddress.unit_test;

import com.samyookgoo.palgoosam.deliveryaddress.domain.DeliveryAddress;
import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressResponseDto;
import com.samyookgoo.palgoosam.user.domain.User;
import java.time.LocalDateTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DeliveryAddress DTO 유닛 테스트")
public class DeliveryAddressDtoTest {
    @Test
    @DisplayName("정적 팩토리 메서드 of로 새로운 DTO를 생성할 수 있다.")
    public void Given_DeliveryAddress_When_CallofMethod_Then_ReturnDeliveryResponseDto() {
        //given
        User testUser = new User();
        DeliveryAddress testAddress = DeliveryAddress.builder()
                .id(1L)
                .user(testUser) // User 객체 필요
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .addressLine1("서울특별시 강남구 테헤란로 123")
                .addressLine2("ABC빌딩 4층")
                .zipCode("12345")
                .isDefault(false)
                .createdAt(LocalDateTime.now())
                .build();
        //when
        DeliveryAddressResponseDto created = DeliveryAddressResponseDto.of(testAddress);

        //then
        Assertions.assertThat(created.getId()).isEqualTo(testAddress.getId());
        Assertions.assertThat(created.getName()).isEqualTo(testAddress.getName());
        Assertions.assertThat(created.getPhoneNumber()).isEqualTo(testAddress.getPhoneNumber());
        Assertions.assertThat(created.getAddressLine1()).isEqualTo(testAddress.getAddressLine1());
        Assertions.assertThat(created.getAddressLine2()).isEqualTo(testAddress.getAddressLine2());
        Assertions.assertThat(created.getZipCode()).isEqualTo(testAddress.getZipCode());
        Assertions.assertThat(created.getIsDefault()).isEqualTo(testAddress.getIsDefault());

    }
}
