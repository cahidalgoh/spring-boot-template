package es.nextdigital.demo.controller;

import es.nextdigital.demo.dto.TransactionDTO;
import es.nextdigital.demo.mapper.TransactionMapper;
import es.nextdigital.demo.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final TransactionService transactionService;


    @GetMapping("/{iban}/transactions")
    @Operation(
            summary = "Consultar transacciones",
            description = "Devuelve el historial de transacciones de una cuenta bancaria a partir de su IBAN"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historial devuelto correctamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public List<TransactionDTO> getTransactions(@PathVariable String iban) {
        return transactionService.getTransactionsByAccountIban(iban)
                .stream()
                .map(TransactionMapper::toDTO)
                .toList();
    }
}
