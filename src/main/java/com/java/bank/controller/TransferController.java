package com.java.bank.controller;


import com.java.bank.dto.TransferRequest;
import com.java.bank.security.JwtUtil;
import com.java.bank.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final JwtUtil jwtUtil;
    @PostMapping
    public ResponseEntity<Void> transfer(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody TransferRequest request
    ) {
        Long fromUserId = jwtUtil.extractUserId(authHeader.substring(7));
        transferService.transferFunds(fromUserId, request);
        return ResponseEntity.ok().build();
    }
}



