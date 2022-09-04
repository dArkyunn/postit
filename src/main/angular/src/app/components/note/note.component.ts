import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Router} from "@angular/router";
import {NoteService} from "../../services/note.service";

@Component({
    selector: 'app-note',
    templateUrl: './note.component.html',
    styleUrls: ['./note.component.scss']
})
export class NoteComponent implements OnInit {

    @Input() public id?: number;

    constructor(private router: Router, private noteService: NoteService) {
    }

    ngOnInit(): void { }
}
