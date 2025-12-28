package com.example.book.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class PricingClient {

    private static final Logger log = LoggerFactory.getLogger(PricingClient.class);

    private final RestTemplate restTemplate;
    private final String pricingServiceUrl;

    public PricingClient(RestTemplate restTemplate,
                         @Value("${pricing.service.url}") String pricingServiceUrl) {
        this.restTemplate = restTemplate;
        this.pricingServiceUrl = pricingServiceUrl;
    }

    /**
     * Get price for a book from the pricing service.
     * Uses Retry (3 attempts) and CircuitBreaker pattern.
     * Falls back to 0.0 if all retries fail or circuit is open.
     * 
     * @param bookId Book ID
     * @return Price from pricing service, or 0.0 as fallback
     */
    @Retry(name = "pricingService", fallbackMethod = "getBookPriceFallback")
    @CircuitBreaker(name = "pricingService", fallbackMethod = "getBookPriceFallback")
    public Double getBookPrice(Long bookId) {
        log.info("Calling pricing service for book ID: {}", bookId);
        
        String url = pricingServiceUrl + "/api/prices/" + bookId;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        
        if (response != null && response.containsKey("price")) {
            Double price = ((Number) response.get("price")).doubleValue();
            log.info("Received price {} for book ID: {}", price, bookId);
            return price;
        }
        
        throw new RuntimeException("Invalid response from pricing service");
    }

    /**
     * Fallback method when pricing service is unavailable.
     * Returns 0.0 as a default price.
     * 
     * @param bookId Book ID
     * @param ex Exception that triggered the fallback
     * @return Default price of 0.0
     */
    public Double getBookPriceFallback(Long bookId, Exception ex) {
        log.warn("Fallback triggered for book ID: {}. Reason: {}", bookId, ex.getMessage());
        return 0.0;
    }
}
