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

    // Click on a listing card to open the modal
    await page.getByText("Cool Shirt").click();

//     // Verify that the modal card is visible
//     await expect(page.getByRole("dialog")).toBeVisible();
//   });

//   test("Verify Modal Card Displays Correct Information", async ({ page }) => {
//     // Navigate to the page containing the modal card
//     await page.goto(url);

    // Click on a listing card to open the modal
    await page.getByText("Cool Shirt").click();

    // Verify that the modal card displays the correct information
    const dialogHeading = page
      .getByRole("dialog")
      .getByRole("heading", { name: "Cool Shirt" });
    await expect(dialogHeading).toBeVisible();

    await expect(
      page.getByText("This is a really cool shirt...")
    ).toBeVisible();
    await expect(page.getByRole("heading", { name: "$5" })).toBeVisible();
  });

  test("Verify Modal Card Close Button", async ({ page }) => {
    // Navigate to the page containing the modal card
    await page.goto(url);
    await page.getByText("Cool Shirt").click();
    await page.getByLabel("Close Modal").click();
    // Verify that the modal card is not visible
    await expect(page.getByRole("dialog")).not.toBeVisible();
  });

  test("Verify Modal Card Outside Click", async ({ page }) => {
    await page.goto(url);
    await page.getByText("Cool Shirt").click();
    await page.mouse.click(0, 0);
    await expect(page.getByRole("dialog")).not.toBeVisible();
  });

  test("Verify Modal Card Escape Key", async ({ page }) => {
    await page.goto(url);
    await page.getByText("Cool Shirt").click();
    await page.keyboard.press("Escape");
    await expect(page.getByRole("dialog")).not.toBeVisible();
  });

  test("Verify Favorite Button", async ({ page }) => {
    await page.goto(url);
    await page.getByText("Cool Shirt").click();
    await expect(page.getByLabel("Favorite")).toBeVisible();
  });

  test("Verify Message Button", async ({ page }) => {
    await page.goto(url);
    await page.getByText("Cool Shirt").click();
    await page.getByLabel("Email Seller").click();
    await expect(page.getByRole("dialog")).toBeVisible();
  });

  test("Verify Clicking Favorite Button", async ({ page }) => {
    await page.goto(url);
    await page.getByText("Cool Shirt").click();
    await page.getByLabel("Favorite").click();
    await expect(page.getByLabel("favorited message")).toBeVisible();
    // click again to unfavorite
    await page.getByLabel("Favorite", { exact: true }).click();

    await expect(page.getByLabel("unfavorited message")).toBeVisible();
  });
});
