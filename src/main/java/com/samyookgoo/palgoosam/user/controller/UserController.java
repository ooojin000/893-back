package com.samyookgoo.palgoosam.user.controller;

import com.samyookgoo.palgoosam.bid.controller.response.BidResponseDto;
import com.samyookgoo.palgoosam.bid.domain.Bid;
import com.samyookgoo.palgoosam.bid.repository.BidRepository;
import com.samyookgoo.palgoosam.user.domain.User;
import com.samyookgoo.palgoosam.user.dto.UserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.samyookgoo.palgoosam.auth.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final AuthService authService;
    private final BidRepository bidRepository;

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        User user = authService.getCurrentUser();

        UserInfoResponseDto dto = new UserInfoResponseDto(
                user.getEmail(),
                user.getName(),
                user.getProfileImage(),
                user.getProvider()
        );

        return ResponseEntity.ok(dto);
    }

    @CrossOrigin(origins="http://localhost:3000", allowCredentials="true")
    @GetMapping("/bids")
    public ResponseEntity<?> getUserInfoByEmail() {
        User user = authService.getCurrentUser();

        List<Bid> dtos = bidRepository.findAllByBidder_Id(user.getId());

        return ResponseEntity.ok(dtos);
    }
}