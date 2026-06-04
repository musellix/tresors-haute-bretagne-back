package com.tresorshautebretagne.userProgress;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProximityCheckResult {
    private boolean withinRange;
    private int distanceMeters;
    private int radiusMeters;
}
