package com.samyookgoo.palgoosam.auction.file;

import com.samyookgoo.palgoosam.auction.domain.AuctionImage;
import java.io.File;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultFileStore {
    private String folderPath;
    private String storeFileName;
    private String originalFileName;


    public String getFullPath() {
        return folderPath + File.separator + storeFileName;
    }

    public static AuctionImage toEntity(ResultFileStore resultFileStore) {
        return AuctionImage.builder()
                .originalName(resultFileStore.getOriginalFileName())
                .storeName(resultFileStore.getStoreFileName())
                .build();
    }

    public boolean isVaild() {
        return folderPath != null && storeFileName != null && originalFileName != null;
    }
}
