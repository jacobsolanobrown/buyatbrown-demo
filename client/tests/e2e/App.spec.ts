import {
    clerkSetup,
    setupClerkTestingToken,
    clerk,
  } from "@clerk/testing/playwright";
  import { test as setup } from "@playwright/test";
  import { expect, test } from "@playwright/test";
  
  const url = "http://localhost:8000/";

  test.beforeAll(async () => {
    await clerkSetup({
      publishableKey: process.env.VITE_CLERK_PUBLISHABLE_KEY,
      frontendApiUrl: "http://localhost:8000",
    });
  });
  
  test.beforeEach(async ({ page }) => {
    await setupClerkTestingToken({ page });
    await page.goto(url);
    await clerk.loaded({ page });

    await clerk.signIn({
      page,
      signInParams: {
        strategy: "password",
        password: process.env.E2E_CLERK_USER2_PASSWORD!,
        identifier: process.env.E2E_CLERK_USER2_USERNAME!,
      },
    });

    // Ensure the user is signed in
    await expect(page).toHaveURL(url);
  });


// ******************* TEST SEARCH ******************

// Exact Match: 
test('Test search for item that exist -- test exact match with title', async ({ page }) => {
  // await page.goto('http://localhost:8000/');


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
  // await page.goto('http://localhost:8000/search-results');
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
  // await page.goto('http://localhost:8000/');
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

// Test furniture page:

test('test titles + conditions + listings in furniture page have correct tags', async ({ page }) => {
  // await page.goto('http://localhost:8000/');
  await expect(page.getByRole('heading', { name: 'Home' })).toBeVisible();
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

// Test navigating to multiple subpages in a row: 


// Test filters (multiple tag filters + condition filters, results + no results)
test('test filters on furniture page', async ({ page }) => {
  // await page.goto('http://localhost:8000/');
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
  // await page.goto('http://localhost:8000/');
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
  // await page.goto('http://localhost:8000/');
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

test('test form inputs', async ({ page }) => {
  // await page.goto('http://localhost:8000/');
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

// ********************* TEST ADD POST ***********************
test('test adding a post adds it to home page + your listings', async ({ page }) => {
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








  

