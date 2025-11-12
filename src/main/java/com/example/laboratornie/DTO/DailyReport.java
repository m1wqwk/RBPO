package com.example.laboratornie.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DailyReport {
    private LocalDate reportDate;
    private double totalIncome;
    private double totalRefunds;
    private long totalTransactions;
    private long completedBookings;

    public DailyReport(LocalDate reportDate, double totalIncome, double totalRefunds,
                       long totalTransactions, long completedBookings) {
        this.reportDate = reportDate;
        this.totalIncome = totalIncome;
        this.totalRefunds = totalRefunds;
        this.totalTransactions = totalTransactions;
        this.completedBookings = completedBookings;
    }
}