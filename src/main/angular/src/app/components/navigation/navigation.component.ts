import {Component, OnInit, Renderer2} from '@angular/core';
import {Router} from "@angular/router";

@Component({
    selector: 'app-navigation',
    templateUrl: './navigation.component.html',
    styleUrls: ['./navigation.component.scss']
})
export class NavigationComponent implements OnInit {

    public darkMode: boolean = false;

    constructor(private renderer: Renderer2, private router: Router) {
    }

    ngOnInit(): void {
        const darkMode = localStorage.getItem("darkMode");
        if (darkMode === "true") {
            this.darkMode = true;
            this.renderer.addClass(document.body, 'dark')
        }
    }

    public async onCreate(): Promise<void> {
        await this.router.navigate(['/edit'])
    }

    public async onHome(): Promise<void> {
        await this.router.navigate(['/'])
    }

    public changeMode() {
        if (this.darkMode) {
            this.renderer.removeClass(document.body, 'dark')
        } else {
            this.renderer.addClass(document.body, 'dark')
        }

        this.darkMode = !this.darkMode
        localStorage.setItem("darkMode", this.darkMode.toString());
    }
}
