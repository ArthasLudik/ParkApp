package org.example.park.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_session")
@Getter
@Setter
public class ParkingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", nullable = false)
    private ParkingSpot spot;

    @PrePersist
    public void setEntryTime() {
        this.entryTime = LocalDateTime.now();
    }

    @PreUpdate
    public void calculateCost() {
        if (exitTime != null && entryTime != null) {
            long hours = Duration.between(entryTime, exitTime).toHours();
            hours = hours == 0 ? 1 : hours;
            this.totalCost = spot.getHourlyRate().multiply(BigDecimal.valueOf(hours));
        }
    }
}