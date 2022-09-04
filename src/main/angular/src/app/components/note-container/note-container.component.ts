import {Component, OnInit} from '@angular/core';
import Note from 'src/app/models/note';
import {NoteService} from "../../services/note.service";
import {Router} from "@angular/router";

@Component({
    selector: 'app-note-container',
    templateUrl: './note-container.component.html',
    styleUrls: ['./note-container.component.scss']
})
export class NoteContainerComponent implements OnInit {

    public notes: Note[] = []

    constructor(private router: Router, private noteService: NoteService) {  }

    ngOnInit(): void {
        this.noteService.getAllNotes().subscribe(response => {
            if (!response.ok) return;

            this.notes = response.body as Note[];
        })
    }

    public getIdTracking(index: number, item: Note): number | undefined {
        return item.id;
    }

    public onNoteDeleted(id: number, arrayPos: number): void {
        this.notes.splice(arrayPos, 1);
    }

    public async moveToNote(arrayPos: number): Promise<void> {
        let note = this.notes[arrayPos];
        if (note === undefined) return;

        await this.router.navigate(['/edit', note.id])
    }

    public get sortedNotes() {
        return this.notes.sort((a: Note, b: Note) => {
            return a.id! - b.id!;
        })
    }
}


