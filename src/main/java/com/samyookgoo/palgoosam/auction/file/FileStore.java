package com.samyookgoo.palgoosam.auction.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileStore {

    @Value("${file.dir}")
    private String uploadPath;

    public List<ResultFileStore> storeFiles(List<MultipartFile> multipartFiles) {
        List<ResultFileStore> storeFileResult = new ArrayList<>();

        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (MultipartFile multipartFile : multipartFiles) {
                storeFileResult.add(storeFile(multipartFile));
            }
        }

        return storeFileResult;
    }

    public ResultFileStore storeFile(MultipartFile multipartFile) {
        if (multipartFile.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 저장할 수 없습니다.");
        }

        String originalFilename = multipartFile.getOriginalFilename();      // 파일 이름
        String storeFileName = createStoreFileName(originalFilename);       // 파일 저장 이름
        String folderPath = makeFolder();                                   // 폴더 생성

        String fullPath = folderPath + File.separator + storeFileName;      // 이미지 저장

        try {
            multipartFile.transferTo(new File(fullPath));
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + originalFilename, e);
        }

        return new ResultFileStore(folderPath, storeFileName, originalFilename);
    }

    private String createStoreFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "_" + originalFileName;
    }

    private String makeFolder() {
        String folderPath = uploadPath;
        File uploadPathFolder = new File(folderPath);

        if (!uploadPathFolder.exists()) {
            uploadPathFolder.mkdirs();
        }

        return folderPath;
    }
}
