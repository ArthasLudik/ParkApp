package org.example.park.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.park.model.dto.enums.SpotType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_spot")
@Getter
@Setter
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SpotType spotType;

    @Column(nullable = false)
    private boolean isOccupied = false;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @OneToMany(mappedBy = "spot", cascade = CascadeType.ALL)
    private List<ParkingSession> sessions = new ArrayList<>();
}