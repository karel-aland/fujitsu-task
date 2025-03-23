package com.example.deliveryfeecalculator.controller;

import com.example.deliveryfeecalculator.model.FeeRule;
import com.example.deliveryfeecalculator.service.FeeRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fee-rules")
public class FeeRulesController {

    private final FeeRuleService feeRuleService;

    @Autowired
    public FeeRulesController(FeeRuleService feeRuleService) {
        this.feeRuleService = feeRuleService;
    }

    // GET: All fee rules
    @GetMapping
    public ResponseEntity<List<FeeRule>> getAllFeeRules() {
        return ResponseEntity.ok(feeRuleService.getAllFeeRules());
    }

    // GET: Specific city (ex. /api/fee-rules/Tartu)
    @GetMapping("/{city}")
    public ResponseEntity<List<FeeRule>> getFeeRulesByCity(@PathVariable String city) {
        List<FeeRule> rules = feeRuleService.getFeeRulesByCity(city);

        if (rules.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(rules);
    }

    // GET: Specific fee rule for city and vehicle type
    @GetMapping(params = {"city", "vehicleType"})
    public ResponseEntity<?> getFeeRule(@RequestParam String city, @RequestParam String vehicleType,
                                        @RequestParam(required = false) String datetime,
                                        @RequestParam(required = false) Double temperature,
                                        @RequestParam(required = false) Double windSpeed) {
        try {
            if (datetime != null) {
                // If datetime exists
                LocalDateTime requestTime = LocalDateTime.parse(datetime);
                List<FeeRule> feeRules = feeRuleService.getFeeRuleByCityAndVehicleWithDateTime(city, vehicleType, requestTime);
                if (feeRules.isEmpty()) {
                    return ResponseEntity.notFound().build();
                }
            }
            // If datetime does not exist
            Double baseFee = feeRuleService.calculateBaseFee(city, vehicleType, temperature, windSpeed, datetime != null ? LocalDateTime.parse(datetime) : null);

            // Return base fee by MAP
            Map<String, Object> response = new HashMap<>();
            response.put("baseFee", baseFee);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(400).body("ERROR: " + e.getMessage());
        }
    }

    // POST: Add new fee rule
    @PostMapping
    public ResponseEntity<FeeRule> createFeeRule(@RequestBody FeeRule feeRule) {
        return ResponseEntity.ok(feeRuleService.saveFeeRule(feeRule));
    }

    // PUT: Edit an existing fee rule
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFeeRule(@PathVariable Long id, @RequestBody FeeRule updatedRule) {
        try {
            return ResponseEntity.ok(feeRuleService.updateFeeRule(id, updatedRule));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE: Delete an existing fee rule
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeeRule(@PathVariable Long id) {
        feeRuleService.deleteFeeRule(id);
        return ResponseEntity.noContent().build();
    }
}
