package com.efecte.postit;

import com.efecte.postit.model.Note;
import com.efecte.postit.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

/**
 * Represents API controller
 */
@RestController
@RequestMapping("/api")
public class NoteController {

    final NoteRepository noteRepository;

    /**
     * Instantiate the controller with <code>NoteRepository</code> using dependency injection
     * @param noteRepository JPARepository of notes
     */
    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    /**
     * Gets all notes from the database
     * @return A list of all notes
     */
    @GetMapping("/notes")
    public ResponseEntity<List<Note>> getAll() {
        List<Note> notes = this.noteRepository.findAll();
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    /**
     * Gets a specific note identified by id from the database
     * @param id ID of note to get
     * @return A note, or null if not found
     */
    @GetMapping("/notes/{id}")
    public ResponseEntity<Note> getById(@PathVariable Long id) {
        Note note = this.noteRepository.findById(id).orElse(null);
        return new ResponseEntity<>(note, note != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a note
     * @param note Note to add
     * @return Creation status
     */
    @PostMapping("/notes")
    public ResponseEntity<String> create(@RequestBody Note note) {
        if (note == null) return new ResponseEntity<>("The given entity cannot be null", HttpStatus.METHOD_NOT_ALLOWED);
        if (note.getId() != null) note.setId(null);

        this.noteRepository.save(note);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Updates a note
     * @param note Note to update
     * @return Update status
     */
    @PutMapping("/notes")
    public ResponseEntity<String> update(@NotNull @RequestBody Note note) {
        Note resp = this.noteRepository.findById(note.getId()).orElse(null);

        if (resp == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        this.noteRepository.save(note);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Deletes a note
     * @param id ID of note to delete
     * @return Deletion status
     */
    @DeleteMapping("/notes/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        Note resp = this.noteRepository.findById(id).orElse(null);

        if(resp == null) return new ResponseEntity<>("Note with id " + id + "does not exist", HttpStatus.NOT_FOUND);
        this.noteRepository.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
