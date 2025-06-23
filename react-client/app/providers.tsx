"use client";

import { ApolloProvider } from "@apollo/client";
import client from "../lib/apollo-client";
import React from "react";
import { AuthProvider } from "./context/AuthContext";

export default function Providers({ children }: { children: React.ReactNode }) {
    return (
        <AuthProvider>
            <ApolloProvider client={client}>{children}</ApolloProvider>
        </AuthProvider>
    );
}