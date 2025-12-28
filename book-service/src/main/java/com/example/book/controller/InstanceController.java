package com.example.book.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class InstanceController {

    @Value("${server.port:8081}")
    private int serverPort;

    /**
     * Get instance information for testing multi-instance deployment.
     * Returns the hostname and internal port of this instance.
     * 
     * @return Map containing hostname and port
     */
    @GetMapping("/instance")
    public ResponseEntity<Map<String, Object>> getInstanceInfo() {
        String hostname;
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            hostname = "unknown";
        }

        return ResponseEntity.ok(Map.of(
                "hostname", hostname,
                "port", serverPort,
                "message", "Instance information retrieved successfully"));
    }
}
