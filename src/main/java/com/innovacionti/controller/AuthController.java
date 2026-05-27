package com.innovacionti.controller;

import com.innovacionti.domain.User;
import com.innovacionti.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String root(HttpSession session) {
        return userService.currentUser(session).isPresent() ? "redirect:/home" : "redirect:/login";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "register", required = false, defaultValue = "false") boolean register,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "message", required = false) String message,
                        Model model) {
        model.addAttribute("showRegister", register);
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        model.addAttribute("registerUser", new User());
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String email,
                          @RequestParam String password,
                          HttpSession session,
                          Model model) {
        try {
            User user = userService.authenticate(email, password);
            userService.login(session, user);
            return "redirect:/home";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("showRegister", false);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("message", null);
            model.addAttribute("registerUser", new User());
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("showRegister", true);
        model.addAttribute("registerUser", new User());
        return "login";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String alias,
                             @RequestParam String email,
                             @RequestParam String password,
                             Model model,
                             HttpSession session) {
        try {
            if (!userService.isInstitutionalEmail(email)) {
                throw new IllegalArgumentException("Solo se permiten correos institucionales para el registro.");
            }
            User user = new User(email, password, alias, true, true, "STUDENT");
            userService.save(user);
            userService.login(session, user);
            return "redirect:/home";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("showRegister", true);
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("registerUser", new User());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        userService.logout(session);
        return "redirect:/login";
    }
}
