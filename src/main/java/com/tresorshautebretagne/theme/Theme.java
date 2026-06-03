package com.tresorshautebretagne.theme;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.tresorshautebretagne.korrigan.Korrigan;
import com.tresorshautebretagne.treasureHunt.TreasureHunt;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "themes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "korrigan_id", nullable = false)
    private Korrigan korrigan;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL)
    private List<TreasureHunt> treasureHunts;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
