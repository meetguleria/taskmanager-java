package com.taskmanager.service;

import com.taskmanager.model.Board;
import com.taskmanager.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional(readOnly = true)
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Board> getBoardById(Long id) {
        return boardRepository.findById(id);
    }

    @Transactional
    public Board createBoard(Board board) {
        return boardRepository.save(board);
    }

    @Transactional
    public Board updateBoard(Long id, Board boardDetails) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));

        board.setName(boardDetails.getName());
        board.setDescription(boardDetails.getDescription());

        return boardRepository.save(board);
    }

    @Transactional
    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Board not found with id: " + id));
        boardRepository.delete(board);
    }
} 