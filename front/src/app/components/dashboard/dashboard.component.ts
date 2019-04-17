import {Component, OnInit} from '@angular/core'
import {HttpClient} from '@angular/common/http'
import {environment} from '../../../environments/environment'
import {KeycloakService} from 'keycloak-angular'

@Component({
    selector: 'app-dashboard',
    templateUrl: './dashboard.component.html',
    styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
    result: string;

    constructor(private http: HttpClient, private keycloakService: KeycloakService) { }

    ngOnInit() {
        this.keycloakService.isLoggedIn().then(isLogged => {
            if (isLogged) {
                this.keycloakService.getToken().then(token => this.result = 'Logged in with token : ' + token)
            }
        })
    }

    login() {
        this.keycloakService.login();
    }

    logout() {
        this.keycloakService.logout();
    }

    getSecured() {
        this.http.get(environment.apiUrl, {
            responseType: 'text'
        }).subscribe(
            (res: string) => this.result = res,
            error => this.result = error.message
        )
    }

    getSecuredDumb() {
        this.http.get(environment.apiUrl + '/dumb', {
            responseType: 'text'
        }).subscribe(
            (res: string) => this.result = res,
            error => this.result = error.message
        )
    }

    getSecuredRole() {
        this.http.get(environment.apiUrl + '/role', {
            responseType: 'text'
        }).subscribe(
            (res: string) => this.result = res,
            error => this.result = error.message
        )
    }
}
