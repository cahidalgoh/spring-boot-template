package es.nextdigital.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    @Schema(description = "Identificador único de la cuenta", example = "1")
    private Long id;
    @Schema(description = "IBAN de la cuenta", example = "ES9820385778983000760236")
    private String iban;
    @Schema(description = "Saldo actual de la cuenta", example = "1500.00")
    private BigDecimal balance;
}
