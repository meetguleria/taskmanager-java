package com.taskmanager.controller;

import com.taskmanager.model.TaskList;
import com.taskmanager.service.TaskListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/boards/{boardId}/lists")
@CrossOrigin(origins = "*")
public class TaskListController {
    private final TaskListService taskListService;

    @Autowired
    public TaskListController(TaskListService taskListService) {
        this.taskListService = taskListService;
    }

    @GetMapping
    public List<TaskList> getListsByBoardId(@PathVariable Long boardId) {
        return taskListService.getListsByBoardId(boardId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskList> getListById(@PathVariable Long boardId, @PathVariable Long id) {
        return taskListService.getListById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TaskList> createList(@PathVariable Long boardId, @Valid @RequestBody TaskList taskList) {
        TaskList newList = taskListService.createList(boardId, taskList);
        return ResponseEntity.ok(newList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskList> updateList(@PathVariable Long boardId, @PathVariable Long id, 
                                             @Valid @RequestBody TaskList listDetails) {
        try {
            TaskList updatedList = taskListService.updateList(id, listDetails);
            return ResponseEntity.ok(updatedList);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteList(@PathVariable Long boardId, @PathVariable Long id) {
        try {
            taskListService.deleteList(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> updateListPositions(@PathVariable Long boardId, @RequestBody List<TaskList> lists) {
        try {
            taskListService.updateListPositions(boardId, lists);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 