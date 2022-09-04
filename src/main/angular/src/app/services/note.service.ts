import {Injectable} from '@angular/core';
import {HttpClient, HttpParams, HttpResponse} from "@angular/common/http";
import Note from "../models/note";
import {Observable} from "rxjs";

@Injectable({
    providedIn: 'root'
})
export class NoteService {

    constructor(private http: HttpClient) {
    }

    public getAllNotes() : Observable<HttpResponse<Note[]>> {
        return this.http.get<Note[]>('/api/notes', { observe: 'response' });
    }

    public getNote(id: number) : Observable<HttpResponse<Note>> {
        return this.http.get<Note>(`/api/notes/${id}`, { observe: 'response' });
    }

    public deleteNote(id: number): Observable<HttpResponse<any>> {
        return this.http.delete(`/api/notes/${id}`, { observe: 'response' });
    }

    public createNote(note: Note): Observable<HttpResponse<any>> {
        return this.http.post('/api/notes', note, { observe: 'response' });
    }

    public updateNote(note: Note) {
        return this.http.put('/api/notes', note, { observe: 'response' });
    }
}
