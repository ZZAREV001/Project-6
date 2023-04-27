package com.projet6.paymybuddy.controller;

import com.projet6.paymybuddy.dto.ExternalTransferDto;
import com.projet6.paymybuddy.model.BankAccount;
import com.projet6.paymybuddy.service.BankAccountService;
import com.projet6.paymybuddy.service.TransferService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class ExternalTransferController {

    private final TransferService transferService;

    private final BankAccountService bankAccountService;


    @GetMapping("/extransfer")
    public String externalTransferPage(Model model,
                                       @AuthenticationPrincipal UserDetails userDetails) {
            model.addAttribute("externalTransfers",
                    transferService.findExternalTransferByUser(userDetails.getUsername()));
            model.addAttribute("externalTransfer", new ExternalTransferDto());
            model.addAttribute("listBankAccount",
                    bankAccountService.findBankAccountByUser(userDetails.getUsername()));
            model.addAttribute("bankAccount", new BankAccount());
            return "extransfer";
    }

    @PostMapping("/extransfer/doExternalTransfer")
    public String doExternalTransfer(@ModelAttribute ExternalTransferDto externalTransferDto,
                                     @AuthenticationPrincipal UserDetails userDetails) {
        externalTransferDto.setEmailUser(userDetails.getUsername());
        transferService.doExternalTransfer(externalTransferDto);
        return "redirect:/user/extransfer";
    }

    @PostMapping("/extransfer/addBankAccount")
    public String addBankAccount(@ModelAttribute BankAccount bankAccount,
                                 @AuthenticationPrincipal UserDetails userDetails) throws SQLException {
        bankAccountService.addBankAccount(userDetails.getUsername(), bankAccount);
        return "redirect:/user/extransfer";
    }

    @PostMapping("/extransfert/deleteBankAccount")
    public String deleteBankAccount(@RequestParam String iban) {
        bankAccountService.deleteBankAccount(iban);
        return "redirect:/user/extransfer";
    }

}
