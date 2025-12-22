package es.nextdigital.demo.controller;

import es.nextdigital.demo.dto.TransactionDTO;
import es.nextdigital.demo.mapper.TransactionMapper;
import es.nextdigital.demo.model.Transaction;
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

    @PostMapping("/{atmId}/withdraw")
    public TransactionDTO withdraw(@PathVariable Long atmId,
                                   @RequestParam String cardNumber,
                                   @RequestParam BigDecimal amount) {
        Transaction tx = withdrawService.withdraw(cardNumber, atmId, amount);
        return TransactionMapper.toDTO(tx);
    }
}
