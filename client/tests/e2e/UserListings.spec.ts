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

    // Begin at the home page
    await page.goto(url);
    await expect(page.getByRole("heading", { name: "Home" })).toBeVisible();
    await expect(page.getByLabel("post listing")).toBeVisible();
  });

  test("Verify User Listings Page Renders Correctly", async ({ page }) => {
    // Navigate to the user's listings page
    await page.getByRole("link", { name: "Account: notrealnotreal" }).click();
    await expect(
      page.getByRole("heading", { name: "Your Listings" })
    ).toBeVisible();
    await expect(page.getByText("Old Navy Sweater")).toBeVisible();
    await expect(page.getByText("Bear Mug")).toBeVisible();
    await expect(page.getByText("Coach Bag")).toBeVisible();
    await expect(page.getByText("Rain Jacket")).not.toBeVisible();
  });

  test("Verify Edit Modal Displays Correct Information", async ({ page }) => {
    // Navigate to the user's listings page
    await page.getByRole("link", { name: "Account: notrealnotreal" }).click();
    // Click on the edit button for a listing to open the edit modal
    await page.getByText("Old Navy Sweater").click();
    const dialogHeading = page
      .getByRole("dialog")
      .getByRole("heading", { name: "Old Navy Sweater" });
    await expect(dialogHeading).toBeVisible();
    await expect(
      page.getByText("Old navy sweater that is vintage!")
    ).toBeVisible();
    await expect(page.getByRole("heading", { name: "$24" })).toBeVisible();
    await expect(
      page.getByRole("button", { name: "Edit Listing" })
    ).toBeVisible();
    await expect(
      page.getByRole("button", { name: "Delete Listing" })
    ).toBeVisible();
  });

  test("Verify Editing Form Opens from Modal", async ({ page }) => {
    // Navigate to the user's listings page
    await page.getByRole("link", { name: "Account: notrealnotreal" }).click();
    // Click on the edit button for a listing to open the edit modal
    await page.getByText("Old Navy Sweater").click();
    await page.getByRole("button", { name: "Edit Listing" }).click();
    await expect(
      page.getByRole("heading", { name: "Edit Your Listing" })
    ).toBeVisible();
  });

  test("Verify Edit Functionality", async ({ page }) => {
    // Navigate to the user's listings page
    await page.getByRole("link", { name: "Account: notrealnotreal" }).click();
    // Click on the edit button for a listing to open the edit modal
    await page.getByText("Old Navy Sweater").click();
    await page.getByRole("button", { name: "Edit Listing" }).click();

    await expect(page.getByRole("heading")).toContainText("Edit Your Listing");
    await expect(page.locator("#root")).toContainText(
      "Current Listing Title: Old Navy Sweater"
    );
    // price
    await expect(page.locator("#root")).toContainText("Current Price: $24");
    // description
    await expect(page.locator("#root")).toContainText(
      "Current Description: Old navy sweater that is vintage!"
    );
    // check that condition is a dropdown:
    await expect(page.locator("#root")).toContainText(
      "Current Condition: used"
    );
    await page.getByLabel("Condition").selectOption("New");
    // check that category is a dropdown:
    await expect(page.getByLabel("Category")).toBeVisible();
    await page.getByLabel("Category").selectOption("Furniture");
    // tags
    await expect(page.locator("#root")).toContainText("Current Tags: Sweater");
    // update button
    await expect(
      page.getByRole("button", { name: "Update Listing" })
    ).toBeVisible();
  });

  test("Verify Cancel Functionality", async ({ page }) => {
    // Navigate to the user's listings page
    await page.getByRole("link", { name: "Account: notrealnotreal" }).click();
    // Click on the edit button for a listing to open the edit modal
    await page.getByText("Old Navy Sweater").click();
    await page.getByRole("button", { name: "Edit Listing" }).click();

    // Editing form should be visible
    await expect(page.getByRole("heading")).toContainText("Edit Your Listing");
    await expect(page.locator("#root")).toContainText(
      "Current Listing Title: Old Navy Sweater"
    );
    // price
    await expect(page.locator("#root")).toContainText("Current Price: $24");
    // description
    await expect(page.locator("#root")).toContainText(
      "Current Description: Old navy sweater that is vintage!"
    );
    // Click the cancel button
    await expect(page.getByRole("button", { name: "Cancel" })).toBeVisible();
    await page.getByRole("button", { name: "Cancel" }).click();

    // Verify that the modal is closed and the listing is not updated
    await expect(page.getByRole("dialog")).not.toBeVisible();
    await expect(page.getByText("Old Navy Sweater")).toBeVisible();
    await expect(page.getByText("$24")).toBeVisible();
  });

  test("Verify Delete Functionality", async ({ page }) => {
    await page.goto(url);
    await page.goto("http://localhost:8000/listing-form");
    // Fill out form:
    await page.getByPlaceholder("Choose a listing title").click();
    await page.getByPlaceholder("Choose a listing title").fill("Jane Austen");
    await page.getByPlaceholder("Choose a price").click();
    await page.getByPlaceholder("Choose a price").fill("30");
    await page.getByPlaceholder("Upload an image (png or jpeg)").click();
    await page
      .getByPlaceholder("Upload an image (png or jpeg)")
      .setInputFiles("./src/assets/JaneAustenCover.jpeg");
    await page.getByPlaceholder("Describe your listing").click();
    await page
      .getByPlaceholder("Describe your listing")
      .fill("persuasion was an alright book");
    await page.getByLabel("Condition").selectOption("Like New");
    await page.getByLabel("Category").selectOption("School");
    await page.getByPlaceholder("Add tags").click();
    await page.getByPlaceholder("Add tags").fill("books");
    // Post it:
    await page.getByRole("button", { name: "Post Listing" }).click();
    // Check that listing card of tv appears on the home page:
    await expect(page.getByRole("heading", { name: "Home" })).toBeVisible();
    await expect(page.getByText("Jane Austen")).toBeVisible();
    await expect(page.getByLabel("all listings")).toContainText("Jane Austen");
    await expect(page.getByLabel("all listings")).toContainText("$30");
    await expect(page.getByLabel("all listings")).toContainText(
      "by notrealnotreal"
    );
    // Check that the bigger on click card has correct info:
    await page.getByText("Jane Austen").click();
    await expect(page.getByLabel("Close modal")).toBeVisible();
    await expect(
      page.getByRole("dialog").getByRole("heading", { name: "Jane Austen" })
    ).toBeVisible();
    await expect(page.getByRole("heading", { name: "like new" })).toBeVisible();
    await expect(page.getByRole("heading", { name: "$" })).toBeVisible();
    await expect(page.getByRole("dialog")).toContainText(
      "persuasion was an alright book"
    );
    await expect(page.getByRole("dialog")).toContainText("Tags: School, books");
    await page.getByLabel("Close modal").click();

    // Navigate to the user's listings page
    // Navigate to the user's listings page
    await page.getByRole("link", { name: "Account: notrealnotreal" }).click();
    await expect(
      page.getByRole("heading", { name: "Your Listings" })
    ).toBeVisible();
    await expect(page.locator("#root")).toContainText(
      "Edit, or delete active/sold listings."
    );
    await expect(page.locator("#root")).toContainText("Jane Austen");
    await expect(page.locator("#root")).toContainText("$30");
    await expect(page.locator("#root")).toContainText("by notrealnotreal");
    // Check big card on "Your listings" has correct content
    await page.getByText("Jane Austen").click();
    await expect(page.getByRole("dialog")).toContainText("Tags: School, books");
    await expect(
      page.getByRole("button", { name: "Edit Listing" })
    ).toBeVisible();
    await expect(
      page.getByRole("button", { name: "Delete Listing" })
    ).toBeVisible();
    await page.getByRole("button", { name: "Delete Listing" }).click();

    // Navigate to the user's listings page
    await page.getByRole("link", { name: "Account: notrealnotreal" }).click();
    // Click on the edit button for a listing to open the edit modal

    // Verify that the card is not visible
    await expect(page.getByText("Jane Austen")).not.toBeVisible();
    await expect(page.getByText("$30")).not.toBeVisible();

    // Verify that the modal is closed and the listing is not updated
    await expect(page.getByRole("dialog")).not.toBeVisible();
    await expect(page.getByText("Jane Austen")).not.toBeVisible();
    await expect(page.getByText("$30")).not.toBeVisible();
  });
});
