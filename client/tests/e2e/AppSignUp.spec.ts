import { setupClerkTestingToken } from "@clerk/testing/playwright";
import { test, expect } from "@playwright/test";
import { clerkSetup } from "@clerk/testing/playwright";
import { clerk } from "@clerk/testing/playwright";

const url = "http://localhost:8000/";

/**
 * Must be running http://localhost:3232/ server for these tests to pass.
 */

test.beforeAll(async () => {
  await clerkSetup({
    publishableKey: process.env.VITE_CLERK_PUBLISHABLE_KEY,
    frontendApiUrl: "http://localhost:8000",
  });
});

//  ############## TESTING USER AUTH ##############

/**
 * Test to verify the presence of the Sign in button when the page initially loads.
 */
test.describe("Sign-In Page Tests", () => {
  // Test to verify that the sign-in page renders correctly
  test("Verify Sign-In Page Renders Correctly", async ({ page }) => {
    await setupClerkTestingToken({ page });
    await page.goto(url);
    await clerk.loaded({ page });
    await expect(page).toHaveURL(url);
    // heading
    await expect(
      page.getByRole("heading", { name: "BUY @ BROWN" })
    ).toBeVisible();
    // heading
    await expect(
      page.getByRole("heading", {
        name: "Buy & Sell Exclusively at Brown By Students, For Students",
      })
    ).toBeVisible();
    // sign in button
    await expect(
      page.getByRole("button", {
        name: "Sign in with Clerk",
      })
    ).toBeVisible();
  });

  // Test to verify that the sign-in button triggers the Clerk sign-in process
  test("Verify Sign-In Button Click", async ({ page }) => {
    await setupClerkTestingToken({ page });
    await page.goto(url);
    await clerk.loaded({ page });
    await page.getByRole("button", { name: "Sign in with Clerk" }).click();
    // Add assertions to verify that the Clerk sign-in process is triggered
    await expect(page).toHaveURL(/.*clerk/); // Example assertion, adjust based on actual behavior
  });
});

test.describe("Create a username", () => {
  test.beforeEach(async ({ page }) => {
    await setupClerkTestingToken({ page });
    await page.goto(url);
    await clerk.loaded({ page });

    await clerk.signIn({
      page,
      signInParams: {
        strategy: "password",
        password: process.env.E2E_CLERK_USER1_PASSWORD!,
        identifier: process.env.E2E_CLERK_USER1_USERNAME!,
      },
    });

    // Ensure the username page renders correctly
    await expect(
      page.locator(
        "text=Create a username to start selling now!(Other users will see this username on listings)"
      )
    ).toBeVisible();

    // Ensure the input field is visible
    await expect(
      page.locator('input[placeholder="Enter your username"]')
    ).toBeVisible();

    // Cancel button is visible
    const cancelButton = page.getByRole("button", { name: "Cancel" }).nth(0); // First button
    await expect(cancelButton).toBeVisible();

    // Submit button is visible
    const submitButton = page.getByRole("button", { name: "Submit" }).nth(1); // First button
    await expect(submitButton).toBeVisible();

  });
});
