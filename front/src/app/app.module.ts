import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { KeycloakService, KeycloakAngularModule } from 'keycloak-angular';
import { HttpClientModule } from '@angular/common/http';
import { initializer } from './utils/app-init';

import { AppComponent } from './app.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';

@NgModule({
    declarations: [
        AppComponent,
        DashboardComponent
    ],
    imports: [
        BrowserModule, HttpClientModule, KeycloakAngularModule
    ],
    providers: [{
        provide: APP_INITIALIZER,
        useFactory: initializer,
        multi: true,
        deps: [KeycloakService]
    }],
    bootstrap: [AppComponent]
})
export class AppModule { }
