package com.dodev.analyzer.api.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("")
    public ResponseEntity<String> getTest() {
        return ResponseEntity.ok("TEST");
    }

}