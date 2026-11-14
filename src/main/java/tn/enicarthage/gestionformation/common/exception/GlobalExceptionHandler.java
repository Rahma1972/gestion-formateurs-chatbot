package tn.enicarthage.gestionformation.common.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String gererNonTrouve(ResourceNotFoundException ex, Model model) {
        String message = messageSource.getMessage(ex.getMessage(), ex.getArgs(), LocaleContextHolder.getLocale());
        model.addAttribute("message", message);
        return "error/404";
    }

    @ExceptionHandler(BusinessException.class)
    public String gererErreurMetier(BusinessException ex, Model model) {
        String message = messageSource.getMessage(ex.getMessage(), ex.getArgs(), LocaleContextHolder.getLocale());
        model.addAttribute("message", message);
        return "error/erreur-metier";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String gererAccesRefuse(AccessDeniedException ex, Model model) {
        String message = messageSource.getMessage(ex.getMessage(), null, ex.getMessage(), LocaleContextHolder.getLocale());
        model.addAttribute("message", message);
        return "error/erreur-metier";
    }
}