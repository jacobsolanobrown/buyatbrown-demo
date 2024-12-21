// import { setupClerkTestingToken } from "@clerk/testing/playwright";
// import { test, expect } from "@playwright/test";
// import { clerkSetup } from "@clerk/testing/playwright";
// import { clerk } from "@clerk/testing/playwright";

// const url = "http://localhost:8000/";

// test.beforeAll(async () => {
//   await clerkSetup({
//     publishableKey: process.env.VITE_CLERK_PUBLISHABLE_KEY,
//     frontendApiUrl: "http://localhost:8000",
//   });
// });

// test.describe("Modal Card Tests with Clerk User2", () => {
//   test.beforeEach(async ({ page }) => {
//     await setupClerkTestingToken({ page });
//     await page.goto(url);
//     await clerk.loaded({ page });

//     await clerk.signIn({
//       page,
//       signInParams: {
//         strategy: "password",
//         password: process.env.E2E_CLERK_USER2_PASSWORD!,
//         identifier: process.env.E2E_CLERK_USER2_USERNAME!,
//       },
//     });

//     // Ensure the user is signed in
//     await expect(page).toHaveURL(url);
//   });

//   test("Verify Modal Card Opens", async ({ page }) => {
//     // Navigate to the page containing the modal card
//     await page.goto(url);

//     // Click on a listing card to open the modal
//     await page.getByRole("button", { name: "Open Listing Modal" }).click();

//     // Verify that the modal card is visible
//     await expect(page.getByRole("dialog")).toBeVisible();
//   });

//   test("Verify Modal Card Displays Correct Information", async ({ page }) => {
//     // Navigate to the page containing the modal card
//     await page.goto(url);

//     // Click on a listing card to open the modal
//     await page.getByRole("button", { name: "Open Listing Modal" }).click();

//     // Verify that the modal card displays the correct information
//     await expect(
//       page.getByRole("heading", { name: "Listing Title" })
//     ).toBeVisible();
//     await expect(page.getByText("Listing Description")).toBeVisible();
//     await expect(page.getByText("$100")).toBeVisible();
//   });

//   test("Verify Modal Card Close Button", async ({ page }) => {
//     // Navigate to the page containing the modal card
//     await page.goto(url);

//     // Click on a listing card to open the modal
//     await page.getByRole("button", { name: "Open Listing Modal" }).click();

//     // Click the close button
//     await page.getByRole("button", { name: "Close modal" }).click();

//     // Verify that the modal card is not visible
//     await expect(page.getByRole("dialog")).not.toBeVisible();
//   });

//   test("Verify Modal Card Outside Click", async ({ page }) => {
//     // Navigate to the page containing the modal card
//     await page.goto(url);

//     // Click on a listing card to open the modal
//     await page.getByRole("button", { name: "Open Listing Modal" }).click();

//     // Click outside the modal
//     await page.mouse.click(0, 0);

//     // Verify that the modal card is not visible
//     await expect(page.getByRole("dialog")).not.toBeVisible();
//   });
// });
