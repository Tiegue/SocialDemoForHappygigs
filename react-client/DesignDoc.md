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

## 📁 Frontend Project Structure (React + Apollo + Next.js App Router)

```
react-client/
├── app/                        # Next.js App Router root
│   ├── components/             # Shared React components
│   │   ├── EnterVenue.tsx      # Main venue-entry component with subscriptions
│   │   └── ClientEnterVenueWrapper.tsx  # Dynamic client-only wrapper for EnterVenue
│   ├── layout.tsx              # Root layout wrapper (includes Providers)
│   ├── page.tsx                # Main page entry (renders ClientEnterVenueWrapper)
│   └── providers.tsx           # ApolloProvider wrapper for GraphQL context
│
├── lib/
│   └── apollo-client.ts        # Apollo Client config (includes wsLink for subscriptions)
│
├── styles/
│   └── globals.css             # Tailwind and global styles
│
├── public/                     # Static assets (if needed)
│
├── tsconfig.json               # TypeScript config
├── tailwind.config.ts          # Tailwind CSS configuration
├── postcss.config.js           # PostCSS for Tailwind
├── package.json                # Project dependencies and scripts
└── README.md                   # Project documentation
```

---

### ✅ Notes

- Uses **Next.js App Router** (`app/` folder instead of `pages/`)
- Subscriptions handled via **Apollo Client + graphql-ws**
- Styled with **Tailwind CSS**
- Organized for **clean component separation** (app/lib/components)
- `ClientEnterVenueWrapper.tsx` dynamically imports the `EnterVenue` component on the client only

---

## 🧠 Frontend Main Logic Overview (React + Apollo + Next.js)

This section describes the **main logic**, **component relationships**, and **file responsibilities** in the SocialDemo frontend project.

---

### 📁 Key Files and Their Roles

#### `app/page.tsx`
- The root route page (`/`)
- Renders the `<EnterVenue />` component inside the `<Providers>` wrapper

#### `app/layout.tsx`
- The root layout file used by Next.js App Router
- Wraps all pages with shared structure and styles
- Includes the `Providers` component to inject the Apollo context

#### `app/providers.tsx`
- Wraps the application with `<ApolloProvider client={client} />`
- Makes Apollo Client available to all components using GraphQL hooks

#### `lib/apollo-client.ts`
- Configures Apollo Client:
  - HTTP link for queries/mutations
  - WebSocket link for subscriptions
  - Uses `split()` to route between HTTP and WS
- Creates and exports a singleton Apollo Client

#### `app/components/ClientEnterVenueWrapper.tsx`
- A client-only wrapper for `<EnterVenue />`
- Dynamically imports `EnterVenue` with `ssr: false` to avoid hydration mismatch

#### `app/components/EnterVenue.tsx`
- Core interactive component where users:
  - Input `userId` and `venueId`
  - Trigger mutations (`enterVenue`, `leaveVenue`)
  - Subscribe to:
    - `receiveMessages` → single-message updates
    - `receiveUserList` → list of users in venue
- Displays:
  - Input form
  - Current users
  - Latest message

---

### 🔗 Logical Flow (Click "Enter Venue")

1. User types `userId` and `venueId` → clicks **Enter Venue**
2. `entered` state becomes `true`
3. Apollo calls `ENTER_VENUE` mutation to backend
4. React subscriptions start (due to `skip: !entered`)

#### Subscriptions now activate:
- `receiveMessages(userId)` → shows new arrivals
- `receiveUserList(userId)` → shows who is already present

5. Backend pushes messages/user list via WebSocket
6. Apollo Client receives updates → triggers React re-render

---

### 🔄 Click "Leave Venue"

1. User clicks **Leave Venue**
2. Calls `LEAVE_VENUE` mutation
3. On completion → sets `entered = false`
4. Subscriptions automatically stop (skipped again)
5. UI resets

---

### 🧠 Component + Data Relationship Summary

```txt
page.tsx
  └── layout.tsx
        └── providers.tsx
              └── EnterVenue.tsx
                     ├── useMutation(ENTER_VENUE)
                     ├── useSubscription(receiveMessages)
                     ├── useSubscription(receiveUserList)
                     └── useMutation(LEAVE_VENUE)
```

---

### ✅ React GraphQL Hook Flow Summary

| File | Purpose |
|------|---------|
| `apollo-client.ts` | Defines the connection logic (HTTP vs WS) |
| `providers.tsx` | Injects Apollo Client into React |
| `EnterVenue.tsx` | Handles user logic, UI, GraphQL operations |
| `page.tsx` | Loads the component at runtime |


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
