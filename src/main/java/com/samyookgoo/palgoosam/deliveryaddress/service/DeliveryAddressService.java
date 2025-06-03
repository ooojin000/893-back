package com.samyookgoo.palgoosam.deliveryaddress.service;

import com.samyookgoo.palgoosam.auth.service.AuthService;
import com.samyookgoo.palgoosam.deliveryaddress.domain.DeliveryAddress;
import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressRequestDto;
import com.samyookgoo.palgoosam.deliveryaddress.dto.DeliveryAddressResponseDto;
import com.samyookgoo.palgoosam.deliveryaddress.exception.DeliveryAddressNotFoundException;
import com.samyookgoo.palgoosam.deliveryaddress.repository.DeliveryAddressRepository;
import com.samyookgoo.palgoosam.global.exception.ErrorCode;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.exception.UserForbiddenException;
import com.samyookgoo.palgoosam.user.exception.UserUnauthorizedException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final AuthService authService;

    public List<DeliveryAddressResponseDto> getUserDeliveryAddresses() {
        User user = getAuthenticatedUser(authService.getCurrentUser());
        List<DeliveryAddress> deliveryAddressList = deliveryAddressRepository.findAllByUser_Id(user.getId());

        return deliveryAddressList.stream()
                .map(DeliveryAddressResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteUserDeliveryAddress(Long deliveryAddressId) {
        User user = getAuthenticatedUser(authService.getCurrentUser());
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findByUserAndId(user, deliveryAddressId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException(ErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

        if (deliveryAddress.hasPermission(user.getId())) {
            deliveryAddressRepository.deleteById(deliveryAddress.getId());
        } else {
            throw new UserForbiddenException();
        }
    }

    @Transactional
    public void postUserDeliveryAddress(DeliveryAddressRequestDto requestDto) {
        User user = getAuthenticatedUser(authService.getCurrentUser());
        DeliveryAddress isDeliveryAddressSaveSuccess = DeliveryAddress.from(requestDto, user);
        deliveryAddressRepository.save(isDeliveryAddressSaveSuccess);
    }

    @Transactional
    public void modifyDefaultDeliveryAddress(Long deliveryAddressId) {
        User user = getAuthenticatedUser(authService.getCurrentUser());
        DeliveryAddress target = deliveryAddressRepository.findByUserAndIsDefaultTrue(user)
                .orElseThrow(() -> new DeliveryAddressNotFoundException(ErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

        DeliveryAddress deliveryAddressToDefault = deliveryAddressRepository.findById(deliveryAddressId)
                .orElseThrow(() -> new DeliveryAddressNotFoundException(ErrorCode.DELIVERY_ADDRESS_NOT_FOUND));

        if (target.hasPermission(user.getId()) && deliveryAddressToDefault.hasPermission(user.getId())) {
            target.removeDefault();
            deliveryAddressToDefault.setDefault();
        } else {
            throw new UserForbiddenException();
        }
    }

    public User getAuthenticatedUser(User user) {
        if (user == null) {
            throw new UserUnauthorizedException();
        }
        return user;
    }
}
