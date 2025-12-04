package com.example.controller;

import com.example.model.Person;
import com.example.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')") // Только для админов
public class AdminController {

    private final PersonService personService;

    @Autowired
    public AdminController(PersonService personService) {
        this.personService = personService;
    }

    // Метод для добавления информации о пользователе в модель
    private void addUserInfoToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("currentUser", authentication.getName());
            model.addAttribute("isAuthenticated", true);

            // Проверяем роль администратора
            boolean isAdmin = false;
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    isAdmin = true;
                    break;
                }
            }
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("currentUser", "Гость");
            model.addAttribute("isAuthenticated", false);
            model.addAttribute("isAdmin", false);
        }
    }

    // Панель администратора - полный CRUD
    @GetMapping("/persons")
    public String adminPanel(Model model) {
        addUserInfoToModel(model);
        List<Person> persons = personService.getAllPersons();
        model.addAttribute("persons", persons);
        model.addAttribute("totalCount", persons.size());
        return "admin-panel"; // Исправлено: возвращаем admin-panel из корневой папки
    }

    // Форма для создания новой персоны
    @GetMapping("/persons/new")
    public String showCreateForm(Model model) {
        addUserInfoToModel(model);
        model.addAttribute("person", new Person());
        model.addAttribute("isEdit", false);
        return "admin-person-form"; // Исправлено: возвращаем admin-person-form из корневой папки
    }

    // Создание новой персоны
    @PostMapping("/persons")
    public String createPerson(@ModelAttribute Person person, RedirectAttributes redirectAttributes) {
        personService.savePerson(person);
        redirectAttributes.addFlashAttribute("success", "Запись успешно создана!");
        return "redirect:/admin/persons";
    }

    // Форма для редактирования персоны
    @GetMapping("/persons/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        addUserInfoToModel(model);
        Optional<Person> person = personService.getPersonById(id);
        if (person.isPresent()) {
            model.addAttribute("person", person.get());
            model.addAttribute("isEdit", true);
            return "admin-person-form"; // Исправлено: возвращаем admin-person-form из корневой папки
        } else {
            redirectAttributes.addFlashAttribute("error", "Запись не найдена!");
            return "redirect:/admin/persons";
        }
    }

    // Обновление персоны
    @PostMapping("/persons/{id}")
    public String updatePerson(@PathVariable Long id, @ModelAttribute Person person,
                               RedirectAttributes redirectAttributes) {
        person.setId(id);
        personService.savePerson(person);
        redirectAttributes.addFlashAttribute("success", "Запись успешно обновлена!");
        return "redirect:/admin/persons";
    }

    // Удаление персоны
    @GetMapping("/persons/{id}/delete")
    public String deletePerson(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            personService.deletePerson(id);
            redirectAttributes.addFlashAttribute("success", "Запись успешно удалена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении записи: " + e.getMessage());
        }
        return "redirect:/admin/persons";
    }
}