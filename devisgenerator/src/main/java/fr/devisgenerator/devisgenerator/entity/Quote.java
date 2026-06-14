package fr.devisgenerator.devisgenerator.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fr.devisgenerator.devisgenerator.enums.QuoteStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="quote")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quote {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuoteStatus status = QuoteStatus.DRAFT;

    @Column(name = "total_ht")
    private BigDecimal totalHt;

    @Column(name = "total_tva")
    private BigDecimal totalTva;

    @Column(name = "total_ttc")
    private BigDecimal totalTtc;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

}