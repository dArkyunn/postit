import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {NoteService} from "../../services/note.service";
import Note from "../../models/note";

@Component({
    selector: 'app-editor',
    templateUrl: './editor.component.html',
    styleUrls: ['./editor.component.scss']
})
export class EditorComponent implements OnInit {

    public id?: number;
    public text?: string = "";

    constructor(private router: Router, private route: ActivatedRoute, private noteService: NoteService) {
        let id = this.route.snapshot.paramMap.get('id');
        if (id !== null) this.id = parseInt(id);
    }

    ngOnInit(): void {
        if (this.id === undefined) return;
        this.noteService.getNote(this.id).subscribe(response => {
            this.text = response.body?.text;
        })
    }

    public onAccept(): void {
        let note: Note = { id: this.id, text: this.text };
        if (this.id === undefined) {
            this.noteService.createNote(note).subscribe(async response => {
                if (response.ok) {
                    await this.router.navigate(['/']);
                }
            })
        } else {
            this.noteService.updateNote(note).subscribe(async response => {
                if (response.ok) {
                    await this.router.navigate(['/']);
                }
            })
        }
    }

    public onDelete(): void {
        this.noteService.deleteNote(this.id!).subscribe(async response => {
            if (response.ok) {
                await this.router.navigate(['/']);
            }
        })
    }

    public async onCancel(): Promise<void> {
        await this.router.navigate(['/']);
    }
}
