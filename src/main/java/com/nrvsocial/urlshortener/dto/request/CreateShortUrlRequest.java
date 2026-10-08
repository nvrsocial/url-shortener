package com.nrvsocial.urlshortener.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateShortUrlRequest {
    @NotBlank
    @Size(max = 1024)
    private String originalUrl;

    @Future
    private LocalDateTime expiresAt;
}
