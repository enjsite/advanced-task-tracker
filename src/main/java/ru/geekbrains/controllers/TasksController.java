package ru.geekbrains.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.geekbrains.entities.Task;
import ru.geekbrains.services.ProjectService;
import ru.geekbrains.services.TaskHistoryService;
import ru.geekbrains.services.TasksService;
import ru.geekbrains.services.UserService;

import java.io.Serializable;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TasksController implements Serializable {

    private TasksService tasksService;
    private ProjectService projectService;
    private UserService userService;
    private TaskHistoryService taskHistoryService;

    public TasksController(TasksService tasksService, ProjectService projectService, UserService userService, TaskHistoryService taskHistoryService) {
        this.tasksService = tasksService;
        this.projectService = projectService;
        this.userService = userService;
        this.taskHistoryService = taskHistoryService;
    }

    @GetMapping("/")
    public String showTasks(Model model) {
        List<Task> tasksList = tasksService.findAll();
        model.addAttribute("tasks", tasksList);
        return "tasks/tasks-list";
    }

    @GetMapping("/create")
    public String createTask(Model model, Principal principal, @ModelAttribute(name = "task") Task task) {
        task.setManager_id(userService.getUser(principal.getName()).getId());
        model.addAttribute("urgencyList", task.getUrgency().values());
        model.addAttribute("statusList", task.getStatus().values());
        model.addAttribute("projectList", projectService.findAll());
        model.addAttribute("userList", userService.findAll());
        return "tasks/create-task";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute(name = "task") Task task) {
        tasksService.save(task);
        return "redirect:/tasks/";
    }

    @GetMapping("/edit")
    public String editTask(Model model, Principal principal, @RequestParam(name = "id", required = false) Long id) throws Exception {
        Task task = null;
        if (id != null) {
            task = tasksService.findById(id);
        } else {
            throw new Exception("Id отсутствует");
        }
        model.addAttribute("editor", userService.getUser(principal.getName()));
        model.addAttribute("task", task);
        model.addAttribute("urgencyList", task.getUrgency().values());
        model.addAttribute("statusList", task.getStatus().values());
        model.addAttribute("projectList", projectService.findAll());
        model.addAttribute("userList", userService.findAll());
        return "tasks/edit-task";
    }

    @PostMapping("/edit")
    public String saveModifiedProduct(@ModelAttribute(name = "task") Task task, Principal principal) {
        tasksService.save(task, principal);
        return "redirect:/tasks/";
    }

    @GetMapping("/show")
    public String showTask(Model model, @RequestParam(name = "id") Long id) throws Exception {
        Task task = null;
        if (id != null) {
            task = tasksService.findById(id);
        } else {
            throw new Exception("Id не указан");
        }
        model.addAttribute("task", task);
        model.addAttribute("manager", userService.findById(task.getManager_id()));
        model.addAttribute("employer", userService.findById(task.getEmployer_id()));
        model.addAttribute("project", projectService.findById(task.getProject_id()));
        return "tasks/show-task";
    }

}
