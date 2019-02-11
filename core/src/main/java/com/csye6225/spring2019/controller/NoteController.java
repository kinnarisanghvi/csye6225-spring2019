package com.csye6225.spring2019.controller;

import com.csye6225.spring2019.exception.ResourceNotFoundException;
import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.model.User;
import com.csye6225.spring2019.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.sql.Date;

@RestController
public class NoteController {

    @Autowired
    NoteRepository noteRepository;


    @GetMapping("/note")
    public List<Note> getAllNote() {

        return noteRepository.findAll();
    }

    @PostMapping("/note")
    public Note newNote(@Valid @RequestBody Note note) {

        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        note.setNoteId(randomUUIDString);
        java.util.Date uDate = new java.util.Date();
        java.sql.Date sDate = new java.sql.Date();
        
        System.out.println("Time in java.sql.Date is : " + sDate);
        DateFormat df = new SimpleDateFormat("dd/MM/YYYY - hh:mm:ss");
        System.out.println("Using a dateFormat date is : " + df.format(uDate));
        return noteRepository.save(note);
    }

    @GetMapping("/note/{id}")
    public Note getOneNote(@PathVariable(value = "noteid") Long noteid) {

        return noteRepository.findById(noteid).orElseThrow(() -> new ResourceNotFoundException("Note", "noteid", noteid));
    }

    @PutMapping("/note/{id}")
    public Note updateNote(@PathVariable(value = "noteid") Long noteid, @Valid @RequestBody Note note) {

        Note note1 = noteRepository.findById(noteid).orElseThrow(() -> new ResourceNotFoundException("Note", "noteid", noteid));

        note1.setNoteTitle(note.getNoteTitle());
        note1.setNoteContent(note.getNoteContent());

        Note changedNote = noteRepository.save(note1);
        return changedNote;


    }


    @DeleteMapping("/note/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable(value = "noteid") Long noteid) {
        Note note1 = noteRepository.findById(noteid).orElseThrow(() -> new ResourceNotFoundException("Note", "noteid", noteid));
        noteRepository.delete(note1);
        return ResponseEntity.ok().build();

    }
}