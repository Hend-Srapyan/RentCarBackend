package com.example.rentcar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private Customer customer;
    @ManyToOne
    private Vehicle vehicle;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateFrom;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date dateTo;
    private int total;
    @Enumerated(EnumType.STRING)
    private Status status;
}