"use client";

import { ApolloClient, InMemoryCache, split, HttpLink } from "@apollo/client";
import { GraphQLWsLink } from "@apollo/client/link/subscriptions";
import { createClient } from "graphql-ws";
import { getMainDefinition } from "@apollo/client/utilities";
import { setContext } from "@apollo/client/link/context";
import keycloak from "@/lib/keycloak";

import { onError } from '@apollo/client/link/error';

const httpLink = new HttpLink({
    uri: "http://localhost:4446/graphql"
});

const authLink = setContext(async (_, { headers }) => {
    const token = keycloak.token;
    return {
        headers: {
            ...headers,
            Authorization: token ? `Bearer ${keycloak.token}` : "",
        },
    };
});

const wsLink = new GraphQLWsLink(
    createClient({
        url: 'ws://localhost:4446/graphql', // Make sure this matches backend
        connectionParams: () => ({
            Authorization: `Bearer ${keycloak.token}`,
        }),
    })
);

const splitLink = split(
        ({ query }) => {
            const def = getMainDefinition(query);
            return def.kind === "OperationDefinition" && def.operation ==="subscription";
        },
        wsLink,
        authLink.concat(httpLink)
    );
// Log Network Error in Apollo
const errorLink = onError(({ networkError, graphQLErrors }) => {
    if (graphQLErrors)
        graphQLErrors.forEach(({ message, locations, path }) =>
            console.error(`[GraphQL error]: Message: ${message}, Path: ${path}`)
        );

    if (networkError) console.error(`[Network error]:`, networkError);
});

const client = new ApolloClient({
    link: splitLink,
    // link: errorLink.concat(splitLink), //Log Network Error in Apollo, if needed
    cache: new InMemoryCache(),
});

export default client;

