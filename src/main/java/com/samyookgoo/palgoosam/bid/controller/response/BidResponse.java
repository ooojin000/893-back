package com.samyookgoo.palgoosam.bid.controller.response;

import com.samyookgoo.palgoosam.bid.domain.Bid;
import java.time.format.DateTimeFormatter;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BidResponse {
    private Long bidId;
    private String bidderEmail;
    private Integer bidPrice;
    private String createdAt;
    private String updatedAt;

    public static BidResponse from(Bid bid) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return BidResponse.builder()
                .bidId(bid.getId())
                .bidderEmail(bid.getBidder().getEmail())
                .bidPrice(bid.getPrice())
                .createdAt(bid.getCreatedAt().format(formatter))
                .updatedAt(bid.getUpdatedAt() != null ? bid.getUpdatedAt().format(formatter) : null)
                .build();
    }
}
