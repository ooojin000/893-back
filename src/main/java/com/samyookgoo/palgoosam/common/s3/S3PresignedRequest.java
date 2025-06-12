package com.samyookgoo.palgoosam.common.s3;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class S3PresignedRequest {
    @NotBlank
    private String fileName;
    private String contentType;
}
