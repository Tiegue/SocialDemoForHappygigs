
import ClientEnterVenueWrapper from './components/ClientEnterVenueWrapper';


export default function Home(){
    return (
        <main className="p-8 max-w-xl mx-auto">
            <h1 className="text-2xl font-bold mb-4">HappyGigs Social</h1>
            <ClientEnterVenueWrapper />
        </main>
    );
}

