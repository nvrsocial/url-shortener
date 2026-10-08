package com.nrvsocial.urlshortener.dto.response;

import com.nrvsocial.urlshortener.entity.ShortUrl;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Setter
public class ShortUrlResponse {
    private Long id;
    private String code;
    private String originalUrl;
    private String shortUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public ShortUrlResponse(Long id, String code, String originalUrl, String shortUrl, LocalDateTime createdAt, LocalDateTime expiresAt){
        this.id = id;
        this.code = code;
        this.originalUrl = originalUrl;
        this.shortUrl = shortUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }
}
