import {
    clerkSetup,
    setupClerkTestingToken,
    clerk,
  } from "@clerk/testing/playwright";
  import { test as setup } from "@playwright/test";
  import { expect, test } from "@playwright/test";
  
  setup("global setup", async ({}) => {
    await clerkSetup();
  });


// ******************* TEST SEARCH ******************

// Exact Match: 
test('Test search for item that exist -- test exact match with title', async ({ page }) => {
  await page.goto('http://localhost:8000/');
  await page.getByPlaceholder('Search for items...').click();
  await page.getByPlaceholder('Search for items...').fill('beyonce tickets');
  await page.getByRole('button', { name: 'search' }).click();
  await expect(page.getByRole('heading', { name: 'Search Results for: beyonce' })).toBeVisible();
  await expect(page.getByLabel('post details')).toBeVisible();
  await expect(page.getByRole('img', { name: 'Beyonce Tickets' })).toBeVisible();
  await expect(page.getByLabel('post details').getByRole('heading')).toContainText('Beyonce Tickets');
  await expect(page.getByLabel('post details')).toContainText('$500');
  await page.getByRole('img', { name: 'Beyonce Tickets' }).click();
});

// Search when item does not exist 
test('test search when no listings match term', async ({ page }) => {
  await page.goto('http://localhost:8000/search-results');
  await page.getByPlaceholder('Search for items...').click();
  await page.getByPlaceholder('Search for items...').fill('laptop');
  await page.getByPlaceholder('Search for items...').press('Enter');
  await page.getByRole('button', { name: 'search' }).click();
  await page.getByLabel('loading').click();
  await expect(page.getByText('No listings matched search')).toBeVisible();
  await expect(page.getByRole('paragraph')).toContainText('No listings matched search term: laptop');
});

// Search when listing title contains search term: 
test('Test search for item that exist -- test search contains', async ({ page }) => {
  await page.goto('http://localhost:8000/');
  // Search for "beanbag" -- contains
  await page.getByPlaceholder('Search for items...').click();
  await page.getByPlaceholder('Search for items...').fill('beanbag');
  await page.getByRole('button', { name: 'search' }).click();
  // Navigate to new page with the search results for "beanbag"
  await page.goto('http://localhost:8000/search-results');

  // await expect(page.locator('#root')).toContainText('Search Results for: beanbag');

  // Bar on the left to contain message indicating what one searched for + conditions
  await expect(page.getByRole('heading', { name: 'Search Results for: beanbag' })).toBeVisible();
  await expect(page.getByRole('heading', { name: 'Condition Filters' })).toBeVisible();
  await expect(page.getByText('New', { exact: true })).toBeVisible();
  await expect(page.getByText('Like New')).toBeVisible();
  await expect(page.getByText('Used')).toBeVisible();

  // check that card matching search displays:
  await expect(page.getByLabel('post details')).toBeVisible();
  await expect(page.getByRole('img', { name: 'Pink Beanbag Chair' })).toBeVisible();
  await expect(page.getByLabel('post details')).toBeVisible();
  await expect(page.getByLabel('post details').getByRole('heading')).toContainText('Pink Beanbag Chair');
  await expect(page.getByLabel('post details')).toContainText('$17.00');
  await expect(page.getByLabel('results')).toBeVisible();

  // Test filter within search results page

  // condition matches: 
  await page.getByText('New', { exact: true }).click();
  await expect(page.getByRole('img', { name: 'Pink Beanbag Chair' })).toBeVisible();

  // condition doesn't match - Test no listings matched message
  await page.getByText('Like New').click();
  await expect(page.getByText('No listings matched search')).toBeVisible();
});


// Test subsequent searches: 
test('test subsequent successful and no listing searches', async ({ page }) => {
  await page.goto('http://localhost:8000/search-results');
  // await page.getByRole('link', { name: 'BUY@BROWN' }).click();

  // Search # 1 -- item that exist: 
  await page.getByPlaceholder('Search for items...').click();
  await page.getByPlaceholder('Search for items...').fill('blue shirt');
  await page.getByRole('button', { name: 'search' }).click();
  await expect(page.getByRole('heading', { name: 'Search Results for: blue shirt' })).toBeVisible();
  await expect(page.getByRole('img', { name: 'Blue Shirt' })).toBeVisible();
  await expect(page.getByLabel('post details')).toBeVisible();
  await expect(page.getByLabel('post details')).toContainText('$5');
  // Search #2 -- item that exist:
  await page.getByPlaceholder('Search for items...').click();
  await page.getByPlaceholder('Search for items...').fill('beyonce tickets');
  await page.getByRole('button', { name: 'search' }).click();
  await expect(page.getByRole('heading', { name: 'Search Results for: beyonce' })).toBeVisible();
  await expect(page.getByRole('img', { name: 'Beyonce Tickets' })).toBeVisible();
  // Search #3 -- item that does not exist:
  await page.getByPlaceholder('Search for items...').click();
  await page.getByPlaceholder('Search for items...').fill('laptop');
  await page.getByPlaceholder('Search for items...').press('Enter');
  await page.getByRole('button', { name: 'search' }).click();
  await page.getByLabel('loading').click();
  await expect(page.getByRole('paragraph')).toContainText('No listings matched search term: laptop');
  await expect(page.locator('h2')).toContainText('Search Results for: laptop');
});

// ****************** TEST FILTER *******************

// Test clothes page displays just clothes:


// Test tech page displays just tech: 

// Test Filtering by one tag when listing exist: 

// Test Filtering by one tag when listing does not exist: 


// Test Filtering by condition:


// Test filtering by multiple tags and conditions:


// Test filtering then unfiltering: 

// 






  

