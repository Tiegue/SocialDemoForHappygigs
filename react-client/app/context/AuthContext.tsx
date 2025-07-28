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
    // const [loading, setLoading] = useState(true);

    useEffect(() => {
        //setLoading(true);
        keycloak.init({
            onLoad: "check-sso",//login-required
            // redirectUri: "http://localhost:3000/entervenue",
        }).then((authenticated) => {
            if (authenticated) {
                const token = keycloak.token ?? null;
                const parsed = keycloak.tokenParsed;

                console.log("🔐 Token parsed:", parsed);
                console.log("🔐 Roles:", parsed?.realm_access?.roles);

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
