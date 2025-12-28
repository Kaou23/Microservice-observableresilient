package com.example.pricing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/prices")
public class PricingController {

    private final Random random = new Random();

    /**
     * Get price for a book by ID.
     * Implements chaos engineering features:
     * - If fail=true parameter is passed, always throws exception
     * - 30% random chance of failure to simulate instability
     * 
     * @param id Book ID
     * @param fail Optional parameter to force failure
     * @return Price calculated as 50.0 + (bookId % 10) * 5.0
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPrice(
            @PathVariable Long id,
            @RequestParam(value = "fail", required = false, defaultValue = "false") boolean fail) {
        
        // Chaos Engineering: Force failure if fail=true
        if (fail) {
            throw new RuntimeException("Forced failure triggered by fail=true parameter");
        }
        
        // Chaos Engineering: Random 30% failure rate
        if (random.nextDouble() < 0.30) {
            throw new RuntimeException("Random failure occurred (30% chaos simulation)");
        }
        
        // Calculate price: 50.0 + (bookId % 10) * 5.0
        double price = 50.0 + (id % 10) * 5.0;
        
        return ResponseEntity.ok(Map.of(
                "bookId", id,
                "price", price,
                "currency", "EUR"
        ));
    }

    /**
     * Exception handler for runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleException(RuntimeException ex) {
        return ResponseEntity.internalServerError()
                .body(Map.of(
                        "error", "Pricing Service Error",
                        "message", ex.getMessage()
                ));
    }
}
