import {Injectable} from '@angular/core'
import {ActivatedRouteSnapshot, Router, RouterStateSnapshot} from '@angular/router'
import {KeycloakAuthGuard, KeycloakService} from 'keycloak-angular'

@Injectable()
export class AppAuthGuard extends KeycloakAuthGuard {

    constructor(protected router: Router, protected keycloakService: KeycloakService) {
        super(router, keycloakService)
    }

    isAccessAllowed(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Promise<boolean> {
        return new Promise(async (resolve, reject) => {
            if (!this.authenticated) {
                this.keycloakService.login()
                return
            }

            const requiredRoles = route.data.roles
            if (!requiredRoles || requiredRoles.length === 0) {
                return resolve(true)
            } else {
                if (!this.roles || this.roles.length === 0) {
                    this.router.navigate([''])
                    resolve(false)
                }
                let granted: boolean = false
                for (const requiredRole of requiredRoles) {
                    if (this.roles.indexOf(requiredRole) > -1) {
                        granted = true
                        break
                    }
                }

                if (!granted) {
                    // Redirect hack as sometimes we hit https://github.com/angular/angular/issues/16211
                    this.router.navigate([''])
                }
                resolve(granted)
            }
        })
    }
}
