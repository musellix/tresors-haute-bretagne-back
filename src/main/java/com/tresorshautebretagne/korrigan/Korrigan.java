package com.tresorshautebretagne.korrigan;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.tresorshautebretagne.theme.Theme;
import com.tresorshautebretagne.treasureHunt.dialogue.Dialogue;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "korrigans")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Korrigan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String imageUrl;

    @OneToMany(mappedBy = "korrigan", cascade = CascadeType.ALL)
    private List<Theme> themes;

    @OneToMany(mappedBy = "korrigan", cascade = CascadeType.ALL)
    private List<Dialogue> dialogues;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
