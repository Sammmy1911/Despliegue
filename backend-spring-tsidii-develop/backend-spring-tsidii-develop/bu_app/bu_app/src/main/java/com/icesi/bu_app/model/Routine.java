package com.icesi.bu_app.model;

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
@Entity(name = "Routines")
@NoArgsConstructor
@SQLDelete(sql = "UPDATE Routines SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    @ManyToOne
    @JoinColumn(name = "trainee_id")
    private User trainee;

    @OneToMany(mappedBy = "routine")
    @JsonIgnore
    @ToString.Exclude
    private List<RoutineExercise> routinesExercises;

    @OneToMany(mappedBy = "routine")
    @JsonIgnore
    @ToString.Exclude
    private List<Progress> progresses;
}
