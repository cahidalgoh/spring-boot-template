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
    public TransactionDTO withdraw(@PathVariable Long atmId,
                                   @RequestBody WithdrawRequestDTO withdrawRequestDTO) {
        Transaction tx = withdrawService.withdraw(
                withdrawRequestDTO.getCardNumber(),
                atmId,
                withdrawRequestDTO.getAmount());
        return TransactionMapper.toDTO(tx);
    }

    @PostMapping("/{atmId}/deposit")
    public TransactionDTO deposit(@PathVariable Long atmId,
                                  @RequestBody DepositRequestDTO request) {
        Transaction tx = depositService.deposit(request.getCardNumber(), atmId, request.getAmount());
        return TransactionMapper.toDTO(tx);
    }

    @PostMapping("/transfer")
    public TransactionDTO transfer(@RequestBody TransferRequestDTO request) {
        Transaction tx = transferService.transfer(
                request.getCardNumber(),
                request.getDestinationIban(),
                request.getAmount()
        );
        return TransactionMapper.toDTO(tx);
    }


}
