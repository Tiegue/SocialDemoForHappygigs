'use client';
import {useState} from "react";
export default function Counter(){
    const [count, setCount] = useState(0);

    return (
        <div className="p-4 border rounded-md">
            <p className="mb-2">Count: {count}</p>
            <button
                className="px-4 py-1 gb-blue-500 text-black rounded"
                onClick={() => setCount(count + 1)}
            >
            Increment
            </button>
        </div>
    );
}
