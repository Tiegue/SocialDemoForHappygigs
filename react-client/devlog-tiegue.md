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
## Subscriptions 
- Bug: Since the logic is correct, but both message and user list show null in the frontend.
- Reason: In EnterVenue.tsx, click "Enter Venue"
  That mutation:
  Emits to userListSink
  Emits messages to messageSink
  Then the React component updates entered = true and subscribes
  😢 Subscriptions miss the emission because they weren’t listening yet.


(Placeholder – write down your notes here about dynamically importing components.)

