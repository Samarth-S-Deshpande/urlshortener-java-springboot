
package com.samarth.urlshortener.controller;

import com.samarth.urlshortener.dto.request.CreateUrlRequest;
import com.samarth.urlshortener.dto.response.CreateUrlResponse;
import com.samarth.urlshortener.dto.response.UrlStatsResponse;
import com.samarth.urlshortener.entity.ShortUrl;
import com.samarth.urlshortener.service.ShortUrlService;

import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/urls")
public class ShortUrlController {

    private final ShortUrlService service;

    public ShortUrlController(ShortUrlService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUrlResponse create(
            @Valid @RequestBody CreateUrlRequest request) {

        ShortUrl saved = service.createShortUrl(
                request.originalUrl()
        );

        return new CreateUrlResponse(
                saved.getShortCode(),
                "http://localhost:8081/" + saved.getShortCode(),
                saved.getOriginalUrl()
        );
    }


    @GetMapping("/{code}/stats")
    public UrlStatsResponse getStats(
            @PathVariable String code) {

        ShortUrl url;

        try {
            url = service.getByShortCode(code);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Short URL not found"
            );
        }

        return new UrlStatsResponse(
                url.getShortCode(),
                "http://localhost:8081/" + url.getShortCode(),
                url.getOriginalUrl(),
                url.getClickCount(),
                url.getCreatedAt()
        );
    }
}
