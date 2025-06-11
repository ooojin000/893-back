package com.samyookgoo.palgoosam.common.s3;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class S3PresignedResponse {
    private String presignedUrl;
    private String storeName;
    private Instant expirationTime;
}
