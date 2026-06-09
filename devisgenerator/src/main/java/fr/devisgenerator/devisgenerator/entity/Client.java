package fr.devisgenerator.devisgenerator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="clients")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false, unique=true)
    private String email;

    private String phone;

    private String address;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
}