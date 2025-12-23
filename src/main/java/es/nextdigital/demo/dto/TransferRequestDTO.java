package es.nextdigital.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    @Schema(description = "Número de tarjeta origen", example = "1234567890123456")
    private String cardNumber;
    @Schema(description = "IBAN de la cuenta destino", example = "ES9820385778983000760236")
    private String destinationIban;
    @Schema(description = "Monto a transferir", example = "500.00")
    private BigDecimal amount;
}
