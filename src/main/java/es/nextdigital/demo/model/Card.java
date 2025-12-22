package es.nextdigital.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;

    @Enumerated(EnumType.STRING)
    private CardType type; // DEBIT o CREDIT

    private boolean active;
    private String pinHash;

    // Límite máximo asignado por el banco
    @Column(precision = 19, scale = 2)
    private BigDecimal creditLimit;

    // Crédito disponible en este momento
    @Column(precision = 19, scale = 2)
    private BigDecimal creditAvailable;

    @Column(precision = 19, scale = 2)
    private BigDecimal withdrawLimit;
    private String bankId;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
