package com.samarth.urlshortener.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUrlRequest(

        @NotBlank(message = "URL is required")
        @Pattern(
                regexp = "(?i)^https?://.+",
                message = "URL must start with http:// or https://"
        )
        String originalUrl

) {}

