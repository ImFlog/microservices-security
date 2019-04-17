export const environment = {
    production: true,

    apiUrl: 'http://localhost:8081/secured',
    keycloak: {
        url: 'http://localhost:8080/auth',
        realm: 'keycloak-demo',
        clientId: 'front',
    }
}
