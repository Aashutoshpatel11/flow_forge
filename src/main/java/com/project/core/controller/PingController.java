package com.project.core.controller;

import com.project.core.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ping")
public class PingController {

    @GetMapping("/me")
    public ResponseEntity<String> pingme(){
        return ResponseEntity.ok().body("Spanked");
    }


    @PostMapping("/me")
    public ResponseEntity<Object> postMe(@RequestBody Object o){
        return ResponseEntity.ok().body(o);
    }

}
