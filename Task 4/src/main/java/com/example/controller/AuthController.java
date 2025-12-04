package com.example.controller;

import com.example.model.Role;
import com.example.model.User;
import com.example.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }
        if (logout != null) {
            model.addAttribute("message", "Вы успешно вышли из системы");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success",
                    "Регистрация прошла успешно! Теперь вы можете войти в систему.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/persons";
    }

    /**
     * Метод для создания администратора (для тестирования)
     * Пароль: admin123
     */
    @GetMapping("/create-admin")
    public String createAdminUser(RedirectAttributes redirectAttributes) {
        try {
            if (userService.getUserByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@example.com");
                admin.setPassword("admin123");
                admin.setFullName("Системный администратор");
                admin.setRole(Role.ADMIN);

                userService.saveUser(admin);

                redirectAttributes.addFlashAttribute("success",
                        "Администратор успешно создан! Логин: admin, Пароль: admin123");
            } else {
                redirectAttributes.addFlashAttribute("info",
                        "Администратор уже существует в системе");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка при создании администратора: " + e.getMessage());
        }
        return "redirect:/login";
    }

    /**
     * Страница для просмотра всех пользователей (для отладки)
     * Только для администраторов
     */
    @GetMapping("/view-users")
    public String viewAllUsers(Model model, RedirectAttributes redirectAttributes) {
        try {
            // В реальном приложении здесь должна быть проверка роли ADMIN
            var users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("totalUsers", users.size());
            return "view-users"; // Нужно создать этот шаблон
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка: " + e.getMessage());
            return "redirect:/login";
        }
    }
}