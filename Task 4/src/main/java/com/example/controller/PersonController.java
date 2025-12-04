package com.example.controller;

import com.example.model.Person;
import com.example.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/persons")
public class PersonController {

    private final PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

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

    private boolean isAdminUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    @GetMapping
    public String showPersonForm(Model model) {
        addUserInfoToModel(model);

        List<Person> persons = personService.getAllPersons();
        model.addAttribute("persons", persons);

        // Добавляем пустую персону ТОЛЬКО если пользователь - админ
        if (isAdminUser()) {
            model.addAttribute("person", new Person());
        }

        return "person-form";
    }

    @PostMapping("/add")
    public String addPerson(@ModelAttribute Person person) {
        if (!isAdminUser()) {
            return "redirect:/persons?error=access_denied";
        }

        personService.savePerson(person);
        return "redirect:/persons";
    }

    @GetMapping("/view/{id}")
    public String viewPersonById(@PathVariable("id") Long id, Model model) {
        addUserInfoToModel(model);

        Optional<Person> person = personService.getPersonById(id);
        if (person.isPresent()) {
            model.addAttribute("person", person.get());
            return "view-person-details";
        } else {
            model.addAttribute("errorMessage", "Запись с ID " + id + " не найдена");
            return "error";
        }
    }

    @GetMapping("/view/all")
    public String viewAllPersons(Model model) {
        addUserInfoToModel(model);

        List<Person> persons = personService.getAllPersons();
        model.addAttribute("persons", persons);
        model.addAttribute("totalCount", persons.size());

        return "view-all-persons";
    }

    @GetMapping("/search")
    public String showSearchForm(Model model) {
        addUserInfoToModel(model);
        return "search-form";
    }

    @GetMapping("/search/lastname")
    public String findByLastName(@RequestParam("lastName") String lastName, Model model) {
        addUserInfoToModel(model);

        List<Person> persons = personService.findByLastNameIgnoreCase(lastName);
        model.addAttribute("persons", persons);
        model.addAttribute("searchParam", "Фамилия: " + lastName);

        return "search-results";
    }

    @GetMapping("/search/fullname")
    public String findByFullName(@RequestParam("firstName") String firstName,
                                 @RequestParam("lastName") String lastName,
                                 Model model) {
        addUserInfoToModel(model);

        List<Person> persons = personService.findByFirstNameAndLastName(firstName, lastName);
        model.addAttribute("persons", persons);
        model.addAttribute("searchParam", "Имя: " + firstName + ", Фамилия: " + lastName);

        return "search-results";
    }

    @GetMapping("/search/email")
    public String findByEmail(@RequestParam("email") String email, Model model) {
        addUserInfoToModel(model);

        List<Person> persons = personService.findByEmail(email);
        model.addAttribute("persons", persons);
        model.addAttribute("searchParam", "Email: " + email);

        return "search-results";
    }

    @GetMapping("/search/age")
    public String findByAgeGreaterThan(@RequestParam("age") Integer age, Model model) {
        addUserInfoToModel(model);

        List<Person> persons = personService.findByAgeGreaterThan(age);
        model.addAttribute("persons", persons);
        model.addAttribute("searchParam", "Возраст старше: " + age);

        return "search-results";
    }
}