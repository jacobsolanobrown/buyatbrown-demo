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


// ****************** TEST FILTER *******************

test.describe("Test Filter", () => {
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
      await expect(page.getByRole('heading', { name: 'Home' })).toBeVisible();
    });

    test('test titles + conditions + listings in furniture page have correct tags', async ({ page }) => {
        await page.goto(url, { timeout: 60000 });  // 60 seconds
        // await expect(page.getByRole('heading', { name: 'Home' })).toBeVisible();
        await page.waitForSelector('h2');  // Wait for any <h1> to appear, or use a more specific selector
        await expect(page.getByRole('heading', { name: 'Home' })).toBeVisible({ timeout: 40000 });
        // go to furniture subpage:
        await page.getByRole('link', { name: 'Furniture' }).click();
        // check correct title and conditions are present:
        await expect(page.getByRole('heading', { name: 'Furniture' })).toBeVisible();
        await expect(page.getByText('Bedding')).toBeVisible();
        await expect(page.getByText('Beds & Mattresses')).toBeVisible();
        await expect(page.getByText('Tables')).toBeVisible();
        // check that listings on page are furniture:
        await expect(page.getByRole('img', { name: 'Blanket' })).toBeVisible();
        await expect(page.getByRole('img', { name: 'white fluffy rug' })).toBeVisible();
        await expect(page.getByRole('img', { name: 'twin xl mattress topper' })).toBeVisible();
        await expect(page.locator('#root')).toContainText('Blanket');
        await expect(page.locator('#root')).toContainText('white fluffy rug');
        await expect(page.locator('#root')).toContainText('twin xl mattress topper');
        // check that tags on listings are furniture:
        // post #1: 
        await page.getByRole('img', { name: 'twin xl mattress topper' }).click();
        await expect(page.getByRole('dialog')).toContainText('Tags: Furniture, Beds & Mattresses');
        await page.getByLabel('Close modal').click();
        // post #2:
        await page.getByRole('img', { name: 'white fluffy rug' }).click();
        await expect(page.getByRole('dialog')).toContainText('Tags: Furniture, Flooring & Rugs');
        await page.getByLabel('Close modal').click();
      
        // filter by tag "Flooring & Rugs"
        await page.getByText('Flooring & Rugs').click();
        await expect(page.getByLabel('post details').getByRole('heading')).toContainText('white fluffy rug');
        await page.getByRole('img', { name: 'white fluffy rug' }).click();
        await expect(page.getByRole('dialog')).toContainText('Tags: Furniture, Flooring & Rugs');
        await page.getByLabel('Close modal').click();
        await expect(page.locator('div').filter({ hasText: /^white fluffy rug\$20by kqueen038$/ }).first()).toBeVisible();
        // remove filter
        await page.getByText('Flooring & Rugs').click();
        // filter by tag "Beds & Mattresses"
        await page.getByText('Beds & Mattresses').click();
        await page.getByRole('img', { name: 'twin xl mattress topper' }).click();
        await expect(page.getByRole('dialog')).toContainText('twin xl mattress topper');
        await expect(page.getByRole('dialog')).toContainText('Tags: Furniture, Beds & Mattresses');
        await page.getByLabel('Close modal').click();
        await expect(page.locator('div').filter({ hasText: /^twin xl mattress topper\$40by kqueen038$/ }).first()).toBeVisible();
      
        // filter by both tag and condition that does not match: 
        await page.getByText('New', { exact: true }).click();
        await expect(page.getByRole('paragraph')).toContainText('No furniture listings available with filters: Beds & Mattresses, New');
        await page.getByText('New', { exact: true }).click(); // unclick new
        // filter by both tag and condition that does match:
        await page.getByText('Used').click();
        await expect(page.getByLabel('post details').getByRole('heading')).toContainText('twin xl mattress topper');
      });
      
      
      // Test filters (multiple tag filters + condition filters, results + no results)
      test('test filters on furniture page', async ({ page }) => {
        await page.goto(url, { timeout: 60000 });  // 60 seconds
        await expect(page.getByRole('heading', { name: 'Home' })).toBeVisible();
        // go to furniture subpage:
        await page.getByRole('link', { name: 'Furniture' }).click();
        // check that listings on page::
        await expect(page.getByRole('img', { name: 'Blanket' })).toBeVisible();
        await expect(page.getByRole('img', { name: 'white fluffy rug' })).toBeVisible();
        await expect(page.getByRole('img', { name: 'twin xl mattress topper' })).toBeVisible();
        await expect(page.locator('#root')).toContainText('Blanket');
        await expect(page.locator('#root')).toContainText('white fluffy rug');
        await expect(page.locator('#root')).toContainText('twin xl mattress topper');
      
        // check tags on listings:
        // post #1: 
        await page.getByRole('img', { name: 'twin xl mattress topper' }).click();
        await expect(page.getByRole('dialog')).toContainText('Tags: Furniture, Beds & Mattresses');
        await page.getByLabel('Close modal').click();
        // post #2:
        await page.getByRole('img', { name: 'white fluffy rug' }).click();
        await expect(page.getByRole('dialog')).toContainText('Tags: Furniture, Flooring & Rugs');
        await page.getByLabel('Close modal').click();
      
      
        // filter by tag "Flooring & Rugs"
        await page.getByText('Flooring & Rugs').click();
        await expect(page.getByLabel('post details').getByRole('heading')).toContainText('white fluffy rug');
        await page.getByRole('img', { name: 'white fluffy rug' }).click();
        await expect(page.getByRole('dialog')).toContainText('Tags: Furniture, Flooring & Rugs');
        await page.getByLabel('Close modal').click();
        await expect(page.locator('div').filter({ hasText: /^white fluffy rug\$20by kqueen038$/ }).first()).toBeVisible();
        // remove filter
        await page.getByText('Flooring & Rugs').click();
        // filter by tag "Beds & Mattresses"
        await page.getByText('Beds & Mattresses').click();
        await page.getByRole('img', { name: 'twin xl mattress topper' }).click();
        await expect(page.getByRole('dialog')).toContainText('twin xl mattress topper');
        await expect(page.getByRole('dialog')).toContainText('Tags: Furniture, Beds & Mattresses');
        await page.getByLabel('Close modal').click();
        await expect(page.locator('div').filter({ hasText: /^twin xl mattress topper\$40by kqueen038$/ }).first()).toBeVisible();
      
        // filter by both tag and condition that does not match: 
        await page.getByText('New', { exact: true }).click();
        await expect(page.getByRole('paragraph')).toContainText('No furniture listings available with filters: Beds & Mattresses, New');
        await page.getByText('New', { exact: true }).click();
      
        // filter by both tag and condition that does match:
        await page.getByText('Used').click();
        await expect(page.getByLabel('post details').getByRole('heading')).toContainText('twin xl mattress topper');
      });
      
      // Test clothes subpage has clothes listings cards and correct filters
      test('test clothes subpage has correct titles, filters, and cards', async ({ page }) => {
        await page.goto(url, { timeout: 60000 });  // 60 seconds
        // Navigate to clothes page and check filters:
        await page.getByRole('link', { name: 'Clothes' }).click();
        await expect(page.getByRole('heading', { name: 'Clothes' })).toBeVisible();
        await expect(page.getByLabel('post listing')).toBeVisible();
        await expect(page.getByText('Outerwear')).toBeVisible();
        await expect(page.getByText('Tops')).toBeVisible();
        await expect(page.getByText('Pants')).toBeVisible();
        await expect(page.getByText('Dresses & Skirts')).toBeVisible();
        // Check that expected listing cards appear:
        await expect(page.locator('#root')).toContainText('Black Platform Chuck Taylors');
        await expect(page.locator('#root')).toContainText('Aritzia Super-puffer');
        await expect(page.locator('#root')).toContainText('Blue Shirt');
        await expect(page.getByRole('img', { name: 'Black Platform Chuck Taylors' })).toBeVisible();
        await expect(page.getByRole('img', { name: 'Aritzia Super-puffer' })).toBeVisible();
        await expect(page.getByRole('img', { name: 'Blue Shirt' })).toBeVisible();
        // Check that listings cards on page have category clothes:
        await page.getByRole('img', { name: 'Black Platform Chuck Taylors' }).click();
        await expect(page.getByRole('dialog')).toContainText('Tags: Clothes, Shoes');
        await page.getByLabel('Close modal').click();
        await page.getByRole('img', { name: 'Aritzia Super-puffer' }).click();
        await expect(page.getByRole('dialog')).toContainText('Tags: Clothes, Outerwear');
        await page.getByLabel('Close modal').click();
        await page.getByRole('img', { name: 'Blue Shirt' }).click();
        await expect(page.getByRole('dialog')).toContainText('Tags: Clothes, Shirt');
        await page.getByLabel('Close modal').click();
        // Filter by tag "Outerwear"
        await page.getByText('Outerwear').click();
        await page.locator('div').filter({ hasText: /^Aritzia Super-puffer\$89\.99by cprincess$/ }).first().click();
        await expect(page.getByLabel('post details').getByRole('heading')).toContainText('Aritzia Super-puffer');
        // Filter by condition + tag "Outerwear & Used":
        await page.getByText('Used').click();
        await expect(page.getByRole('paragraph')).toContainText('No clothing listings available with filters: Outerwear, Used');
      
      });
      
      // Test navigating to different subpages/tabs 
      test('test each subpage has correct title and filters', async ({ page }) => {
        await page.goto(url, { timeout: 60000 });  // 60 seconds
        // Navigate to Clothes and check filters:
        await page.getByRole('link', { name: 'Clothes' }).click();
        await expect(page.getByRole('heading', { name: 'Clothes' })).toBeVisible();
        await expect(page.getByLabel('post listing')).toBeVisible();
        await expect(page.getByText('Outerwear')).toBeVisible();
        await expect(page.getByText('Tops')).toBeVisible();
        await expect(page.getByText('Pants')).toBeVisible();
        await expect(page.getByText('Dresses & Skirts')).toBeVisible();
        // Navigate to Tech and check filters:
        await page.getByRole('link', { name: 'Tech' }).click();
        await expect(page.getByRole('heading', { name: 'Tech' })).toBeVisible();
        await expect(page.getByText('Computers')).toBeVisible();
        await expect(page.getByText('Televisions')).toBeVisible();
        await expect(page.getByText('Tablets')).toBeVisible();
        // Check that the tech page has correct card:
        await page.locator('div').filter({ hasText: /^Black Headphones\$15\.99by kqueen038$/ }).first().click();
        await page.getByRole('img', { name: 'Black Headphones' }).click();
        await expect(page.getByRole('dialog')).toContainText('Black Headphones');
        await expect(page.getByRole('dialog')).toContainText('Tags: Tech, Wearables');
        await page.getByLabel('Close modal').click();
        // Filter by tag "wearables"
        await page.getByText('Wearables').click();
        await expect(page.getByLabel('post details').getByRole('heading')).toContainText('Black Headphones');
        // Navigate to School and check filters:
        await page.getByRole('link', { name: 'School' }).click();
        await expect(page.getByRole('heading', { name: 'School' })).toBeVisible();
        await expect(page.getByText('Stationary')).toBeVisible();
        await expect(page.getByText('Books', { exact: true })).toBeVisible();
        // Navigate to Furniture and check filters:
        await page.getByRole('link', { name: 'Furniture' }).click();
        await expect(page.getByRole('heading', { name: 'Furniture' })).toBeVisible();
        await expect(page.getByText('Bedding')).toBeVisible();
        await expect(page.getByText('Dressers & Storage')).toBeVisible();
        // Navigate to Kitchen and check filters:
        await page.getByRole('link', { name: 'Kitchen' }).click();
        await expect(page.getByRole('heading', { name: 'Kitchen' })).toBeVisible();
        await expect(page.getByText('Appliances')).toBeVisible();
        await expect(page.getByText('Kitchenware')).toBeVisible();
        // Navigate to Bathroom and check filters:
        await page.getByRole('link', { name: 'Bathroom' }).click();
        await expect(page.getByRole('heading', { name: 'Bathroom' })).toBeVisible();
        await expect(page.getByText('Bath Supplies')).toBeVisible();
        await expect(page.getByText('Textiles')).toBeVisible();
        await expect(page.getByRole('paragraph')).toContainText('No bathroom listings available.');
        // Navigate to Misc and check filters:
        await page.getByRole('link', { name: 'Misc' }).click();
        await expect(page.getByRole('heading', { name: 'Misc' })).toBeVisible();
        await expect(page.getByText('New', { exact: true })).toBeVisible();
      });

});



