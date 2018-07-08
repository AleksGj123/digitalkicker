package com.bechtle.config;

import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.config.ConfigFactory;
import org.pac4j.core.matching.PathMatcher;
import org.pac4j.http.client.direct.DirectBasicAuthClient;
import org.pac4j.http.credentials.authenticator.RestAuthenticator;

public class Pac4JConfig implements ConfigFactory {

    @Override
    public Config build(final Object... parameters) {
        RestAuthenticator authenticator = new RestAuthenticator("http://rest-api-url");
        DirectBasicAuthClient directBasicAuthClient = new DirectBasicAuthClient(authenticator);

        final Clients clients = new Clients("http://localhost:8080/callback", directBasicAuthClient);

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer("ROLE_ADMIN"));
        config.addMatcher("excludedPath", new PathMatcher().excludeRegex("^/facebook/notprotected$"));
        return config;
    }
}