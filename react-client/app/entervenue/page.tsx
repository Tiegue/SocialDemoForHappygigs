"use client";

import dynamic from "next/dynamic";
import { useAuth } from "@/app/context/AuthContext";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

// Dynamically import EnterVenue (client-only)
const EnterVenue = dynamic(() => import("../components/EnterVenue"), {
    ssr: false,
});

export default function EnterVenuePage() {
    const { token, logout } = useAuth();
    const router = useRouter();

    useEffect(() => {
        if ( !token) {
            router.push("/login");
        }
    }, [ token]);

 //   if (loading) return <p className="text-center mt-20">Checking login...</p>;
    if (!token) return null;

    return (
        <main className="p-8 max-w-xl mx-auto">
            <div className="flex justify-between items-center mb-4">
                <h1 className="text-2xl font-bold">HappyGigs Social</h1>
                <button
                    onClick={logout}
                    className="bg-red-500 text-white px-3 py-1 rounded"
                >
                    Logout
                </button>
            </div>
            <EnterVenue />
        </main>
    );
}
