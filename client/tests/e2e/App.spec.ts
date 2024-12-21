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
  await page.getByPlaceholder('Search for items...').fill('Beyonce tickets');
  await page.locator('form').getByRole('button').click();
//   await expect(page.getByRole('heading', { name: 'Search Results for: Beyonce' })).toBeVisible();
  await expect(page.locator('#root')).toContainText('Search Results for: Beyonce tickets');
  await expect(page.getByRole('img', { name: 'Beyonce Tickets' })).toBeVisible();
  await expect(page.getByRole('heading', { name: 'Beyonce Tickets', exact: true })).toBeVisible();
  await page.getByText('Beyonce Tickets$500by jaykoyuk').click();
});

// Search when listing title contains search term: 


// Search when item does not exist 


// ****************** TEST FILTER *******************

// Test clothes page displays just clothes:

// Test tech page displays just tech: 

// Test Filtering by one tag when listing exist: 

// Test Filtering by one tag when listing does not exist: 


// Test Filtering by condition:


// Test filtering by multiple tags and conditions:


// Test filtering then unfiltering: 

// 






  

