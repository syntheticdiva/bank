package com.java.bank.controller;

import com.java.bank.dto.TransferRequest;
import com.java.bank.security.JwtUtil;
import com.java.bank.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;
    private final JwtUtil jwtUtil;
    @Operation(summary = "Money transfer")
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



