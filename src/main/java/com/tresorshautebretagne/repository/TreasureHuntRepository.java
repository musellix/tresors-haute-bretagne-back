package com.tresorshautebretagne.repository;

import com.tresorshautebretagne.entity.TreasureHunt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TreasureHuntRepository extends JpaRepository<TreasureHunt, Long> {
    List<TreasureHunt> findByThemeId(Long themeId);
    List<TreasureHunt> findByIsActiveTrue();
}
