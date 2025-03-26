
# 📄 SocialDemo – Real-Time Venue Presence System (Design Summary)

## 🎯 Purpose
A real-time application where users can **enter a venue**, see who’s already there, and be **notified when new users join** — powered by Spring Boot (WebFlux + GraphQL Subscriptions) and a React + Apollo Client frontend.

---

## 🧩 Backend (Spring Boot 3.4 + WebFlux + GraphQL)

### ✔️ Features:
- Uses **GraphQL subscriptions** via WebSocket (`graphql-ws` protocol)
- **Sinks.Many** emit live events:
    - `messageSink`: emits join notifications (filtered by receiver)
    - `userListSink`: emits user lists (filtered per new user only)
- Maintains **venue-specific user tracking** via `VenuePresence`
- Isolated **per-user filtering** ensures only the intended user receives the user list

### ⚙️ Tools:
- `spring-boot-starter-graphql`
- `Sinks.many().multicast().onBackpressureBuffer()` for real-time push
- `UserListPayload` record for targeted list delivery
- Supports multiple venues and clean message routing

---

## 💻 Frontend (Next.js + Apollo Client + Tailwind CSS)

### ✔️ Features:
- Clean UI built with **Next.js App Router**
- Apollo Client configured for **GraphQL over WebSocket**
- `<EnterVenue />` component:
    - Inputs user and venue ID
    - Subscribes to `receiveMessages` and `receiveUserList`
    - Triggers `enterVenue` and `leaveVenue` mutations
- Uses `useEffect` + delay to ensure subscription is live **before** mutation fires

### 🗂 Structure Highlights:
- `app/page.tsx` loads `ClientEnterVenueWrapper` (no SSR)
- `lib/apollo-client.ts` handles WebSocket + HTTP split link
- State-driven UI conditionally renders messages and user lists

---

## 🔄 Realtime Logic Flow

1. User B enters venue
2. Backend:
    - Emits a message to all existing users: “User B entered your venue”
    - Emits a list of existing users only to B
3. React frontend:
    - Receives message (if current user ≠ B)
    - Receives user list (if current user = B)
4. UI updates live via GraphQL subscriptions
