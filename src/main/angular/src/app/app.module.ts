import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {RouterModule} from "@angular/router";
import {HttpClientModule} from "@angular/common/http";

import {AppComponent} from './app.component';
import {NoteComponent} from './components/note/note.component';
import {NoteContainerComponent} from './components/note-container/note-container.component';
import {NavigationComponent} from './components/navigation/navigation.component';
import {EditorComponent} from './components/editor/editor.component';
import {FormsModule} from "@angular/forms";

@NgModule({
    declarations: [
        AppComponent,
        NoteComponent,
        NoteContainerComponent,
        NavigationComponent,
        EditorComponent
    ],
    imports: [
        BrowserModule,
        HttpClientModule,
        RouterModule.forRoot([
            {path: '', component: NoteContainerComponent},
            {path: 'edit', component: EditorComponent},
            {path: 'edit/:id', component: EditorComponent},
            {path: '**', redirectTo: ''}
        ], {useHash: true}),
        FormsModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
