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

    @Column(name = "total_cost")
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

    public void calculateCost() {
        if (exitTime != null && entryTime != null) {
            long minutes = Duration.between(entryTime, exitTime).toMinutes();
            minutes = minutes == 0 ? 1 : minutes;
            this.totalCost = spot.getHourlyRate().multiply(BigDecimal.valueOf(minutes));
        }
    }
}