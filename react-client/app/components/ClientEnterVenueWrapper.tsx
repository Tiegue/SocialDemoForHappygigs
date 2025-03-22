'use client';

import dynamic from "next/dynamic";

const EnterVenue = dynamic(
    () => import('./EnterVenue'), {ssr: false});

export default function ClientEnterVenueWrapper() {
    return (<EnterVenue />);
}