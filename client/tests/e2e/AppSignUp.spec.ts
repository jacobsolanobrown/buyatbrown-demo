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

//  ############## TESTING USER AUTH AND FIREBASE FUNCTIONALITIES ##############

/**
 * Test to verify the presence of the Sign in button when the page initially loads.
 */
test("Sign in page should have Sign In Button and Maps", async ({ page }) => {
  await setupClerkTestingToken({ page });
  await page.goto(url);
  await clerk.loaded({ page });
  await expect(
    page.getByRole("heading", { name: "Sprint 5: Maps" })
  ).toBeVisible();
  await expect(page.getByRole("button", { name: "Sign in" })).toBeVisible();
});
