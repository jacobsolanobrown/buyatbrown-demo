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
    await page.getByRole("link", { name: "Favorites" }).click();
    // Verify that the user's favorites page renders correctly
    await expect(
      page.getByRole("heading", { name: "Your Favorites" })
    ).toBeVisible();
    await expect(
      page.locator("text=View, buy, or unfavorite your favorite listings.")
    ).toBeVisible();
    await expect(page.getByText("Rain Jacket")).toBeVisible();
    await expect(page.getByText("tower fan")).toBeVisible();
    await expect(page.getByText("Bear Mug")).toBeVisible();
    await expect(page.getByText("Old Navy Sweater")).not.toBeVisible();
  });

  test("Verify Unfavorite Button Works Correctly", async ({ page }) => {
    // Get a listing card to unfavorite
    await page.getByText("Dyson Airwrap").click();
    // Verify that the modal card displays the correct information
    const dialogHeading = page
      .getByRole("dialog")
      .getByRole("heading", { name: "Dyson Airwrap" });
    await expect(dialogHeading).toBeVisible();
    await expect(page.getByRole("heading", { name: "$300" })).toBeVisible();
    // Ensure the listing is favorited (from an unfavorite state)
    // await page.getByLabel("Favorite", { exact: false }).click();
    await page.getByLabel("Favorite").click();
    await expect(page.getByLabel("favorited message")).toBeVisible();
    // Close the modal
    await page.mouse.click(0, 0);
    await expect(page.getByRole("dialog")).not.toBeVisible();

    // Navigate to the user's listings page
    await page.getByRole("link", { name: "Account: notrealnotreal" }).click();
    // Navigate to the user's favorites page
    await page.getByRole("link", { name: "Favorites" }).click();

    // Get the listing card to verify that it is favorited
    await page.getByText("Dyson Airwrap").click();
    // Verify that the modal card displays the correct information
    const dialogHeading2 = page
      .getByRole("dialog")
      .getByRole("heading", { name: "Dyson Airwrap" });
    await expect(dialogHeading2).toBeVisible();
    await expect(page.getByRole("heading", { name: "$300" })).toBeVisible();

    // Ensure the listing is favorited and unfavorite it (from favorited state)
    await page.getByLabel("Favorite", { exact: true }).click();
    // Close the modal
    await page.mouse.click(0, 0);
    await expect(page.getByRole("dialog")).not.toBeVisible();
    // Reload the page
    // Navigate to the user's listings page
    await page.getByRole("link", { name: "Account: notrealnotreal" }).click();
    // Navigate to the user's favorites page
    await page.getByRole("link", { name: "Favorites" }).click();
    // Ensure the listing is no longer favorited
    await expect(page.getByText("Dyson Airwrap")).not.toBeVisible();
  });
});
