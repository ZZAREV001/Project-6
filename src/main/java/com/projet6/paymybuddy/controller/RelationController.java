package com.projet6.paymybuddy.controller;

import com.projet6.paymybuddy.dto.BuddyFormDto;
import com.projet6.paymybuddy.exception.DataNotFoundException;
import com.projet6.paymybuddy.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class RelationController {
    private final UserService userService;

    @GetMapping("/relation")
    public String addBuddy(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("relations",
                userService.listEmailRelation(userDetails.getUsername()));
        return "relations";
    }

    @PostMapping("/addBuddy")
    public String addBuddy(@ModelAttribute("buddy")BuddyFormDto buddyFormDto,
                           @AuthenticationPrincipal UserDetails userDetails)
            throws DataNotFoundException {

        buddyFormDto.setOwner(userDetails.getUsername());
        userService.addBuddy(buddyFormDto);
        return "redirect:/user/relation?success";
    }

    @PostMapping("/relation")
    public String deleteBuddy(@RequestParam Integer id) {
        userService.deleteBuddy(id);
        return "redirect:/user/relation";
    }


}
