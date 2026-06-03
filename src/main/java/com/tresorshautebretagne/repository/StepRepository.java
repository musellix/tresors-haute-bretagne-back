package com.tresorshautebretagne.repository;

import com.tresorshautebretagne.entity.Step;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByTreasureHuntIdOrderByStepOrder(Long treasureHuntId);
}
