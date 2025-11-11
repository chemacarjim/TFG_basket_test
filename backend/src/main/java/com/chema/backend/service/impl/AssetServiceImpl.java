package com.chema.backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.chema.backend.service.AssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class AssetServiceImpl implements AssetService {

    private final Cloudinary cloudinary;

    @Autowired
    public AssetServiceImpl(
            @Value("${app.cloudinary.cloud-name:}") String cloudName,
            @Value("${app.cloudinary.api-key:}") String apiKey,
            @Value("${app.cloudinary.api-secret:}") String apiSecret
    ) {
        if (cloudName != null && !cloudName.isBlank()) {
            this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret
            ));
        } else {
            this.cloudinary = null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public String uploadQuestionImage(MultipartFile file) {
        if (cloudinary == null) {
            throw new IllegalStateException("Cloudinary no configurado");
        }
        try {
            Map<String, Object> upload = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "tfg-basket/questions",
                    "resource_type", "image"
            ));
            return (String) upload.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("No se pudo subir la imagen", e);
        }
    }
}
