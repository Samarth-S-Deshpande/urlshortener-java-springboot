package com.samarth.urlshortener.service;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;
import com.samarth.urlshortener.entity.ShortUrl;
import com.samarth.urlshortener.exception.ResourceNotFoundException;
import com.samarth.urlshortener.repository.ShortUrlRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Service
public class ShortUrlService {

    private static final Logger log =
            LoggerFactory.getLogger(ShortUrlService.class);

    private static final String CHARACTERS =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int CODE_LENGTH = 6;
    private static final int MAX_ATTEMPTS = 5;

    private final ShortUrlRepository repository;
    private final StringRedisTemplate redisTemplate;
    private final java.security.SecureRandom random =
            new java.security.SecureRandom();

    public ShortUrlService(
            ShortUrlRepository repository,
            StringRedisTemplate redisTemplate) {

        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public ShortUrl createShortUrl(String originalUrl) {

        validateUrl(originalUrl);

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {

            String code = generateCode();

            if (repository.existsByShortCode(code)) {
                continue;
            }

            ShortUrl shortUrl = new ShortUrl();
            shortUrl.setShortCode(code);
            shortUrl.setOriginalUrl(originalUrl);

            try {
                ShortUrl saved = repository.saveAndFlush(shortUrl);

                log.info("Short URL created successfully: {}", code);

                return saved;

            } catch (DataIntegrityViolationException ex) {
                log.debug("Short code collision on attempt {}", attempt);
            }
        }

        throw new IllegalStateException(
                "Could not generate a unique short code. Please retry."
        );
    }

    @Transactional(readOnly = true)
    public ShortUrl getByShortCode(String code) {

        return repository.findByShortCode(code)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Short URL not found"));
    }


    @Transactional
    public String resolveAndCountClick(String code) {

        String cacheKey = "url:" + code;

        // 1. Check Redis first
        String originalUrl = redisTemplate
                .opsForValue()
                .get(cacheKey);

        if (originalUrl != null) {

            log.info("Redis cache HIT for code: {}", code);

        } else {

            // 2. If missing from Redis, fetch from MySQL
            ShortUrl shortUrl = repository.findByShortCode(code)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Short URL not found"));

            originalUrl = shortUrl.getOriginalUrl();

            // 3. Save the URL in Redis for 24 hours
            redisTemplate.opsForValue().set(
                    cacheKey,
                    originalUrl,
                    Duration.ofHours(24)
            );

            log.info("Redis cache MISS for code: {}", code);
        }

        // 4. Always update the click count in MySQL
        repository.incrementClickCount(code);

        log.info("Redirect requested for short code: {}", code);

        return originalUrl;
    }

    private String generateCode() {

        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(
                    CHARACTERS.charAt(
                            random.nextInt(CHARACTERS.length())
                    )
            );
        }

        return code.toString();
    }

    private void validateUrl(String url) {

        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException(
                    "URL must not be empty");
        }

        try {
            URI uri = URI.create(url);

            if (!("http".equalsIgnoreCase(uri.getScheme())
                    || "https".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null) {

                throw new IllegalArgumentException();
            }

        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Please provide a valid HTTP or HTTPS URL");
        }
    }
}

