package com.example.controller;

import com.example.model.Person;
import com.example.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

    // Метод для добавления информации о пользователе в модель
    private void addUserInfoToModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            model.addAttribute("currentUser", authentication.getName());
            model.addAttribute("isAuthenticated", true);
        } else {
            model.addAttribute("currentUser", "Гость");
            model.addAttribute("isAuthenticated", false);
        }
    }

    // Главная страница с формой добавления и списком всех записей
    @GetMapping
    public String showPersonForm(Model model) {
        addUserInfoToModel(model);

        List<Person> persons = personService.getAllPersons();
        System.out.println("=== DEBUG: Found " + persons.size() + " persons in database ===");

        model.addAttribute("person", new Person());
        model.addAttribute("persons", persons);
        return "person-form";
    }

    // Добавление новой записи
    @PostMapping("/add")
    public String addPerson(@ModelAttribute Person person) {
        personService.savePerson(person);
        return "redirect:/persons";
    }

    // Детальный просмотр конкретной записи по ID
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

    // Страница просмотра всех записей в виде таблицы
    @GetMapping("/view/all")
    public String viewAllPersons(Model model) {
        addUserInfoToModel(model);

        List<Person> persons = personService.getAllPersons();
        model.addAttribute("persons", persons);
        model.addAttribute("totalCount", persons.size());

        return "view-all-persons";
    }

    // Страница с формами поиска
    @GetMapping("/search")
    public String showSearchForm(Model model) {
        addUserInfoToModel(model);
        return "search-form";
    }

    // Поиск по фамилии
    @GetMapping("/search/lastname")
    public String findByLastName(@RequestParam("lastName") String lastName, Model model) {
        addUserInfoToModel(model);

        List<Person> persons = personService.findByLastNameIgnoreCase(lastName);
        model.addAttribute("persons", persons);
        model.addAttribute("searchParam", "Фамилия: " + lastName);

        return "search-results";
    }

    // Поиск по имени и фамилии
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

    // Поиск по email
    @GetMapping("/search/email")
    public String findByEmail(@RequestParam("email") String email, Model model) {
        addUserInfoToModel(model);

        List<Person> persons = personService.findByEmail(email);
        model.addAttribute("persons", persons);
        model.addAttribute("searchParam", "Email: " + email);

        return "search-results";
    }

    // Поиск по возрасту (старше)
    @GetMapping("/search/age")
    public String findByAgeGreaterThan(@RequestParam("age") Integer age, Model model) {
        addUserInfoToModel(model);

        List<Person> persons = personService.findByAgeGreaterThan(age);
        model.addAttribute("persons", persons);
        model.addAttribute("searchParam", "Возраст старше: " + age);

        return "search-results";
    }

    // Метод для инициализации тестовых данных
    @GetMapping("/init-test-data")
    public String initTestData() {
        try {
            if (personService.getAllPersons().isEmpty()) {
                Person person1 = new Person("Иван", "Иванов", "ivan@mail.ru", 25);
                Person person2 = new Person("Петр", "Петров", "petr@gmail.com", 30);
                Person person3 = new Person("Мария", "Сидорова", "maria@yandex.ru", 28);

                personService.savePerson(person1);
                personService.savePerson(person2);
                personService.savePerson(person3);

                System.out.println("=== DEBUG: Test data added successfully ===");
            }
            return "redirect:/login";
        } catch (Exception e) {
            System.out.println("ERROR adding test data: " + e.getMessage());
            return "redirect:/login";
        }
    }
}