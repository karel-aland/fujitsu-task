package com.example.deliveryfeecalculator.repository;

import com.example.deliveryfeecalculator.model.FeeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeeRuleRepository extends JpaRepository<FeeRule, Long> {

    // Searches for a fee rule by city and vehicle type
    Optional<FeeRule> findByCityAndVehicleType(String city, String vehicleType);

    // Searches for all fee rules by city
    List<FeeRule> findByCity(String city);

    // Returns the base fee
    @Query("SELECT f.baseFee FROM FeeRule f WHERE LOWER(f.city) = LOWER(:city) AND LOWER(f.vehicleType) = LOWER(:vehicleType)")
    Optional<Double> findFeeByCityAndVehicleType(@Param("city") String city, @Param("vehicleType") String vehicleType);

    // Searches for all valid fee rules by city, vehicle type and datetime
    @Query("SELECT f FROM FeeRule f WHERE f.city = :city AND f.vehicleType = :vehicleType AND f.validFrom <= :datetime AND (f.validTo IS NULL OR f.validTo >= :datetime)")
    List<FeeRule> findValidFeeRules(@Param("city") String city, @Param("vehicleType") String vehicleType, @Param("datetime") LocalDateTime datetime);
}


