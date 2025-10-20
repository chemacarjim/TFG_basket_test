package com.chema.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface AssetService {
    String uploadQuestionImage(MultipartFile file); // devuelve URL
}
