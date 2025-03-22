"use client";

import {gql, useMutation, useSubscription } from "@apollo/client";
import {useState} from "react";

const ENTER_VENUE = gql`
    mutation EnterVenue($userId: String!, $venueId: String!) {
        userEnteredVenue(userId: $userId, venueId: $venueId) 
    }
`;

const LEAVE_VENUE = gql`
    mutation LeaveVenue($userId: String!, $venueId: String!) {
        userLeftVenue(userId: $userId, venueId: $venueId)
    }
`;

const RECEIVE_MESSAGE = gql`
    subscription OnMessage($userId: String!) {
        receiveMessages(userId: $userId){
            sender
            receiver
            content
            timestamp
        }
    }
`;
const RECEIVE_USER_LIST = gql`
    subscription OnUserList($userId: String!) {
        receiveUserList(userId: $userId)
    }
`;

export default function EnterVenue() {
    const [userId, setUserId] = useState("");
    const [venueId, setVenueId] = useState("");
    const [entered, setEntered] = useState(false);

    const [enterVenue] = useMutation(ENTER_VENUE, {
        variables: {userId, venueId},
        onCompleted: () => {setEntered(true);},
    });

    const [leaveVenue] = useMutation(LEAVE_VENUE, {
        variables: {userId, venueId},
        onCompleted: () => {setEntered(false);},
    });

    const { data: messageData } = useSubscription(RECEIVE_MESSAGE, {
        variables: {userId, venueId},
        skip: !entered,
    });

    const { data: userListData } = useSubscription(RECEIVE_USER_LIST, {
        variables: {userId},
        skip: !entered,
    })

    return (
        <div className="max-w-md mx-auto bg-white shadow p-6 rounded-lg">
            <h2 className="text-xl font-semibold mb-4 text-center">Enter a Venue</h2>
            <div className="space-y-2">
                <input
                    placeholder="User ID"
                    value={userId}
                    onChange={(e) => setUserId(e.target.value)}
                    className="w-full border px-3 py-2 rounded-md"
                />
                <input
                    placeholder="Venue ID"
                    value={venueId}
                    onChange={(e) => setVenueId(e.target.value)}
                    className="w-full border px-3 py-2 rounded-md"
                />
                <button
                    onClick={() => enterVenue()}
                    className="w-full bg-blue-20 text-white py-10 rounded-md hover:bg-blue-700 transition"
                >
                    Enter Venue
                </button>
            </div>

            {entered && (
                <div className="mt-6 space-y-4">
                    <div>
                        <h3 className="font-semibold">Live Messages:</h3>
                        {messageData?.receiveMassages ? (
                            <div className="text-sm text-gray-700">
                                <strong>{messageData.receiverMessages.sender}:</strong> {messageData.receiveMessages.content}{' '}
                                <span className="text-sm text-grey-500">({messageData.receiverMessages.timestamp})</span>
                            </div>
                        ) : (
                            <p className="text-sm text-gray-500">No messages yet.</p>
                        )}
                    </div>

                    <div>
                        <h3 className="font-semibold">Current Users:</h3>
                        <ul className="list-disc ml-5 text-sm">
                            {userListData?.receiveUserList?.map((user: string) => (
                                <li key={user}>{user}</li>
                            ))}
                        </ul>
                    </div>

                    <button
                        onClick={() => leaveVenue()}
                        className="w-full bg-red-500 text-white py-2 rounded-md hover:bg-red-600 transition"
                    >
                        Leave Venue
                    </button>
                </div>
            )}
        </div>
    );
}