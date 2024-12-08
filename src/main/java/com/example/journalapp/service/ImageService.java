package com.example.journalapp.service;

import com.example.journalapp.entity.Image;
import com.example.journalapp.repository.ImageRepository;
import com.example.journalapp.util.ImageUtils;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Data
@Slf4j
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    public List<Image> uploadImages(List<MultipartFile> images) throws IOException {
        List<Image> uploadedImages = new ArrayList<>();

        for (MultipartFile image : images) {
            // Compress and save each image
            Image img = Image.builder()
                    .name(image.getOriginalFilename())
                    .type(image.getContentType())
                    .image(ImageUtils.compressImage(image.getBytes())) // Compress image before saving
                    .build();

            // Save the image entity to the database
            Image savedImage = imageRepository.save(img);

            // Add the saved image to the result list
            uploadedImages.add(savedImage);
            log.info("image uploaded: " + image.getOriginalFilename());
        }

        return uploadedImages;
    }

    @Transactional
    public byte[] downloadImage(String fileName){
        Optional<Image> dbImageData = imageRepository.findByName(fileName);
        return ImageUtils.decompressImage(dbImageData.get().getImage());
    }
}
