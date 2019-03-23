# Keycloak POC

## Support

This repo contains the materials for the conference "La sécurité dans une architecture microservices ? Pas de soucis !"

You can find the slides (in french) in the "slides" directory

## Global

This POC is composed of four projects:

- An angular application serving the frontend (using [keycloak-angular](https://www.npmjs.com/package/keycloak-angular))
- A Gateway (Zuul + Spring)
- Two Resource Server : one in spring and the other in dotnet

All three linked to Keycloak to manage the authentication.

## Flow

front (public client) -> Keycloak login (Authorization server). To login use your ldap e-mail and password.

The front retrieve the tokens (id, access, refresh) after the authorization flow.

front (public client) -> gateway (bearer-only) -> resource (bearer-only)

Every call to a backend service (i.e the gateway), trigger a token verification using the Keycloak public key.
When the call hits the resource server, we use the @PreAuthorize to specify SPEL expression to validation.
Here we can call (via the frontend):

- /auth => Accessible if the user is authenticated.
- /dumb => Accessible if you have the role "dumb", should be very rare :)
- /role => Accessible if you have the role "yes_we_can", you can change that according to your account permissions.

## Requirements

You need Java 11, dotnet core, Docker and NodeJS.

To run the project, launch `start.sh`
