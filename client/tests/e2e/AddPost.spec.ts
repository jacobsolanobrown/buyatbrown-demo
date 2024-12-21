import { setupClerkTestingToken } from "@clerk/testing/playwright";
import { test, expect } from "@playwright/test";
import { clerkSetup } from "@clerk/testing/playwright";
import { clerk } from "@clerk/testing/playwright";
  
  const url = "http://localhost:8000/";

  test.beforeAll(async () => {
    await clerkSetup({
      publishableKey: process.env.VITE_CLERK_PUBLISHABLE_KEY,
      frontendApiUrl: "http://localhost:8000",
    });
  });

  test.describe("Test Add Post", () => {
    test.beforeEach(async ({ page }) => {
      await setupClerkTestingToken({ page });
      await page.goto(url);
      await clerk.loaded({ page });
  
      await clerk.signIn({
        page,
        signInParams: {
          strategy: "password",
          password: process.env.E2E_CLERK_USER3_PASSWORD!,
          identifier: process.env.E2E_CLERK_USER3_USERNAME!,
        },
      });
  
      // Ensure the user is signed in
      await expect(page).toHaveURL(url);
    });

    test('test form inputs', async ({ page }) => {
        await page.goto(url);
        await expect(page.getByRole('heading', { name: 'Home' })).toBeVisible();
        await expect(page.getByLabel('post listing')).toBeVisible();
        await page.getByLabel('post listing').click();
        // Check contents of the form:
        await expect(page.getByRole('heading')).toContainText('Post a New Listing');
        await expect(page.locator('#root')).toContainText('Listing Title');
        await expect(page.getByPlaceholder('Choose a listing title')).toBeVisible();
        await expect(page.locator('#root')).toContainText('Price');
        await expect(page.getByPlaceholder('Choose a price')).toBeVisible();
        await expect(page.locator('#root')).toContainText('Photo');
        await expect(page.getByPlaceholder('Upload an image (png or jpeg)')).toBeVisible();
        await expect(page.locator('#root')).toContainText('Description');
        await expect(page.getByPlaceholder('Describe your listing')).toBeVisible();
        // check that condition is a dropdown:
        await expect(page.getByLabel('Condition')).toBeVisible();
        await expect(page.locator('#root')).toContainText('Condition');
        await page.getByLabel('Condition').selectOption('New');
        // check that category is a dropdown:
        await expect(page.getByLabel('Category')).toBeVisible();
        await page.getByLabel('Category').selectOption('Furniture');
      
        await expect(page.getByPlaceholder('Add tags')).toBeVisible();
        await expect(page.locator('#root')).toContainText('Tags');
      
        await expect(page.getByRole('button', { name: 'Post Listing' })).toBeVisible();
      });
      
      test('test adding a post adds it to home page + your listings', async ({ page }) => {
        await page.goto(url);
        await page.goto('http://localhost:8000/listing-form');
        // Fill out form:
        await page.getByPlaceholder('Choose a listing title').click();
        await page.getByPlaceholder('Choose a listing title').fill('tv');
        await page.getByPlaceholder('Choose a price').click();
        await page.getByPlaceholder('Choose a price').fill('60');
        await page.getByPlaceholder('Upload an image (png or jpeg)').click();
        await page.getByPlaceholder('Upload an image (png or jpeg)').setInputFiles('tv.jpeg');
        await page.getByPlaceholder('Describe your listing').click();
        await page.getByPlaceholder('Describe your listing').fill('roku tv');
        await page.getByLabel('Condition').selectOption('Like New');
        await page.getByLabel('Category').selectOption('Tech');
        await page.getByPlaceholder('Add tags').click();
        await page.getByPlaceholder('Add tags').fill('televisions');
        // Post it:
        await page.getByRole('button', { name: 'Post Listing' }).click(); 
        // Check that listing card of tv appears on the home page:
        await expect(page.getByRole('heading', { name: 'Home' })).toBeVisible();
        await expect(page.getByRole('img', { name: 'tv' })).toBeVisible();
        await expect(page.getByText('tv$60by kqueen038')).toBeVisible();
        await expect(page.getByLabel('all listings')).toContainText('tv');
        await expect(page.getByLabel('all listings')).toContainText('$60');
        await expect(page.getByLabel('all listings')).toContainText('by kqueen038');
        // Check that the bigger on click card has correct info:
        await page.getByRole('img', { name: 'tv' }).click();
        await expect(page.getByLabel('Close modal')).toBeVisible();
        await expect(page.getByRole('dialog').getByRole('heading', { name: 'tv' })).toBeVisible();
        await expect(page.getByRole('heading', { name: 'like new' })).toBeVisible();
        await expect(page.getByRole('heading', { name: '$' })).toBeVisible();
        await expect(page.getByRole('dialog').getByRole('img', { name: 'tv' })).toBeVisible();
        await expect(page.getByRole('dialog')).toContainText('kqueen038:');
        await expect(page.getByRole('dialog')).toContainText('roku tv');
        await expect(page.getByRole('dialog')).toContainText('Tags: Tech, televisions');
      
        await expect(page.getByLabel('all listings').getByRole('button').nth(1)).toBeVisible();
        await expect(page.getByRole('button', { name: 'Message Seller: kqueen038' })).toBeVisible();
      
        await page.getByLabel('Close modal').click();
      
        // Check that listing card of tv appears in "Your Listings"
        await page.getByRole('link', { name: 'Account: kqueen038' }).click();
        await expect(page.getByRole('heading', { name: 'Your Listings' })).toBeVisible();
        await expect(page.locator('#root')).toContainText('Edit, or delete active/sold listings.');
        await expect(page.getByRole('img', { name: 'tv' })).toBeVisible();
        await expect(page.locator('#root')).toContainText('tv');
        await expect(page.locator('#root')).toContainText('$60');
        await expect(page.locator('#root')).toContainText('by kqueen038');
        // Check big card on "Your listings" has correct content
        await page.getByRole('img', { name: 'tv' }).click();
        await expect(page.getByRole('dialog')).toContainText('Tags: Tech, televisions');
        await expect(page.getByRole('button', { name: 'Edit Listing' })).toBeVisible();
        await expect(page.getByRole('button', { name: 'Delete Listing' })).toBeVisible();
        await expect(page.getByRole('dialog')).toContainText('tv');
        await page.getByLabel('Close modal').click();
      
        // Check card on "Your Listings" again
        await expect(page.getByRole('img', { name: 'tv' })).toBeVisible();
        await expect(page.getByRole('heading', { name: 'tv' })).toBeVisible();
        await expect(page.locator('#root')).toContainText('Your Listings');
      });

      test('test that all fields in the form are required', async ({ page }) => {
        await page.goto(url);
        await page.goto('http://localhost:8000/yourListings');
        await page.getByLabel('post listing').click();
        await page.getByPlaceholder('Choose a listing title').click();
        await page.getByPlaceholder('Choose a listing title').fill('black rug');
        await page.getByPlaceholder('Choose a price').click();
        await page.getByPlaceholder('Choose a price').fill('15');
        await page.getByPlaceholder('Upload an image (png or jpeg)').click();
        await page.getByPlaceholder('Upload an image (png or jpeg)').setInputFiles('white fluffy rug.jpeg');
        await page.getByPlaceholder('Describe your listing').click();
        // attempt to post listing without filling out all the fields:
        await page.getByRole('button', { name: 'Post Listing' }).click();
        // should still be on the form:
        await expect(page.getByText('Description')).toBeVisible();
        await page.getByPlaceholder('Describe your listing').click();
        await page.getByPlaceholder('Describe your listing').fill('fluffy');
        await page.getByLabel('Condition').selectOption('Like New');
        await page.getByLabel('Category').selectOption('Furniture');
        await page.getByPlaceholder('Add tags').click();
        // try posting again without filling out tags:
        await page.getByRole('button', { name: 'Post Listing' }).click();
        // should still be on the form:
        await page.getByLabel('Category').selectOption('Furniture');
      });
  });












