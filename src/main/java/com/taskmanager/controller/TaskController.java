package com.taskmanager.controller;

import com.taskmanager.model.Task;
import com.taskmanager.model.Task.Priority;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/lists/{listId}/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getTasksByList(@PathVariable Long listId) {
        return taskService.getTasksByList(listId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long listId, @PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@PathVariable Long listId, @Valid @RequestBody Task task) {
        Task newTask = taskService.createTask(listId, task);
        return ResponseEntity.ok(newTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long listId, @PathVariable Long id, 
                                         @Valid @RequestBody Task taskDetails) {
        try {
            Task updatedTask = taskService.updateTask(id, taskDetails);
            return ResponseEntity.ok(updatedTask);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long listId, @PathVariable Long id) {
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> updateTaskPositions(@PathVariable Long listId, @RequestBody List<Task> tasks) {
        try {
            taskService.updateTaskPositions(listId, tasks);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/priority/{priority}")
    public List<Task> getTasksByPriority(@PathVariable Long listId, 
                                        @PathVariable Task.Priority priority) {
        return taskService.findTasksByPriorityInList(listId, priority);
    }

    @GetMapping("/completed/{status}")
    public List<Task> getTasksByStatus(@PathVariable Long listId, 
                                      @PathVariable boolean status) {
        return taskService.findCompletedTasksInList(listId, status);
    }

    @GetMapping("/overdue")
    public List<Task> getOverdueTasks(@PathVariable Long listId) {
        return taskService.findOverdueTasksInList(listId);
    }

    @GetMapping("/due-between")
    public List<Task> getTasksBetweenDates(
            @PathVariable Long listId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return taskService.findTasksBetweenDatesInList(listId, start, end);
    }
} 