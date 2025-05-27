import { test, expect } from '@playwright/test';

test('test', async ({ page }) => {
  await page.goto('http://localhost:3000/');
  await page.getByRole('textbox', { name: 'User ID' }).click();
  await page.getByRole('textbox', { name: 'User ID' }).fill('user1');
  await page.getByRole('textbox', { name: 'Venue ID' }).click();
  await page.getByRole('textbox', { name: 'Venue ID' }).fill('venue');
  await page.getByRole('button', { name: 'Enter Venue' }).click();
  await expect(page.getByText('user1')).toBeVisible();
  await expect(page.getByText('venue')).toBeVisible();
  await expect(page.getByRole('button', { name: 'Leave Venue' })).toBeVisible();
  await page.getByRole('button', { name: 'Leave Venue' }).click();
  await expect(page.getByRole('button', { name: 'Enter Venue' })).toBeVisible();
  await expect(page.getByRole('textbox', { name: 'User ID' })).toBeVisible();
  await expect(page.getByRole('textbox', { name: 'Venue ID' })).toBeVisible();
  await expect(page.getByRole('button', { name: 'Enter Venue' })).toBeEnabled();
  
});