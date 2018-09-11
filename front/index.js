const ClientOAuth2 = require('client-oauth2')
const Querystring = require('querystring')

const hogan = require('hogan-express');
const request = require('request');
const cookieParser = require('cookie-parser');


const REFRESH_TOKEN_COOKIE = 'MyRefreshToken';
const ACCESS_TOKEN_COOKIE = 'MyAccessToken';
const CLIENT_ID = 'front';

// OIDC endpoints can be found here : https://www.keycloak.org/docs/latest/server_admin/index.html#keycloak-server-oidc-uri-endpoints
// We rely on https://www.npmjs.com/package/client-oauth2 for OIDC code flow
var clientAuth = new ClientOAuth2({
    clientId: CLIENT_ID,
    accessTokenUri: 'http://localhost:8080/auth/realms/master/protocol/openid-connect/token',
    authorizationUri: 'http://localhost:8080/auth/realms/master/protocol/openid-connect/auth',
    redirectUri: 'http://localhost:3000/auth/callback',
    scopes: ['openid']
});

var express = require('express')
var app = express()

// To render template
app.set('view engine', 'html');
app.set('views', require('path').join(__dirname, '/view'));
app.engine('html', hogan);

// To use cookies in requests
app.use(cookieParser());

const server = app.listen(3000, function () {
    const host = server.address().address;
    const port = server.address().port;
    console.log('Example app listening at http://%s:%s', host, port);
});

app.get('/', function (req, res) {
    res.render('index', { result: req.cookies[ACCESS_TOKEN_COOKIE] });
});

app.get('/login', function (req, res) {
    var uri = clientAuth.code.getUri()

    res.redirect(uri)
});


// TODO : we need to manage tokens expiration


app.get('/auth/callback', function (req, res) {
    clientAuth.code.getToken(req.originalUrl)
        .then(function (user) {
            // We store the tokens into cookies.
            res.cookie(ACCESS_TOKEN_COOKIE, user.accessToken, { httpOnly: true });
            res.cookie(REFRESH_TOKEN_COOKIE, user.refreshToken, { httpOnly: true });
            return res.render('index', {
                result: user.accessToken
            });
        });
});

app.get('/forward/auth', function (req, res) {
    request.get('http://localhost:8081/test/auth', {
        headers: {
            'Authorization': 'Bearer ' + req.cookies[ACCESS_TOKEN_COOKIE]
        }
    }, function (err, httpResponse, body) {
        console.log(httpResponse);
        res.render('index', {
            result: httpResponse.statusCode + ' : ' + body,
            event: 'forwarded request'
        })
    })
});

// Should be a way to use the client-oauth2 here too
app.get('/refresh', function (req, res) {
    request.post({
        url: 'http://localhost:8080/auth/realms/test/protocol/openid-connect/token', form: {
            grant_type: 'refresh_token',
            refresh_token: req.cookies[REFRESH_TOKEN_COOKIE],
            client_id: CLIENT_ID
        }
    }, function (err, httpResponse, body) {
        console.log(httpResponse);

        let tokens = JSON.parse(body);

        // Just to check that the tokens changed
        console.log(tokens['access_token'] != req.cookies[ACCESS_TOKEN_COOKIE]);
        console.log(tokens['refresh_token'] != req.cookies[REFRESH_TOKEN_COOKIE]);

        res.cookie(ACCESS_TOKEN_COOKIE, tokens['access_token'], { httpOnly: true });
        res.cookie(REFRESH_TOKEN_COOKIE, tokens['refresh_token'], { httpOnly: true });

        res.render('index', {
            result: httpResponse.statusCode,
            event: 'refresh_token'
        });
    });
});

// TODO : add code to logout from a realm
app.get('/logout', function (req, res) {
    res.clearCookie(REFRESH_TOKEN_COOKIE);
    res.clearCookie(ACCESS_TOKEN_COOKIE);
    // logout + redirect to /
    res.redirect('http://localhost:8080/auth/realms/test/protocol/openid-connect/logout?redirect_uri=http%3A%2F%2Flocalhost%3A3000%2F');
});
