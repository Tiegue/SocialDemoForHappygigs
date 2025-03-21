import Image from "next/image";
import Counter from './components/Counter';

export default function Home(){
    return (
        <main className="p-8">
            <h1 className="text-2xl font-bold mb-4">Welcome to Next.js App Router.</h1>
            <Counter />
        </main>
    );
}
// export default async function Home() {
//     const res = await fetch("https://jsonplaceholder.typicode.com/todos/1");
//     const data = await res.json();
//
//     return (
//         <main>
//             <h1>Server-Side Fetching</h1>
//             <p>{data.title}</p>
//         </main>
//     );
// }
