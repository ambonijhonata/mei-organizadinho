package com.meiorganizadinho.service;

import com.meiorganizadinho.dto.reportdto.CashFlowStatmentReportResponseDTO;
import com.meiorganizadinho.dto.reportdto.RevenueReportResponseDTO;
import com.meiorganizadinho.entity.CashFlowStatement;
import com.meiorganizadinho.exception.BusinessException;
import com.meiorganizadinho.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class ReportService {
    private AppointmentRepository appointmentRepository;
    public ReportService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public RevenueReportResponseDTO revenueReport(LocalDate startDate, LocalDate endDate) {
        if(!isPeriodLessThanOneYear(startDate, endDate)) {
            throw new BusinessException("The reporting period should be a maximum of 1 year.");
        }

        Double totalValue = appointmentRepository.getTotalValueByDateRange(startDate, endDate);
        return new RevenueReportResponseDTO(totalValue);
    }

    public CashFlowStatmentReportResponseDTO cashFlowStatmentReport(LocalDate startDate, LocalDate endDate) {
        if(!isPeriodLessThanOneYear(startDate, endDate)) {
            throw new BusinessException("The reporting period should be a maximum of 1 year.");
        }

        List<CashFlowStatement> report = appointmentRepository.getDailyCashFlowReport(startDate, endDate);
        return new CashFlowStatmentReportResponseDTO(report);
    }

    private boolean isPeriodLessThanOneYear(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }

        Period period = Period.between(startDate, endDate);
        return period.getYears() < 1 ||
                (period.getYears() == 1 && period.getMonths() == 0 && period.getDays() == 0);
    }
}
