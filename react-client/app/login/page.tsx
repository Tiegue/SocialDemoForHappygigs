"use client"

import { useEffect } from "react"
import { useRouter } from "next/navigation"
import {useAuth} from "@/app/context/AuthContext";
import keycloak from "@/lib/keycloak";

export default function LoginPage() {
    const router = useRouter();
    const { token, login } = useAuth();

    useEffect(() => {
        if (token) {
            router.push("/entervenue");
        }
    }, [token]);

    const handleLogin = () => {
        keycloak.login({
            redirectUri: "http://localhost:3000/entervenue",
            idpHint: "google", // ✅ force Google login
        });
    };

    return (
        <main className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="bg-white p-8 rounded shadow-md text-center space-y-4">
                <h1 className="text-2xl font-semibold">Login to HappyGigs Social</h1>
                <p className="text-gray-600">Click below to login with Keycloak</p>
                <button
                    onClick={login}


                    className="px-6 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
                >
                    Login with Keycloak
                </button>
            </div>
        </main>
    );

}