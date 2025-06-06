package com.taskmanager.service;

import com.taskmanager.model.Task;
import com.taskmanager.model.TaskList;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.TaskListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskListRepository taskListRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskListRepository taskListRepository) {
        this.taskRepository = taskRepository;
        this.taskListRepository = taskListRepository;
    }

    @Transactional(readOnly = true)
    public List<Task> getTasksByList(Long listId) {
        return taskRepository.findByTaskListIdOrderByPositionAsc(listId);
    }

    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    @Transactional
    public Task createTask(Long listId, Task task) {
        TaskList taskList = taskListRepository.findById(listId)
            .orElseThrow(() -> new RuntimeException("TaskList not found with id: " + listId));

        task.setTaskList(taskList);
        
        // Set position to the end of the list if not specified
        if (task.getPosition() == null) {
            List<Task> existingTasks = getTasksByList(listId);
            task.setPosition(existingTasks.size());
        }

        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, Task taskDetails) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setCompleted(taskDetails.isCompleted());
        task.setPriority(taskDetails.getPriority());
        task.setDueDate(taskDetails.getDueDate());
        
        if (taskDetails.getPosition() != null) {
            task.setPosition(taskDetails.getPosition());
        }

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    @Transactional
    public void updateTaskPositions(Long listId, List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            Task existingTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + task.getId()));
            
            existingTask.setPosition(i);
            taskRepository.save(existingTask);
        }
    }

    @Transactional(readOnly = true)
    public List<Task> findTasksByPriorityInList(Long listId, Task.Priority priority) {
        return taskRepository.findByPriorityAndTaskListId(priority, listId);
    }

    @Transactional(readOnly = true)
    public List<Task> findCompletedTasksInList(Long listId, boolean completed) {
        return taskRepository.findByCompletedAndTaskListId(completed, listId);
    }

    @Transactional(readOnly = true)
    public List<Task> findOverdueTasksInList(Long listId) {
        return taskRepository.findByDueDateBeforeAndCompletedAndTaskListId(
            LocalDateTime.now(), false, listId);
    }

    @Transactional(readOnly = true)
    public List<Task> findTasksBetweenDatesInList(Long listId, LocalDateTime start, LocalDateTime end) {
        return taskRepository.findByDueDateBetweenAndTaskListId(start, end, listId);
    }
} 