package com.taskmanager.service;

import com.taskmanager.model.Board;
import com.taskmanager.model.TaskList;
import com.taskmanager.repository.BoardRepository;
import com.taskmanager.repository.TaskListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TaskListService {
    private final TaskListRepository taskListRepository;
    private final BoardRepository boardRepository;

    @Autowired
    public TaskListService(TaskListRepository taskListRepository, BoardRepository boardRepository) {
        this.taskListRepository = taskListRepository;
        this.boardRepository = boardRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskList> getListsByBoardId(Long boardId) {
        return taskListRepository.findByBoardIdOrderByPositionAsc(boardId);
    }

    @Transactional(readOnly = true)
    public Optional<TaskList> getListById(Long id) {
        return taskListRepository.findById(id);
    }

    @Transactional
    public TaskList createList(Long boardId, TaskList taskList) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new RuntimeException("Board not found with id: " + boardId));

        taskList.setBoard(board);
        
        // Set position to the end of the list if not specified
        if (taskList.getPosition() == null) {
            List<TaskList> existingLists = getListsByBoardId(boardId);
            taskList.setPosition(existingLists.size());
        }

        return taskListRepository.save(taskList);
    }

    @Transactional
    public TaskList updateList(Long id, TaskList listDetails) {
        TaskList list = taskListRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("List not found with id: " + id));

        list.setName(listDetails.getName());
        if (listDetails.getPosition() != null) {
            list.setPosition(listDetails.getPosition());
        }

        return taskListRepository.save(list);
    }

    @Transactional
    public void deleteList(Long id) {
        TaskList list = taskListRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("List not found with id: " + id));
        taskListRepository.delete(list);
    }

    @Transactional
    public void updateListPositions(Long boardId, List<TaskList> lists) {
        for (int i = 0; i < lists.size(); i++) {
            TaskList list = lists.get(i);
            TaskList existingList = taskListRepository.findById(list.getId())
                .orElseThrow(() -> new RuntimeException("List not found with id: " + list.getId()));
            
            existingList.setPosition(i);
            taskListRepository.save(existingList);
        }
    }
} 