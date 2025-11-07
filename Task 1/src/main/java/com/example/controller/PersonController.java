package com.example.controller;

import com.example.model.Person;
import com.example.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/persons")
public class PersonController {
    @Autowired
    private PersonService personService;

    // Главная страница с формой добавления
    @GetMapping
    public String showPersonForm(Model model) {
        model.addAttribute("person", new Person());
        model.addAttribute("persons", personService.getAllPersons());
        return "person-form";
    }

    // Добавление новой записи
    @PostMapping("/add")
    public String addPerson(@ModelAttribute Person person) {
        personService.savePerson(person);
        return "redirect:/persons";
    }

    // Просмотр всех записей
    @GetMapping("/all")
    public String getAllPersons(Model model) {
        model.addAttribute("persons", personService.getAllPersons());
        return "person-list";
    }

    // Поиск по фамилии (форма)
    @GetMapping("/search")
    public String showSearchForm() {
        return "search-form";
    }

    // Поиск по фамилии
    @GetMapping("/search/lastname")
    public String findByLastName(@RequestParam String lastName, Model model) {
        List<Person> persons = personService.findByLastNameIgnoreCase(lastName);
        model.addAttribute("persons", persons);
        model.addAttribute("searchParam", "Фамилия: " + lastName);
        return "search-results";
    }

    // Поиск по имени и фамилии
    @GetMapping("/search/fullname")
    public String findByFullName(@RequestParam String firstName,
                                 @RequestParam String lastName,
                                 Model model) {
        List<Person> persons = personService.findByFirstNameAndLastName(firstName, lastName);
        model.addAttribute("persons", persons);
        model.addAttribute("searchParam", "Имя: " + firstName + ", Фамилия: " + lastName);
        return "search-results";
    }

    // Поиск по email
    @GetMapping("/search/email")
    public String findByEmail(@RequestParam String email, Model model) {
        List<Person> persons = personService.findByEmail(email);
        model.addAttribute("persons", persons);
        model.addAttribute("searchParam", "Email: " + email);
        return "search-results";
    }

    // Поиск по возрасту (старше)
    @GetMapping("/search/age")
    public String findByAgeGreaterThan(@RequestParam Integer age, Model model) {
        List<Person> persons = personService.findByAgeGreaterThan(age);
        model.addAttribute("persons", persons);
        model.addAttribute("searchParam", "Возраст старше: " + age);
        return "search-results";
    }
}
