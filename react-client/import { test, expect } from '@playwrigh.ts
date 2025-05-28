import { test, expect } from '@playwright/test';

// playwright.config.test.ts

const mockMessages = {
    data: {
        receiveMessages: {
            sender: 'user1',
            receiver: 'venue',
            content: 'Hello, this is a test message.',
            timestamp: '2023-10-01T12:00:00Z',
            visitType: 'ENTER'
        }
    }
};

const mockUserList = {
    data: {
        receiveUserList: ['user1', 'user2', 'user3']
    }
};

test.describe('EnterVenue Component', () => {
    test.beforeEach(async ({ page }) => {
        await page.goto('/');
    });

    test('renders initial form', async ({ page }) => {
        await expect(page.getByRole('heading', { name: /enter a venue/i })).toBeVisible();
        await expect(page.getByPlaceholder('User ID')).toBeVisible();
        await expect(page.getByPlaceholder('Venue ID')).toBeVisible();
        await expect(page.getByRole('button', { name: /enter venue/i })).toBeVisible();
    });

    test('fills input fields', async ({ page }) => {
        const user = page.getByPlaceholder('User ID');
        const venue = page.getByPlaceholder('Venue ID');
        await user.fill('alice');
        await venue.fill('mainhall');
        await expect(user).toHaveValue('alice');
        await expect(venue).toHaveValue('mainhall');
    });

    test('shows venue UI after entering', async ({ page }) => {
        await page.route('**/graphql', async route => {
            const postData = route.request().postData();
            if (postData?.includes('userEnteredVenue')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { userEnteredVenue: true } }) });
            } else if (postData?.includes('receiveMessages')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(mockMessages) });
            } else if (postData?.includes('receiveUserList')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(mockUserList) });
            }
        });
        await page.getByPlaceholder('User ID').fill('alice');
        await page.getByPlaceholder('Venue ID').fill('mainhall');
        await page.getByRole('button', { name: /enter venue/i }).click();
        await expect(page.getByText(/live messages:/i)).toBeVisible();
        await expect(page.getByText(/current users:/i)).toBeVisible();
        await expect(page.getByRole('button', { name: /leave venue/i })).toBeVisible();
    });

    test('displays messages when received', async ({ page }) => {
        await page.route('**/graphql', async route => {
            await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(mockMessages) });
        });
        await page.getByPlaceholder('User ID').fill('alice');
        await page.getByPlaceholder('Venue ID').fill('mainhall');
        await page.getByRole('button', { name: /enter venue/i }).click();
        await expect(page.getByText('user1')).toBeVisible();
        await expect(page.getByText('Hello, this is a test message.')).toBeVisible();
    });

    test('displays user list', async ({ page }) => {
        await page.route('**/graphql', async route => {
            const postData = route.request().postData();
            if (postData?.includes('userEnteredVenue')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { userEnteredVenue: true } }) });
            } else if (postData?.includes('receiveMessages')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { receiveMessages: null } }) });
            } else if (postData?.includes('receiveUserList')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(mockUserList) });
            }
        });
        await page.getByPlaceholder('User ID').fill('alice');
        await page.getByPlaceholder('Venue ID').fill('mainhall');
        await page.getByRole('button', { name: /enter venue/i }).click();
        await expect(page.getByText('user1')).toBeVisible();
        await expect(page.getByText('user2')).toBeVisible();
        await expect(page.getByText('user3')).toBeVisible();
    });

    test('leaves venue and resets UI', async ({ page }) => {
        await page.route('**/graphql', async route => {
            const postData = route.request().postData();
            if (postData?.includes('userEnteredVenue')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { userEnteredVenue: true } }) });
            } else if (postData?.includes('userLeftVenue')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { userLeftVenue: true } }) });
            }
        });
        await page.getByPlaceholder('User ID').fill('alice');
        await page.getByPlaceholder('Venue ID').fill('mainhall');
        await page.getByRole('button', { name: /enter venue/i }).click();
        await page.getByRole('button', { name: /leave venue/i }).click();
        await expect(page.getByRole('button', { name: /enter venue/i })).toBeVisible();
        await expect(page.getByText(/live messages:/i)).not.toBeVisible();
    });

    test('disables Enter Venue button until both fields filled', async ({ page }) => {
        const btn = page.getByRole('button', { name: /enter venue/i });
        await expect(btn).toBeDisabled();
        await page.getByPlaceholder('User ID').fill('alice');
        await expect(btn).toBeDisabled();
        await page.getByPlaceholder('Venue ID').fill('mainhall');
        await expect(btn).toBeEnabled();
    });

    test('shows validation errors for empty fields', async ({ page }) => {
        await page.getByRole('button', { name: /enter venue/i }).click();
        await expect(page.getByText(/user id is required/i)).toBeVisible();
        await expect(page.getByText(/venue id is required/i)).toBeVisible();
    });

    test('does not allow whitespace-only input', async ({ page }) => {
        await page.getByPlaceholder('User ID').fill('   ');
        await page.getByPlaceholder('Venue ID').fill('   ');
        await page.getByRole('button', { name: /enter venue/i }).click();
        await expect(page.getByText(/live messages:/i)).not.toBeVisible();
    });

    test('clears input fields after leaving', async ({ page }) => {
        await page.route('**/graphql', async route => {
            const postData = route.request().postData();
            if (postData?.includes('userEnteredVenue')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { userEnteredVenue: true } }) });
            } else if (postData?.includes('userLeftVenue')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { userLeftVenue: true } }) });
            }
        });
        await page.getByPlaceholder('User ID').fill('alice');
        await page.getByPlaceholder('Venue ID').fill('mainhall');
        await page.getByRole('button', { name: /enter venue/i }).click();
        await page.getByRole('button', { name: /leave venue/i }).click();
        await expect(page.getByPlaceholder('User ID')).toHaveValue('');
        await expect(page.getByPlaceholder('Venue ID')).toHaveValue('');
    });

    test('focuses User ID input on load', async ({ page }) => {
        await expect(page.getByPlaceholder('User ID')).toBeFocused();
    });

    test('does not show Leave Venue button before entering', async ({ page }) => {
        await expect(page.getByRole('button', { name: /leave venue/i })).not.toBeVisible();
    });

    test('shows "No users present" if user list empty', async ({ page }) => {
        await page.route('**/graphql', async route => {
            const postData = route.request().postData();
            if (postData?.includes('userEnteredVenue')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { userEnteredVenue: true } }) });
            } else if (postData?.includes('receiveUserList')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { receiveUserList: [] } }) });
            }
        });
        await page.getByPlaceholder('User ID').fill('alice');
        await page.getByPlaceholder('Venue ID').fill('mainhall');
        await page.getByRole('button', { name: /enter venue/i }).click();
        await expect(page.getByText(/current users:/i)).toBeVisible();
        await expect(page.getByText(/no users present/i)).toBeVisible();
    });

    test('shows loading indicator when entering venue', async ({ page }) => {
        await page.route('**/graphql', async route => {
            const postData = route.request().postData();
            if (postData?.includes('userEnteredVenue')) {
                await new Promise(res => setTimeout(res, 500));
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { userEnteredVenue: true } }) });
            }
        });
        await page.getByPlaceholder('User ID').fill('alice');
        await page.getByPlaceholder('Venue ID').fill('mainhall');
        await page.getByRole('button', { name: /enter venue/i }).click();
        await expect(page.getByRole('status', { name: /loading/i })).toBeVisible();
    });

    test('disables message input and send button before entering', async ({ page }) => {
        const msgInput = page.getByRole('textbox', { name: /message/i });
        const sendBtn = page.getByRole('button', { name: /send/i });
        await expect(msgInput).toBeDisabled();
        await expect(sendBtn).toBeDisabled();
    });

    test('allows sending message after entering', async ({ page }) => {
        await page.route('**/graphql', async route => {
            const postData = route.request().postData();
            if (postData?.includes('userEnteredVenue')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { userEnteredVenue: true } }) });
            } else if (postData?.includes('sendMessage')) {
                await route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify({ data: { sendMessage: true } }) });
            }
        });
        await page.getByPlaceholder('User ID').fill('alice');
        await page.getByPlaceholder('Venue ID').fill('mainhall');
        await page.getByRole('button', { name: /enter venue/i }).click();
        const msgInput = page.getByRole('textbox', { name: /message/i });
        const sendBtn = page.getByRole('button', { name: /send/i });
        await msgInput.fill('Hello!');
        await sendBtn.click();
        await expect(msgInput).toHaveValue('');
    });

    test('handles network errors gracefully', async ({ page }) => {
        await page.route('**/graphql', async route => {
            await route.abort('failed');
        });
        await page.getByPlaceholder('User ID').fill('alice');
        await page.getByPlaceholder('Venue ID').fill('mainhall');
        await page.getByRole('button', { name: /enter venue/i }).click();
        // Optionally check for error UI if implemented
    });
});