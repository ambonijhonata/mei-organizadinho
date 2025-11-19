package com.meiorganizadinho.dto.reportdto;

import com.meiorganizadinho.entity.CashFlowStatement;

import java.util.List;

public record CashFlowStatmentReportResponseDTO(
        List<CashFlowStatement> report
) {
}
