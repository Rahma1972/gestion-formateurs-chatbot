package tn.enicarthage.gestionformation.dashboard.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import tn.enicarthage.gestionformation.dashboard.dto.DashboardDTO;
import tn.enicarthage.gestionformation.dashboard.service.DashboardService;

import java.util.ArrayList;

@Controller
public class DashboardController {

    private final DashboardService dashboardService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) throws JsonProcessingException {
        DashboardDTO dashboard = dashboardService.construireDashboard();
        model.addAttribute("dashboard", dashboard);

        model.addAttribute("statutLabelsJson", objectMapper.writeValueAsString(new ArrayList<>(dashboard.getCyclesParStatut().keySet())));
        model.addAttribute("statutValuesJson", objectMapper.writeValueAsString(new ArrayList<>(dashboard.getCyclesParStatut().values())));
        model.addAttribute("moisLabelsJson", objectMapper.writeValueAsString(dashboard.getMoisLabels()));
        model.addAttribute("moisValuesJson", objectMapper.writeValueAsString(dashboard.getCyclesParMois()));
        model.addAttribute("evalLabelsJson", objectMapper.writeValueAsString(new ArrayList<>(dashboard.getMoyenneEvaluationParCycle().keySet())));
        model.addAttribute("evalValuesJson", objectMapper.writeValueAsString(new ArrayList<>(dashboard.getMoyenneEvaluationParCycle().values())));

        return "dashboard/index";
    }
}