package es.nextdigital.demo.controller;

import es.nextdigital.demo.dto.TransactionDTO;
import es.nextdigital.demo.mapper.TransactionMapper;
import es.nextdigital.demo.service.TransactionService;
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
    public List<TransactionDTO> getTransactions(@PathVariable String iban) {
        return transactionService.getTransactionsByAccountIban(iban)
                .stream()
                .map(TransactionMapper::toDTO)
                .toList();
    }
}
