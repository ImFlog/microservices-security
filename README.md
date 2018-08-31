# Keycloak POC

This POC is composed of three projects:
- A NodeJS application serving the frontend (using https://www.npmjs.com/package/client-oauth2)
- A Gateway (Zuul)
- A Resource Server (Spring MVC)

All three linked to Keycloak to manage the authentication.

## Flow
front (public client) -> Keycloak login (Authorization server). To login use your ldap e-mail and password.

The front retrieve the tokens (id, access, refresh) after the authorization flow.

front (public client) -> gateway (bearer-only) -> resource (bearer-only)

Every call to a backend service (i.e the gateway), trigger a token verification using the Keycloak public key.
When the call hits the resource server, we use the @PreAuthorize to specify SPEL expression to validation.
Here we can call (via the frontend):
- /auth => Accessible if the user is authenticated.
- /dumb => Accessible if you have the role "DUMB_ROLE", should be very rare :)
- /role => Accessible if you have the role "GS-TAS-JIRA-Developers", you can change that according to your account permissions.

## Requirements
You need Java 8, Maven, and NodeJS.

You also need to start a local Keycloak instance, to do so you can use the provided docker-compose file:
`$ docker-compose up -d`.
The port 8080 is mapped to your local port, you should be able to login as `admin`/ `admin` on http://localhost:8080.


Then you will have to start the three apps (3 terminals)
```
./gradlew gateway:bootRun
./gradlew resource:bootRun
cd front && npm start
```
