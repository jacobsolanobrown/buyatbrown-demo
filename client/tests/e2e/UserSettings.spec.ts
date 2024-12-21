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

test.describe("User Listings Page Tests", () => {
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

    await page.goto(url);
    await expect(page.getByRole("heading", { name: "Home" })).toBeVisible();
    await expect(page.getByLabel("post listing")).toBeVisible();
  });

  test("Verify User Favorites Page Renders Correctly", async ({ page }) => {
    // Navigate to the user's listings page
    await page.getByRole("link", { name: "Account: notrealnotreal" }).click();
    // Navigate to the user's favorites page
    await page.getByRole("link", { name: "Settings" }).click();
    // Verify that the user's favorites page renders correctly
    await expect(page.getByRole("heading", { name: "Settings" })).toBeVisible();
    await expect(page.locator("text=Delete Account:")).toBeVisible();
    await expect(page.locator("text=Warning: This action is irreversible.")).toBeVisible();
    await expect(
      page.getByRole("button", { name: "Yes, Delete My Account" })
    ).toBeVisible();
  });
});
