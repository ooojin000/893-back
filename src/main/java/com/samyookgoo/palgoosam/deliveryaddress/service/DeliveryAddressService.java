package com.samyookgoo.palgoosam.deliveryaddress.service;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.common.response.BaseResponse;
import com.samyookgoo.palgoosam.deliveryaddress.domain.DeliveryAddress;
import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressRequestDto;
import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressResponseDto;
import com.samyookgoo.palgoosam.deliveryaddress.repository.DeliveryAddressRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final AuthService authService;

    public List<DeliveryAddressResponseDto> getUserDeliveryAddresses() {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        List<DeliveryAddress> entities = getDeliveryAddressByUserId(user.getId());

        List<DeliveryAddressResponseDto> dtoList = entities.stream()
                .map(DeliveryAddressResponseDto::of)
                .collect(Collectors.toList());

        return dtoList;
    }

    public List<DeliveryAddress> getDeliveryAddressByUserId(Long userId) {
        return deliveryAddressRepository.findAllByUser_Id(userId);
    }

    public void deleteUserDeliveryAddress(Long deliveryAddressId) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        boolean isDeliveryAddressDeleteSuccess = deleteUserDeliveryAddress(user, deliveryAddressId);

        if (!isDeliveryAddressDeleteSuccess) {
            ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error("배송지 삭제 에러", null));
        }
    }

    public boolean modifyDefaultDeliveryAddress(User user, Long addressId) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository
                .findByUserAndId(user, addressId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "변경할 주소를 찾을 수 없습니다."
                ));

        deliveryAddressRepository.unsetAllDefaults(user.getId());
        deliveryAddressRepository.updateDefault(deliveryAddress.getId(), user.getId());

        return true;
    }

    public void postUserDeliveryAddress(DeliveryAddressRequestDto requestDto) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        DeliveryAddress entity = DeliveryAddress.builder()
                .user(user)
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .zipCode(requestDto.getZipCode())
                .addressLine1(requestDto.getAddressLine1())
                .addressLine2(requestDto.getAddressLine2())
                .isDefault(requestDto.isDefault())
                .build();

        boolean isDeliveryAddressSaveSuccess = saveUserDeliveryAddress(entity);

        if (!isDeliveryAddressSaveSuccess) {
            ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error("배송지 등록 에러", null));
        }
    }

    public boolean saveUserDeliveryAddress(DeliveryAddress deliveryAddress) {
        DeliveryAddress saved = deliveryAddressRepository.save(deliveryAddress);
        return true;
    }

    public boolean deleteUserDeliveryAddress(User user, Long addressId) {
        DeliveryAddress deliveryAddress = deliveryAddressRepository
                .findByUserAndId(user, addressId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "삭제할 주소를 찾을 수 없습니다."
                ));

        deliveryAddressRepository.deleteById(deliveryAddress.getId());

        return true;
    }

    public void patchUserDefaultAddress(Long deliveryAddressId) {
        User user = authService.getCurrentUser();
        if (user == null) {
            throw new UsernameNotFoundException("유저를 찾을 수 없습니다.");
        }
        boolean isModifySuccess = modifyDefaultDeliveryAddress(user, deliveryAddressId);

        if (!isModifySuccess) {
            ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(BaseResponse.error("기본 배송지 변경 에러", null));
        }
    }
}
