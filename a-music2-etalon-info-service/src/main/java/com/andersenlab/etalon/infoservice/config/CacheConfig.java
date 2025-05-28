package com.andersenlab.etalon.infoservice.config;

import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableCaching
@EnableScheduling
@Slf4j
public class CacheConfig {

  public static final String RATES_RESPONSE_CACHE = "rates_response";
  public static final int CACHE_LIFETIME = 12 * 60 * 60 * 1000;

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager(RATES_RESPONSE_CACHE);
  }

  @CacheEvict(
      allEntries = true,
      value = {RATES_RESPONSE_CACHE})
  @Scheduled(fixedDelay = CACHE_LIFETIME, initialDelay = 500, zone = "${app.default.timezone}")
  public void reportHourlyCacheEvict() {
    log.info("Flush Hourly Cache " + Instant.now());
  }

  @CacheEvict(
      allEntries = true,
      value = {RATES_RESPONSE_CACHE})
  @Scheduled(cron = "0 45 0 * * ?", zone = "${app.default.timezone}")
  public void reportDailyCacheEvict() {
    log.info("Flush Daily Cache " + Instant.now());
  }
}
