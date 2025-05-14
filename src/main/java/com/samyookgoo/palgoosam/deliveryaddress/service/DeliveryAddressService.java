package com.samyookgoo.palgoosam.deliveryaddress.service;

import com.samyookgoo.palgoosam.deliveryaddress.domain.DeliveryAddress;
import com.samyookgoo.palgoosam.deliveryaddress.repository.DeliveryAddressRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryAddressService {
    private final DeliveryAddressRepository deliveryAddressRepository;

    public List<DeliveryAddress> getDeliveryAddressByUserId(Long userId) {
        return deliveryAddressRepository.findAllByUser_Id(userId);
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
}
