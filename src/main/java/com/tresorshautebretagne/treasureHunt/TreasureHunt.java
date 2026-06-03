package com.tresorshautebretagne.treasureHunt;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.tresorshautebretagne.treasureHunt.step.Step;
import com.tresorshautebretagne.userProgress.UserProgress;
import com.tresorshautebretagne.theme.Theme;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "treasure_hunts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TreasureHunt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @Column(nullable = false)
    private Double finalLatitude;

    @Column(nullable = false)
    private Double finalLongitude;

    private String treasureImageUrl;

    @OneToMany(mappedBy = "treasureHunt", cascade = CascadeType.ALL)
    private List<Step> steps;

    @OneToMany(mappedBy = "treasureHunt", cascade = CascadeType.ALL)
    private List<UserProgress> userProgresses;

    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
