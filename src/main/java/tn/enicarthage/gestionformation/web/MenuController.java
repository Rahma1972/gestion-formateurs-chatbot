package tn.enicarthage.gestionformation.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tn.enicarthage.gestionformation.security.CustomUserDetails;

@Controller
public class MenuController {

    @GetMapping("/menu")
    public String menu(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("email", userDetails.getUsername());
        model.addAttribute("role", userDetails.getUtilisateur().getRole().name());
        return "menu";
    }
}