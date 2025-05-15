package com.samyookgoo.palgoosam.deliveryaddress.controller;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.deliveryaddress.domain.DeliveryAddress;
import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressRequestDto;
import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressResponseDto;
import com.samyookgoo.palgoosam.deliveryaddress.service.DeliveryAddressService;
import com.samyookgoo.palgoosam.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
// TODO 아래 API를 UserController 에 통합하는게 나을지?
public class DeliveryAddressController {
    private final AuthService authService;
    private final DeliveryAddressService deliveryAddressService;

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @GetMapping("/addresses")
    public ResponseEntity<BaseResponse<List<DeliveryAddressResponseDto>>> getUserDeliveryAddresses() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        List<DeliveryAddress> entities = deliveryAddressService.getDeliveryAddressByUserId(user.getId());

        List<DeliveryAddressResponseDto> dtoList = entities.stream()
                .map(DeliveryAddressResponseDto::of)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("배송지가 정상적으로 조회되었습니다.", dtoList));
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @DeleteMapping("addresses/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteUserDeliveryAddress(
            @PathVariable Long id
    ) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        boolean isDeliveryAddressDeleteSuccess = deliveryAddressService.deleteUserDeliveryAddress(user, id);

        if (!isDeliveryAddressDeleteSuccess) {
            ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error("배송지 삭제 에러", null));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("배송지가 삭제되었습니다.", null));
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/addresses")
    public ResponseEntity<BaseResponse<Void>> postUserDeliveryAddress(@RequestBody DeliveryAddressRequestDto req) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        DeliveryAddress entity = DeliveryAddress.builder()
                .user(user)
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .zipCode(req.getZipCode())
                .addressLine1(req.getAddressLine1())
                .addressLine2(req.getAddressLine2())
                .isDefault(req.isDefault())
                .build();

        boolean isDeliveryAddressSaveSuccess = deliveryAddressService.saveUserDeliveryAddress(entity);

        if (!isDeliveryAddressSaveSuccess) {
            ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error("배송지 등록 에러", null));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("배송지가 등록되었습니다.", null));
    }

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PatchMapping("/addresses/{id}/default")
    public ResponseEntity<BaseResponse<Void>> patchUserDefaultAddress(
            @PathVariable Long id
    ) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        boolean isModifySuccess = deliveryAddressService.modifyDefaultDeliveryAddress(user, id);

        if (!isModifySuccess) {
            ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error("기본 배송지 변경 에러", null));
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("기본 배송지가 변경됐습니다.", null));
    }
}
