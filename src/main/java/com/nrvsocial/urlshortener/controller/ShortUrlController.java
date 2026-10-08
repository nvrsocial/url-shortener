package com.nrvsocial.urlshortener.controller;

import com.nrvsocial.urlshortener.dto.request.CreateShortUrlRequest;
import com.nrvsocial.urlshortener.dto.response.ShortUrlResponse;
import com.nrvsocial.urlshortener.service.ShortUrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/urls")
public class ShortUrlController {

    private final ShortUrlService shortUrlService;

    public ShortUrlController(ShortUrlService shortUrlService) {
        this.shortUrlService = shortUrlService;
    }

    @PostMapping
    public ResponseEntity<ShortUrlResponse> create(@Valid @RequestBody CreateShortUrlRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(shortUrlService.create(request));
    }
}