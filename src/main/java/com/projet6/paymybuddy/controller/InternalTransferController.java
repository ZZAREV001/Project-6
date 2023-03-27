package com.projet6.paymybuddy.controller;

import com.projet6.paymybuddy.dto.InternalTransferDto;
import com.projet6.paymybuddy.exception.DataNotFoundException;
import com.projet6.paymybuddy.service.TransferService;
import com.projet6.paymybuddy.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class InternalTransferController {

    private final TransferService transferService;

    private final UserService userService;

    @GetMapping("/transfer")
    public String internalTransferPage(Model model,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       RedirectAttributes redirectAttributes) {
        model.addAttribute("transfers",
                transferService.findInternalTransferByUser(userDetails.getUsername()));
        model.addAttribute("relations",
                userService.listEmailRelation(userDetails.getUsername()));
        model.addAttribute("internalTransfer", new InternalTransferDto());
        return "internalTransfer";
    }

    @PostMapping("/transfer/doInternalTransfer")
    public String doInternalTransfer(@ModelAttribute InternalTransferDto internalTransferDto,
                                     @AuthenticationPrincipal UserDetails userDetails)
            throws DataNotFoundException {
        internalTransferDto.setEmailSender(userDetails.getUsername());
        transferService.doInternalTransfer(internalTransferDto);
        return "redirect:/user/transfer";
    }



}
