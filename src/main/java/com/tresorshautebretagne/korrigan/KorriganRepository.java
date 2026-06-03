package com.tresorshautebretagne.korrigan;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KorriganRepository extends JpaRepository<Korrigan, Long> {
    Optional<Korrigan> findByName(String name);
}
