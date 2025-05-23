import {defineConfig} from '@playwright/test';

// export default defineConfig({
//     testDir: './tests',
//     fullyParallel: true,
//     forbidOnly: !!process.env.CI,
//     retries: process.env.CI ? 2 : 0,
//     workers: process.env.CI ? 1 : undefined,
//     reporter: 'html',
//     use: {
//         baseURL: 'http://localhost:3000',
//         trace: 'on-first-retry',
//     },
// });

export default defineConfig({
    testDir: './tests',
    timeout: 30_000,
    retries: 1,
    use: {
        baseURL: 'http://localhost:3000',
        headless: true,
        trace: 'on-first-retry',
    },
    // webServer: {
    //     command: 'npm run dev',
    //     port: 3000,
    // },
    reporter: [['html', { open: 'never' }]],
});