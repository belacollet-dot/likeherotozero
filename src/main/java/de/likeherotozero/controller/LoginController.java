package de.likeherotozero.controller;

import de.likeherotozero.entity.ScientistUser;
import de.likeherotozero.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                         @RequestParam String password,
                         HttpSession session,
                         Model model) {
        Optional<ScientistUser> authenticated = userService.authenticate(email, password);

        if (authenticated.isEmpty()) {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }

        ScientistUser user = authenticated.get();
        session.setAttribute("role", user.getRole().name());
        session.setAttribute("displayName", user.getName());
        session.setAttribute("userId", user.getId());

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
