package com.taskmanager.repository;

import com.taskmanager.model.Task;
import com.taskmanager.model.Task.Priority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    // JpaRepository provides basic CRUD operations by default
    
    // Find tasks by priority
    List<Task> findByPriority(Priority priority);
    
    // Find tasks by completion status
    List<Task> findByCompleted(boolean completed);
    
    // Find tasks due before a certain date
    List<Task> findByDueDateBefore(LocalDateTime date);
    
    // Find overdue incomplete tasks
    @Query("SELECT t FROM Task t WHERE t.completed = false AND t.dueDate < CURRENT_TIMESTAMP")
    List<Task> findOverdueTasks();
    
    // Find tasks by priority and completion status
    List<Task> findByPriorityAndCompleted(Priority priority, boolean completed);
    
    // Find tasks due between two dates
    List<Task> findByDueDateBetween(LocalDateTime start, LocalDateTime end);

    List<Task> findByTaskListIdOrderByPositionAsc(Long taskListId);
    List<Task> findByCompletedAndTaskListId(boolean completed, Long taskListId);
    List<Task> findByPriorityAndTaskListId(Task.Priority priority, Long taskListId);
    List<Task> findByDueDateBeforeAndCompletedAndTaskListId(LocalDateTime date, boolean completed, Long taskListId);
    List<Task> findByDueDateBetweenAndTaskListId(LocalDateTime start, LocalDateTime end, Long taskListId);
} 