package com.project.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class PingController {

    @GetMapping("/pingme")
    public ResponseEntity<String> pingme(){
        return ResponseEntity.ok().body("Spanked");
    }

}
