package com.management.inventory.history.controller;

import com.management.inventory.history.dto.HistoryRequestDTO;
import com.management.inventory.history.dto.HistoryResponseDTO;
import com.management.inventory.history.service.HistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @PostMapping("/withdraw")
    public ResponseEntity<HistoryResponseDTO> registerWithdrawal(@RequestBody HistoryRequestDTO request) throws Exception{
        HistoryResponseDTO response = historyService.registerWithdrawal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/return")
    public ResponseEntity<HistoryResponseDTO> registerReturn(@RequestBody HistoryRequestDTO request) throws Exception {
        HistoryResponseDTO response = historyService.registerReturn(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
