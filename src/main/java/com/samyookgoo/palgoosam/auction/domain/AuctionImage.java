package com.samyookgoo.palgoosam.auction.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "auction_image")
public class AuctionImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @Column(nullable = false, length = 255)
    private String url;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private String storeName;

    @Column(nullable = false)
    private int imageSeq;

    @ColumnDefault("false")
    private Boolean isMain = false;

    @CreationTimestamp
    private LocalDateTime createdAt;
}


