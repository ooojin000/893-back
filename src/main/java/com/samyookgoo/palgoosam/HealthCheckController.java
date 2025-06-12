package com.samyookgoo.palgoosam;

import com.samyookgoo.palgoosam.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/health-check")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<BaseResponse<Void>> healthCheck() {
        return ResponseEntity.ok(null);
    }
}
