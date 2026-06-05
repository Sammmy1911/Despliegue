package com.icesi.bu_app.model;

import java.util.List;
import java.sql.Timestamp;
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
@Entity(name = "Progresses")
@NoArgsConstructor
@SQLDelete(sql = "UPDATE Progresses SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Progress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer repetitions;
    @Column(nullable = false, length = 100)
    private String time;

    @Column(name = "performed_at", nullable = false)
    private Timestamp performedAt;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "stress_level_id", nullable = false)
    private StressLevel stressLevel;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private ProgressType progressType;

    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    private User trainee;

    @OneToMany(mappedBy = "progress")
    @JsonIgnore
    @ToString.Exclude
    private List<Recommendation> recommendations;

    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;
}
