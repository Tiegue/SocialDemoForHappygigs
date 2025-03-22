# 🧩 React Client Design Doc for HappyGigs Social

## Overview

This project is a **Next.js App Router** frontend built to connect with the HappyGigs GraphQL-based social module. It enables real-time communication when users enter shared venues.

---

## Goals

- ✅ Provide a clean UI for entering/leaving venues.
- ✅ Automatically notify users already in the venue.
- ✅ Stream user join messages and update the user list in real-time.
- ✅ Use modern React with App Router and Apollo Client (WebSocket + HTTP).
- ✅ Tailwind CSS for styling.

---

## Tech Stack

| Layer        | Technology             |
|--------------|-------------------------|
| Framework    | Next.js 14+ (App Router) |
| Language     | TypeScript              |
| GraphQL      | Apollo Client           |
| Realtime     | WebSocket (`graphql-ws`)|
| Styling      | Tailwind CSS            |
| State Mgmt   | React Hooks + Apollo    |

---

## Project Structure
react-client/ ├── app/ │ ├── layout.tsx # Root layout with Apollo provider │ ├── page.tsx # Home page with EnterVenue component │ ├── providers.tsx # ApolloProvider wrapper │ └── components/ │ └── EnterVenue.tsx # UI + mutation/subscriptions logic ├── lib/ │ └── apollo-client.ts # Apollo Client setup (HTTP + WebSocket) ├── public/ # Static assets ├── tailwind.config.ts # Tailwind config ├── postcss.config.mjs # Tailwind processor ├── tsconfig.json # TypeScript config └── package.json


---

## Main Features

### ✅ Enter Venue

- Mutation: `userEnteredVenue(userId, venueId)`
- Triggers:
    - Message broadcast to existing users
    - User list push to the new user

### ✅ Live Subscriptions

- `receiveMessages(userId)` – Shows who just entered
- `receiveUserList(userId)` – Sends current user list to newcomer

### ✅ Leave Venue

- Mutation: `userLeftVenue(userId, venueId)`
- Ends live subscriptions and clears UI state.

---

## UI Components

### 🧩 `EnterVenue.tsx`
- Controlled form inputs for `userId` and `venueId`
- `useMutation()` for enter/leave actions
- `useSubscription()` for messages and user list
- Clean Tailwind-based styling

---

## Styling

- Tailwind classes directly used in components
- Future option to abstract with `@apply` in `globals.css`

---

## Example Usage

```bash
# Start backend
cd graphqlserver/
mvn spring-boot:run

# Start frontend
cd react-client/
npm install
npm run dev
