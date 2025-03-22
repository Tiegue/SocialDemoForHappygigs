import { ApolloClient, InMemoryCache, split, HttpLink } from "@apollo/client";
import { GraphQLWsLink } from "@apollo/client/link/subscriptions";
import { createClient } from "graphql-ws";
import { getMainDefinition } from "@apollo/client/utilities";

import { onError } from '@apollo/client/link/error';

const httpLink = new HttpLink({ uri: "http://localhost:4446/graphql" });
const wsLink = typeof window !== "undefined" ? new GraphQLWsLink(createClient({url: "ws://localhost:4446/graphql"})) : null;
const splitLink = typeof window !== "undefined" && wsLink != null
    ? split(
        ({ query }) => {
            const def = getMainDefinition(query);
            return def.kind === "OperationDefinition" && def.operation ==="subscription";
        },
        wsLink,
        httpLink
    ) : httpLink;
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

