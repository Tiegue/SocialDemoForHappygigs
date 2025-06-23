"use client";

import {createContext, useContext, useEffect, useState } from "react";
import keycloak from "@/lib/keycloak";

type AuthContextType = {
    token: string | null;
    username: string | null;
    roles: string[];
    login: () => void;
    logout: () => void;
};

const AuthContext = createContext<AuthContextType>({
    token: null,
    username: null,
    roles: [],
    login: () => {},
    logout: () => {},
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [token, setToken] = useState<string | null>(null);
    const [username, setUsername] = useState<string | null>(null);
    const [roles, setRoles] = useState<string[]>([]);

    useEffect(() => {
        keycloak.init({onLoad: "login-required"}).then((authenticated) => {
            if (authenticated) {
                setToken(keycloak.token ?? null);
                setUsername(keycloak.tokenParsed?.preferred_username ?? null);
                setRoles(keycloak.realmAccess?.roles ?? []);
            }
        });

        keycloak.onTokenExpired = () => {
            keycloak.updateToken(60).then((refreshed) => {
                if (refreshed) {
                    setToken(keycloak.token ?? null);
                }
            });
        };

    }, []);

    const login = () => {
        keycloak.login();
    };

    const logout = () => {
        keycloak.logout();
    };

    return (
        <AuthContext.Provider value={{ token, username, roles, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    return useContext(AuthContext);
}
