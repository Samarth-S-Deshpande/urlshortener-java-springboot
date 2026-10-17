
package com.samarth.urlshortener.dto.response;

public record CreateUrlResponse(
        String shortCode,
        String shortUrl,
        String originalUrl
) {
}