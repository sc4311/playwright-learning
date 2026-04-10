package com.example.taskapp;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Demonstrates every major way to locate elements in Playwright.
 *
 * Prefer this order for robustness (top = most resilient to UI changes):
 *   1. getByRole       — based on ARIA, survives visual redesigns
 *   2. getByLabel      — ties to form labels, very stable
 *   3. getByText       — visible text, easy but breaks on copy changes
 *   4. getByPlaceholder
 *   5. CSS #id / .class
 *   6. XPath           — last resort, brittle
 */
class LocatorStrategiesTest extends PlaywrightBaseTest {

    @Test
    void byId() {
        page.navigate(url("/"));
        // #id is fast and specific — great when elements have stable IDs
        assertThat(page.locator("#page-title")).hasText("Task Manager");
    }

    @Test
    void byClass() {
        page.navigate(url("/tasks"));
        // .class matches all elements with that class
        assertThat(page.locator(".task-item").first()).isVisible();
    }

    @Test
    void byCssAttribute() {
        page.navigate(url("/tasks/new"));
        // Attribute selector — match any HTML attribute
        assertThat(page.locator("input[type='text']")).isVisible();
    }

    // --- Semantic locators ---

    @Test
    void byRole() {
        page.navigate(url("/tasks/new"));
        // ARIA role + accessible name — most resilient locator
        assertThat(page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Create Task"))).isVisible();
    }

    @Test
    void byLabel() {
        page.navigate(url("/tasks/new"));
        // Finds the <input> associated with a <label> — great for forms
        assertThat(page.getByLabel("Title *")).isVisible();
    }

    @Test
    void byText() {
        page.navigate(url("/tasks"));
        // Finds element by its visible text content
        assertThat(page.getByText("Buy groceries")).isVisible();
    }

    @Test
    void byPlaceholder() {
        page.navigate(url("/tasks/new"));
        assertThat(page.getByPlaceholder("Enter task title")).isVisible();
    }

    // --- XPath ---

    @Test
    void byXPath() {
        page.navigate(url("/tasks/new"));
        // Powerful but fragile — breaks easily when HTML structure changes
        assertThat(page.locator("xpath=//button[@id='submit-btn']")).isVisible();
    }

    // --- Chaining and filtering ---

    @Test
    void scopedWithinParent() {
        page.navigate(url("/tasks"));
        // Narrow a locator to search only inside a parent element
        // Prevents accidentally grabbing the wrong matching element on the page
        Locator firstTask = page.locator(".task-item").first();
        assertThat(firstTask.locator(".btn-toggle")).isVisible();
    }

    @Test
    void filterByText() {
        page.navigate(url("/tasks"));
        // Find all task items, then filter to only the one containing specific text
        Locator groceryTask = page.locator(".task-item")
                .filter(new Locator.FilterOptions().setHasText("Buy groceries"));
        assertThat(groceryTask).isVisible();
    }

    @Test
    void nthElement() {
        page.navigate(url("/tasks"));
        // Pick a specific item from a list by zero-based index
        assertThat(page.locator(".task-item").nth(0)).isVisible(); // first
        assertThat(page.locator(".task-item").nth(2)).isVisible(); // third
    }

    @Test
    void countElements() {
        page.navigate(url("/tasks"));
        // Count how many elements match a locator
        int count = page.locator(".task-item").count();
        assertEquals(4, count);
    }

    @Test
    void firstAndLast() {
        page.navigate(url("/tasks"));
        // Shortcuts for first() and last() instead of nth(0) / nth(n-1)
        assertThat(page.locator(".task-item").first()).isVisible();
        assertThat(page.locator(".task-item").last()).isVisible();
    }
}
