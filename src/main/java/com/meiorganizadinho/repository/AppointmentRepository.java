package com.meiorganizadinho.repository;

import com.meiorganizadinho.entity.Appointment;
import com.meiorganizadinho.entity.CashFlowStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT a FROM Appointment a WHERE a.date = :date " +
            "AND a.startTime < :endTime AND a.endTime > :startTime")
    List<Appointment> findConflictingAppointments(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime
    );

    List<Appointment> findByDateOrderByDateAscStartTimeAsc(LocalDate date);
    List<Appointment> findByDateAndStartTimeGreaterThanEqualOrderByDateAscStartTimeAsc(LocalDate date, LocalTime startTime);

    @Query(value = """
        SELECT SUM(s.value) as total_value
        FROM appointment_services aps
        INNER JOIN appointment a ON aps.id_appointment = a.id
        INNER JOIN service s ON aps.service_id = s.id
        WHERE a.date BETWEEN :startDate AND :endDate
        """, nativeQuery = true)
    Double getTotalValueByDateRange(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);

    @Query(value = """
    SELECT 
        DATE(a.date) as appointment_date,
        SUM(s.value) as total_value
    FROM 
        appointment_services aps
    INNER JOIN 
        appointment a ON aps.id_appointment = a.id
    INNER JOIN 
        service s ON aps.service_id = s.id
    WHERE 
        a.date BETWEEN :startDate AND :endDate
    GROUP BY 
        DATE(a.date)
    ORDER BY 
        appointment_date
    """, nativeQuery = true)
    List<CashFlowStatement> getDailyCashFlowReport(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
}
