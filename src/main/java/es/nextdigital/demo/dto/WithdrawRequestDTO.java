package es.nextdigital.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequestDTO {
    @Schema(description = "Número de tarjeta asociado a la cuenta", example = "1234567890123456")
    private String cardNumber;
    @Schema(description = "Monto a retirar", example = "100.00")
    private BigDecimal amount;
}
