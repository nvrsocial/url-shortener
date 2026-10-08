package com.nrvsocial.urlshortener.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "short_urls")

@Getter
@Setter

public class ShortUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false, length = 1024)
    private String originalUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean active;

    public ShortUrl() {}

    public ShortUrl(String code, String originalUrl, LocalDateTime createdAt, LocalDateTime expiresAt, boolean active) {
        this.code = code;
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.active = active;
    }
}
