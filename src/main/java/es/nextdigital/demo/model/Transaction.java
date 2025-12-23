package es.nextdigital.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // WITHDRAWAL, DEPOSIT, FEE, TRANSFER_IN, TRANSFER_OUT

    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    // Para registrar la comisión aplicada en transferencias interbancarias
    @Column(precision = 19, scale = 2)
    private BigDecimal commission;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

}
