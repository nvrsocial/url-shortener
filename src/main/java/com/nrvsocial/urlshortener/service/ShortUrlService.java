package com.nrvsocial.urlshortener.service;

import com.nrvsocial.urlshortener.component.ShortCodeGenerator;
import com.nrvsocial.urlshortener.dto.request.CreateShortUrlRequest;
import com.nrvsocial.urlshortener.dto.response.ShortUrlResponse;
import com.nrvsocial.urlshortener.entity.ShortUrl;
import com.nrvsocial.urlshortener.repository.ShortUrlRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ShortUrlService {
    private final ShortUrlRepository shortUrlRepository;
    private final ShortCodeGenerator shortCodeGenerator;

    public ShortUrlService(ShortUrlRepository shortUrlRepository, ShortCodeGenerator shortCodeGenerator) {
        this.shortUrlRepository = shortUrlRepository;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    @Transactional
    public ShortUrlResponse create(CreateShortUrlRequest createShortUrlRequest) {
        String code = shortCodeGenerator.generate();

        ShortUrl shortUrl = new ShortUrl();

        shortUrl.setCode(code);
        shortUrl.setOriginalUrl(createShortUrlRequest.getOriginalUrl());
        shortUrl.setCreatedAt(LocalDateTime.now());
        shortUrl.setExpiresAt(createShortUrlRequest.getExpiresAt());
        shortUrl.setActive(true);

        shortUrlRepository.save(shortUrl);

        return new ShortUrlResponse(shortUrl.getId(), shortUrl.getCode(), shortUrl.getOriginalUrl(),
                "localhost:8080/" + shortUrl.getCode(), shortUrl.getCreatedAt(), shortUrl.getExpiresAt());
    }

    @Transactional(readOnly = true)
    public String resolve(String code) {

        ShortUrl shortUrl = shortUrlRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found"));

        if (!shortUrl.isActive()) {
            throw new ResponseStatusException(HttpStatus.GONE, "Short URL is disabled");
        }

        if (shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "Short URL expired");
        }

        return shortUrl.getOriginalUrl();
    }

}
