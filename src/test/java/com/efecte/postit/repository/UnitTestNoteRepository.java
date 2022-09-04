package com.efecte.postit.repository;

import com.efecte.postit.repository.NoteRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UnitTestNoteRepository extends NoteRepository {
    @Transactional
    @Modifying
    @Query(
            value = "TRUNCATE TABLE note; ALTER SEQUENCE note_id_seq RESTART WITH 1",
            nativeQuery = true
    )
    void truncate();
}
