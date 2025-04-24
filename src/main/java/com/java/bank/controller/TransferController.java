package com.java.bank.controller;

import com.java.bank.dto.TransferRequest;
import com.java.bank.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {
    private final TransferService transferService;

    @PostMapping
    @PreAuthorize("#fromUserId == authentication.principal.id")
    public void transfer(
            @RequestParam Long fromUserId,
            @RequestBody TransferRequest request) {

        transferService.transfer(fromUserId, request.getToUserId(), request.getAmount());
    }
}

