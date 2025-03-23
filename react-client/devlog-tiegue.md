This is a personal devlog.  
I developed the React client from scratch, learning everything through ChatGPT.  
It's a great way to learn coding.

# How to Learn and Debug with ChatGPT

- Write clear and polished prompts that describe the task.
- Generate all the necessary code until you’re satisfied with the overall structure.
- **Important:** Avoid asking technical questions in the same dialogue. Open a new conversation specifically for debugging or technical questions.
- For example, for this React client project:
    - One dialogue was used to generate all the code.
    - Another was used to learn and debug.

## Route-Based Structure Instead of `src`

It’s amazing that the directory structure directly reflects the project structure.  
This feels cleaner and more intuitive than the traditional `src` folder in older React projects.

## CORS Needs to Be Enabled Even Without Keycloak

- **Bug:** `Failed to fetch` — the backend works via IntelliJ (IDEA), but fails in the browser.
- **Reason:** Even if there’s no authentication or security like Keycloak, the browser still enforces CORS.  
  Without proper CORS headers, you’ll encounter this error.
- **Solution:** Add a CORS config using `addCorsMappings(CorsRegistry registry)`.  
  This configuration will apply CORS headers to all routes, including `/graphql`.

## Dynamic Component Import

React hydration mismatch, a common error when using Next.js with Server-Side Rendering (SSR) and client-only behavior (like state or browser-specific code).
- solution: Create a wrapper client component.

## Bug Report: GraphQL Subscriptions Not Working (Frontend shows null)

### 🐞 Bug Description
Although the backend logic was correct, both the **message** and **user list** in the frontend appeared as `null`.

### 🔍 Root Cause
WebSocket was not enabled on the backend, so Apollo Client couldn't connect and receive GraphQL subscription data.

### 🛠 Solution
Add the following two lines to your Spring Boot `application.properties`:

```properties
spring.graphql.websocket.path=/graphql
spring.graphql.websocket.connection-init-timeout=30s
```

### 🧾 How to Verify
Check the backend startup logs after running `mvn clean install` or starting the application. You should see:

- ✅ `Netty started on port 4446 (http)`
- ✅ `GraphQL endpoint WebSocket /graphql`
- ✅ `GraphQL endpoint HTTP POST /graphql`

These log lines confirm that the server is ready to handle both HTTP and WebSocket GraphQL connections.

### 💡 Lesson Learned
> Fixing this bug took me a long time. If I had written proper unit tests, I could have found the issue more easily. Good testing saves time and helps catch integration issues like this early.

---

### 🔁 Summary of Debugging Steps

1. ❌ Frontend showed no message/user list even though backend logic seemed correct.
2. 🔍 Used `console.log` and `<pre>` debug output to confirm Apollo `messageData` was always `null`.
3. 🧪 Tested WebSocket connection manually:
   ```js
   new WebSocket("ws://localhost:4446/graphql")
   ```
   → It failed, confirming no WebSocket support was active.
4. ✅ Realized Spring Boot WebSocket for GraphQL was not enabled by default.
5. 🛠 Added configuration:
   ```properties
   spring.graphql.websocket.path=/graphql
   spring.graphql.websocket.connection-init-timeout=30s
   ```
6. ✅ Restarted backend, saw correct logs in the console.
7. 🚀 Retested in the browser → WebSocket connection succeeded.
8. 🎉 Messages and user list began showing correctly in the frontend.
9. Delay the mutation, which gives the useSubscription() time to connect before the backend sends the user list. Works well with multicast().

