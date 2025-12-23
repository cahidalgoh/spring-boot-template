package es.nextdigital.demo.controller;

import es.nextdigital.demo.dto.DepositRequestDTO;
import es.nextdigital.demo.dto.TransactionDTO;
import es.nextdigital.demo.dto.TransferRequestDTO;
import es.nextdigital.demo.dto.WithdrawRequestDTO;
import es.nextdigital.demo.mapper.TransactionMapper;
import es.nextdigital.demo.model.Transaction;
import es.nextdigital.demo.service.DepositService;
import es.nextdigital.demo.service.TransferService;
import es.nextdigital.demo.service.WithdrawService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/atm")
@RequiredArgsConstructor
public class ATMController {

    private final WithdrawService withdrawService;
    private final DepositService depositService;
    private final TransferService transferService;

    @PostMapping("/{atmId}/withdraw")
    @Operation(summary = "Retirar dinero", description = "Permite retirar dinero de una cuenta usando un cajero automático")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retiro exitoso"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public TransactionDTO withdraw(@PathVariable Long atmId,
                                   @RequestBody WithdrawRequestDTO withdrawRequestDTO) {
        Transaction tx = withdrawService.withdraw(
                withdrawRequestDTO.getCardNumber(),
                atmId,
                withdrawRequestDTO.getAmount());
        return TransactionMapper.toDTO(tx);
    }

    @PostMapping("/{atmId}/deposit")
    @Operation(summary = "Depositar dinero", description = "Permite depositar dinero en una cuenta usando un cajero automático")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Depósito exitoso"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    public TransactionDTO deposit(@PathVariable Long atmId,
                                  @RequestBody DepositRequestDTO request) {
        Transaction tx = depositService.deposit(request.getCardNumber(), atmId, request.getAmount());
        return TransactionMapper.toDTO(tx);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transferir dinero", description = "Permite transferir dinero desde una cuenta origen a una cuenta destino")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferencia exitosa"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cuenta origen o destino no encontrada")
    })
    public TransactionDTO transfer(@RequestBody TransferRequestDTO request) {
        Transaction tx = transferService.transfer(
                request.getCardNumber(),
                request.getDestinationIban(),
                request.getAmount()
        );
        return TransactionMapper.toDTO(tx);
    }


}
