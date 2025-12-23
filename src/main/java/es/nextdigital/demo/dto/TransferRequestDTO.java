package es.nextdigital.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequestDTO {
    private String cardNumber;
    private String destinationIban;
    private BigDecimal amount;
}
