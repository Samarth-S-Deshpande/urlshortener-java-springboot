
package com.samarth.urlshortener.repository;

import com.samarth.urlshortener.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ShortUrlRepository
        extends JpaRepository<ShortUrl, Long> {

    Optional<ShortUrl> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    @Modifying
    @Transactional
    @Query("""
        UPDATE ShortUrl s
        SET s.clickCount = s.clickCount + 1
        WHERE s.shortCode = :shortCode
        """)
    int incrementClickCount(
            @Param("shortCode") String shortCode
    );
}