package com.icesi.bu_app.model;

import java.sql.Timestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "Alerts")
@NoArgsConstructor
@SQLDelete(sql = "UPDATE Alerts SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "send_date")
    private Timestamp sendDate;

    @Column(nullable = false, length = 100)
    private String message;

    @Column(name = "is_deleted", nullable = false, columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private User trainer;

    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    private User trainee;
}
