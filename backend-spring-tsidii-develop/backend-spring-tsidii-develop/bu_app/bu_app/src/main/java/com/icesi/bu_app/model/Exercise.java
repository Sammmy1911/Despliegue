package com.icesi.bu_app.model;

import java.sql.Blob;
import java.util.List;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity(name = "Exercises")
@NoArgsConstructor
@SQLDelete(sql = "UPDATE Exercises SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;
    @Column(nullable = false)
    private Integer length;
    @Column(nullable = false, length = 100)
    private String description;
    
    private Blob video;

    @Column(name = "is_custom", nullable = false, columnDefinition = "boolean default false")
    private boolean custom = false;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "difficulty_id", nullable = false)
    private ExerciseDifficulty difficulty;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private ExerciseType type;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "exercise")
    @JsonIgnore
    @ToString.Exclude
    private List<RoutineExercise> routinesExercises;
}
