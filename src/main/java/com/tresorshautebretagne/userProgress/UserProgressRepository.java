package com.tresorshautebretagne.userProgress;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    Optional<UserProgress> findByUserIdAndTreasureHuntId(Long userId, Long treasureHuntId);
    List<UserProgress> findByUserId(Long userId);
}
