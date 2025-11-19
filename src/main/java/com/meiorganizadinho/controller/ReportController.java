package com.meiorganizadinho.controller;

import com.meiorganizadinho.dto.reportdto.CashFlowStatmentReportResponseDTO;
import com.meiorganizadinho.dto.reportdto.ReportRequestDTO;
import com.meiorganizadinho.dto.reportdto.RevenueReportResponseDTO;
import com.meiorganizadinho.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/reports")
public class ReportController {
    private ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/revenue-report")
    public ResponseEntity<RevenueReportResponseDTO> revenueReport(@RequestBody @Valid ReportRequestDTO reportRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(reportService.revenueReport(reportRequestDTO.startDate(), reportRequestDTO.endDate()));
    }

    @PostMapping("/cash-flow-statement")
    public ResponseEntity<CashFlowStatmentReportResponseDTO> cashFlowStatmentReport(@RequestBody @Valid ReportRequestDTO reportRequestDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(reportService.cashFlowStatmentReport(reportRequestDTO.startDate(), reportRequestDTO.endDate()));
    }
}
