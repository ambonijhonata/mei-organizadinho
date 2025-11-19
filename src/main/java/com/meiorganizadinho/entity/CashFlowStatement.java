package com.meiorganizadinho.entity;


import java.time.LocalDate;

public interface CashFlowStatement {
    LocalDate getAppointmentDate();
    Double getTotalValue();
}
