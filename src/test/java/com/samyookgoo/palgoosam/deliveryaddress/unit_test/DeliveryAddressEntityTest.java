package com.samyookgoo.palgoosam.deliveryaddress.unit_test;

import com.samyookgoo.palgoosam.deliveryaddress.domain.DeliveryAddress;
import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressRequestDto;
import com.samyookgoo.palgoosam.user.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("DeliveryAddress Entity 유닛 테스트")
class DeliveryAddressEntityTest {

    @Test
    @DisplayName("Request DTO를 통해서 DeliveryAddress Entity를 생성할 수 있다.")
    public void from_ValidParameters_CreatesNewEntity() {
        //given
        DeliveryAddressRequestDto requestDto = DeliveryAddressRequestDto.builder()
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .zipCode("12345")
                .addressLine1("서울특별시 강남구 테헤란로 123")
                .addressLine2("ABC빌딩 4층")
                .isDefault(false)
                .build();

        User user = new User();

        //when
        DeliveryAddress created = DeliveryAddress.from(requestDto, user);

        //then
        Assertions.assertThat(created.getName()).isEqualTo(requestDto.getName());
        Assertions.assertThat(created.getPhoneNumber()).isEqualTo(requestDto.getPhoneNumber());
        Assertions.assertThat(created.getZipCode()).isEqualTo(requestDto.getZipCode());
        Assertions.assertThat(created.getAddressLine1()).isEqualTo(requestDto.getAddressLine1());
        Assertions.assertThat(created.getAddressLine2()).isEqualTo(requestDto.getAddressLine2());
        Assertions.assertThat(created.getIsDefault()).isEqualTo(false);
        Assertions.assertThat(created.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("기본 배송지에서 일반 배송지로 변경할 수 있다.")
    public void removeDefault() {
        //given
        DeliveryAddressRequestDto requestDto = DeliveryAddressRequestDto.builder()
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .zipCode("12345")
                .addressLine1("서울특별시 강남구 테헤란로 123")
                .addressLine2("ABC빌딩 4층")
                .isDefault(true)
                .build();

        User user = new User();
        DeliveryAddress created = DeliveryAddress.from(requestDto, user);

        //when
        created.removeDefault();

        //then
        Assertions.assertThat(created.getIsDefault()).isFalse();
    }

    @Test
    @DisplayName("일반 배송지에서 기본 배송지로 변경할 수 있다.")
    public void setDefault() {
        //given
        DeliveryAddressRequestDto requestDto = DeliveryAddressRequestDto.builder()
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .zipCode("12345")
                .addressLine1("서울특별시 강남구 테헤란로 123")
                .addressLine2("ABC빌딩 4층")
                .isDefault(false)
                .build();

        User user = new User();
        DeliveryAddress created = DeliveryAddress.from(requestDto, user);

        //when
        created.setDefault();

        //then
        Assertions.assertThat(created.getIsDefault()).isTrue();
    }
}
