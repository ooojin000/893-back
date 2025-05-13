package com.samyookgoo.palgoosam.deliveryaddress.controller;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.deliveryaddress.domain.DeliveryAddress;
import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressRequestDto;
import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressResponseDto;
import com.samyookgoo.palgoosam.deliveryaddress.repository.DeliveryAddressRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
// TODO 아래 API를 UserController 에 통합하는게 나을지?
public class DeliveryAddressController {
    private final AuthService authService;
    private final DeliveryAddressRepository deliveryAddressRepository;

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @GetMapping("/addresses")
    public ResponseEntity<BaseResponse> getUserDeliveryAddresses() {
        User user = authService.getCurrentUser();

        List<DeliveryAddress> entities =
                deliveryAddressRepository.findAllByUser_Id(user.getId());

        List<DeliveryAddressResponseDto> dtos = entities.stream()
                .map(DeliveryAddressResponseDto::of)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("배송지가 정상적으로 조회되었습니다.", dtos));
    }

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @DeleteMapping("addresses/{id}")
    public ResponseEntity<BaseResponse> deleteUserDeliveryAddress(
            @PathVariable Long id
    ) {
        User user = authService.getCurrentUser();

        DeliveryAddress deliveryAddress = deliveryAddressRepository
                .findByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "삭제할 주소를 찾을 수 없습니다."
                ));

        deliveryAddressRepository.deleteById(deliveryAddress.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("배송지가 삭제되었습니다.",null));
    }

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @PostMapping("/addresses")
    public ResponseEntity<BaseResponse> postUserDeliveryAddress(@RequestBody DeliveryAddressRequestDto req) {
        User user = authService.getCurrentUser();

        DeliveryAddress entity = DeliveryAddress.builder()
                .user(user)
                .name(req.getName())
                .phoneNumber(req.getPhoneNumber())
                .zipCode(req.getZipCode())
                .addressLine1(req.getAddressLine1())
                .addressLine2(req.getAddressLine2())
                .isDefault(req.isDefault())
                .build();

        DeliveryAddress saved = deliveryAddressRepository.save(entity);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("배송지가 등록되었습니다.",null));
    }

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @PatchMapping("/addresses/{id}/default")
    public ResponseEntity<BaseResponse> patchUserDefaultAddress(
            @PathVariable Long id
    ) {
        User user = authService.getCurrentUser();

        DeliveryAddress deliveryAddress = deliveryAddressRepository
                .findByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "변경할 주소를 찾을 수 없습니다."
                ));

        deliveryAddressRepository.unsetAllDefaults(user.getId());
        deliveryAddressRepository.updateDefault(deliveryAddress.getId(), user.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(BaseResponse.success("기본 배송지가 변경됐습니다.",null));
    }
}
