import {BrowserModule} from '@angular/platform-browser'
import {APP_INITIALIZER, NgModule} from '@angular/core'
import {KeycloakAngularModule, KeycloakService} from 'keycloak-angular'
import {HttpClientModule} from '@angular/common/http'

import {AppComponent} from './app.component'
import {DashboardComponent} from './components/dashboard/dashboard.component'
import {PrivateComponent} from './components/private/private.component'
import {environment} from "../environments/environment"
import {AppRoutingModule} from "./app-routing.module"
import {PublicComponent} from './components/public/public.component'
import {MatButtonModule} from "@angular/material"
import {BrowserAnimationsModule} from "@angular/platform-browser/animations"

@NgModule({
    declarations: [
        AppComponent,
        DashboardComponent,
        PrivateComponent,
        PublicComponent,
    ],
    imports: [
        BrowserModule,
        HttpClientModule,
        KeycloakAngularModule,
        AppRoutingModule,
        MatButtonModule,
        BrowserAnimationsModule
    ],
    providers: [{
        provide: APP_INITIALIZER,
        useFactory: initializer,
        multi: true,
        deps: [KeycloakService]
    }],
    bootstrap: [AppComponent]
})
export class AppModule {
}

export function initializer(keycloak: KeycloakService): () => Promise<any> {
    return (): Promise<any> => {
        return new Promise(async (resolve, reject) => {
            try {
                await keycloak.init({
                    config: environment.keycloak,
                    initOptions: {
                        // onLoad: 'login-required',
                        checkLoginIframe: false
                    },
                    bearerExcludedUrls: []
                })
                resolve()
            } catch (error) {
                reject(error)
            }
        })
    }
}
