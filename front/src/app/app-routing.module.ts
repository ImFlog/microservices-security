import {NgModule} from '@angular/core'
import {RouterModule} from '@angular/router'

import {Routes} from '@angular/router'
import {AppAuthGuard} from './app.authguard'
import {PrivateComponent} from "./components/private/private.component"
import {DashboardComponent} from "./components/dashboard/dashboard.component"
import {PublicComponent} from "./components/public/public.component"

const routes: Routes = [
    {
        path: 'private',
        component: PrivateComponent,
        canActivate: [AppAuthGuard],
        data: {
            roles: ["ADMIN_OVER_9K"]
        }
    },
    {
        path: 'public',
        component: PublicComponent,
        canActivate: [AppAuthGuard],
        data: {
            roles: ["yes_we_can"]
        }
    },
    {
        path: '**',
        component: DashboardComponent,
    }
]

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
    providers: [AppAuthGuard]
})
export class AppRoutingModule {
}
