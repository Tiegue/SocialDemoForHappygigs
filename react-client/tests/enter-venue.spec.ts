import {test, expect} from '@playwright/test';
import { send } from 'process';

//Mock GraphQL responses for testing
const mockMessages = {
    data: {
        receiveMmessages: {
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

test('test: homepage has title', async ({page}) => {
    await page.goto('http://localhost:3000');
    await expect(page).toHaveTitle("SocialDemo");
    await expect(page.locator('h1')).toHaveText('HappyGigs Social');
});

test.describe('EnterVenue Component', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to your app (adjust URL as needed)
    await page.goto('http://localhost:3000');
  });

  test('should render initial form correctly', async ({ page }) => {
    // Check if the main heading is present
    await expect(page.getByRole('heading', { name: 'Enter a Venue' })).toBeVisible();
    
    // Check if input fields are present
    await expect(page.getByPlaceholder('User ID')).toBeVisible();
    await expect(page.getByPlaceholder('Venue ID')).toBeVisible();
    
    // Check if Enter Venue button is present
    await expect(page.getByRole('button', { name: 'Enter Venue' })).toBeVisible();
  });

  test('should fill input fields correctly', async ({ page }) => {
    const userIdInput = page.getByPlaceholder('User ID');
    const venueIdInput = page.getByPlaceholder('Venue ID');
    
    // Fill the input fields
    await userIdInput.fill('testuser123');
    await venueIdInput.fill('testvenue456');
    
    // Verify the values are set correctly
    await expect(userIdInput).toHaveValue('testuser123');
    await expect(venueIdInput).toHaveValue('testvenue456');
  });

  test('should show venue interface after entering venue', async ({ page }) => {
    // Mock GraphQL requests (you'll need to set up MSW or similar for real API mocking)
    await page.route('**/graphql', async route => {
      const request = route.request();
      const postData = request.postData();
      
      if (postData?.includes('userEnteredVenue')) {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ data: { userEnteredVenue: true } })
        });
      } else if (postData?.includes('receiveMessages')) {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(mockMessages)
        });
      } else if (postData?.includes('receiveUserList')) {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify(mockUserList)
        });
      }
    });

    // Fill in the form
    await page.getByPlaceholder('User ID').fill('testuser123');
    await page.getByPlaceholder('Venue ID').fill('testvenue456');
    
    // Click Enter Venue button
    await page.getByRole('button', { name: 'Enter Venue' }).click();
    
    // Wait for the venue interface to appear
    await expect(page.getByText('Live Messages:')).toBeVisible();
    await expect(page.getByText('Current Users:')).toBeVisible();
    await expect(page.getByRole('button', { name: 'Leave Venue' })).toBeVisible();
  });

  test('should display messages when received', async ({ page }) => {
    // Mock the GraphQL subscription response
    await page.route('**/graphql', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(mockMessages)
      });
    });

    // Enter venue first
    await page.getByPlaceholder('User ID').fill('testuser123');
    await page.getByPlaceholder('Venue ID').fill('testvenue456');
    await page.getByRole('button', { name: 'Enter Venue' }).click();
    
    // Check if message content is displayed
    await expect(page.getByText('John:')).toBeVisible();
    await expect(page.getByText('Hello everyone!')).toBeVisible();
  });

  test('should display user list', async ({ page }) => {
    // Mock user list response
    await page.route('**/graphql', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify(mockUserList)
      });
    });

    // Enter venue
    await page.getByPlaceholder('User ID').fill('testuser123');
    await page.getByPlaceholder('Venue ID').fill('testvenue456');
    await page.getByRole('button', { name: 'Enter Venue' }).click();
    
    // Check if users are listed
    await expect(page.getByText('user1')).toBeVisible();
    await expect(page.getByText('user2')).toBeVisible();
    await expect(page.getByText('user3')).toBeVisible();
  });

  test('should leave venue when Leave Venue button is clicked', async ({ page }) => {
    // Mock leave venue mutation
    await page.route('**/graphql', async route => {
      const request = route.request();
      const postData = request.postData();
      
      if (postData?.includes('userLeftVenue')) {
        await route.fulfill({
          status: 200,
          contentType: 'application/json',
          body: JSON.stringify({ data: { userLeftVenue: true } })
        });
      }
    });

    // Enter venue first
    await page.getByPlaceholder('User ID').fill('testuser123');
    await page.getByPlaceholder('Venue ID').fill('testvenue456');
    await page.getByRole('button', { name: 'Enter Venue' }).click();
    
    // Wait for venue interface to load
    await expect(page.getByRole('button', { name: 'Leave Venue' })).toBeVisible();
    
    // Click Leave Venue
    await page.getByRole('button', { name: 'Leave Venue' }).click();
    
    // Verify we're back to the initial state
    await expect(page.getByRole('button', { name: 'Enter Venue' })).toBeVisible();
    await expect(page.getByText('Live Messages:')).not.toBeVisible();
  });

  test('should handle empty input validation', async ({ page }) => {
    // Try to enter venue without filling inputs
    await page.getByRole('button', { name: 'Enter Venue' }).click();
    
    // The component should not show venue interface without proper inputs
    await expect(page.getByText('Live Messages:')).not.toBeVisible();
  });

  test('should show "No messages yet" when no messages are received', async ({ page }) => {
    // Mock empty message response
    await page.route('**/graphql', async route => {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ data: { receiveMessages: null } })
      });
    });

    // Enter venue
    await page.getByPlaceholder('User ID').fill('testuser123');
    await page.getByPlaceholder('Venue ID').fill('testvenue456');
    await page.getByRole('button', { name: 'Enter Venue' }).click();
    
    // Should show "No messages yet" text
    await expect(page.getByText('No messages yet.')).toBeVisible();
  });

  test('should test accessibility features', async ({ page }) => {
    // Check if form elements have proper labels/placeholders
    const userInput = page.getByPlaceholder('User ID');
    const venueInput = page.getByPlaceholder('Venue ID');
    
    await expect(userInput).toBeVisible();
    await expect(venueInput).toBeVisible();
    
    // Test keyboard navigation
    await userInput.focus();
    await expect(userInput).toBeFocused();
    
    await page.keyboard.press('Tab');
    await expect(venueInput).toBeFocused();
    
    await page.keyboard.press('Tab');
    await expect(page.getByRole('button', { name: 'Enter Venue' })).toBeFocused();
  });

  test('should handle network errors gracefully', async ({ page }) => {
    // Mock network error
    await page.route('**/graphql', async route => {
      await route.abort('failed');
    });

    await page.getByPlaceholder('User ID').fill('testuser123');
    await page.getByPlaceholder('Venue ID').fill('testvenue456');
    await page.getByRole('button', { name: 'Enter Venue' }).click();
    
    // Component should handle error gracefully (check console or error states)
    // You might want to add error handling UI to your component
  });
});



