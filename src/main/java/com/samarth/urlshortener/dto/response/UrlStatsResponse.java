
package com.samarth.urlshortener.dto.response;

import java.time.LocalDateTime;

public record UrlStatsResponse(
        String shortCode,
        String shortUrl,
        String originalUrl,
        long clickCount,
        LocalDateTime createdAt
) {
}