package com.tresorshautebretagne.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "steps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "treasure_hunt_id", nullable = false)
    private TreasureHunt treasureHunt;

    @Column(nullable = false)
    private Integer stepOrder;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // GPS coordinates for this step
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    // Radius in meters - user must be within this radius to access the step
    @Column(nullable = false)
    private Integer radiusMeters = 50;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    private List<Dialogue> dialogues;

    @OneToMany(mappedBy = "step", cascade = CascadeType.ALL)
    private List<Question> questions;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
