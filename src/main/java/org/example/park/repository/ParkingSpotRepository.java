package org.example.park.repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.park.model.entity.ParkingSpot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    boolean existsByNumber(@NotNull @Size(max = 20) String number);

    ParkingSpot findByNumber(String number);

    List<ParkingSpot> findByIsOccupiedFalse();

    void deleteByNumber(String number);
}
