package es.nextdigital.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    @Schema(description = "Identificador único de la transacción", example = "42")
    private Long id;
    @Schema(description = "Tipo de transacción (DEPOSIT, WITHDRAWAL, TRANSFER)", example = "WITHDRAWAL")
    private String type;
    @Schema(description = "Monto de la transacción", example = "100.00")
    private BigDecimal amount;
    @Schema(description = "Comisión aplicada en la transacción", example = "2.50")
    private BigDecimal commission;
    @Schema(description = "Fecha y hora de la transacción", example = "2025-12-22T20:19:25")
    private LocalDateTime timestamp;
    @Schema(description = "Cuenta asociada a la transacción")
    private AccountDTO account;
}
