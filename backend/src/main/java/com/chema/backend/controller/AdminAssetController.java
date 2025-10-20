package com.chema.backend.controller;

import com.chema.backend.service.AssetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/assets")
@RequiredArgsConstructor
public class AdminAssetController {

    private final AssetService assetService;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        String url = assetService.uploadQuestionImage(file);
        return ResponseEntity.ok(url);
    }
}
