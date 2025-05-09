package com.samyookgoo.palgoosam.auction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Builder
@Entity
@Table(name = "auction_image")
@AllArgsConstructor
@NoArgsConstructor
public class AuctionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storeName;

//    @Column(nullable = false)
//    private Long size;

    @Column(nullable = false)
    private int imageSeq;

    @ColumnDefault("false")
    private Boolean isMain = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 255)
    private String url;
}
