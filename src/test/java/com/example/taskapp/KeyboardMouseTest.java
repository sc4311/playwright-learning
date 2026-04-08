package com.example.taskapp;

import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Demonstrates keyboard and mouse interactions in Playwright.
 *
 * Most of the time you'll use page.fill() and page.click(), but knowing
 * the low-level keyboard/mouse APIs is important for:
 *   - Keyboard shortcuts
 *   - Drag and drop
 *   - Hover effects
 *   - Complex form interactions
 */
class KeyboardMouseTest extends PlaywrightBaseTest {

    // --- Keyboard ---

    @Test
    void typeIntoField() {
        page.navigate(url("/tasks/new"));

        // fill() clears the field first then types — use for most form inputs
        page.fill("#title", "Typed by Playwright");

        assertThat(page.locator("#title")).hasValue("Typed by Playwright");
    }

    @Test
    void pressEnterToSubmit() {
        page.navigate(url("/tasks/new"));

        page.fill("#title", "Enter Key Task");
        // Press a specific key — same names as JavaScript KeyboardEvent.key
        page.keyboard().press("Enter");

        assertThat(page).hasURL(url("/tasks"));
    }

    @Test
    void tabBetweenFields() {
        page.navigate(url("/tasks/new"));

        // Focus the first field, then Tab to move to the next
        page.click("#title");
        page.keyboard().press("Tab");

        // After Tab, focus should be on the description textarea
        assertThat(page.locator("#description")).isFocused();
    }

    @Test
    void selectAllAndReplace() {
        page.navigate(url("/tasks/new"));

        page.fill("#title", "Original text");

        // Ctrl+A selects all text, then type replaces it
        page.locator("#title").press("Control+a");
        page.keyboard().type("Replaced text");

        assertThat(page.locator("#title")).hasValue("Replaced text");
    }

    @Test
    void keyboardShortcut() {
        page.navigate(url("/tasks/new"));
        page.fill("#title", "Shortcut Task");

        // Modifier keys: Control, Shift, Alt, Meta (Cmd on Mac)
        page.keyboard().press("Control+Enter"); // submits in some browsers
    }

    @Test
    void typeSlowly() {
        page.navigate(url("/tasks/new"));

        // type() simulates real keystrokes one character at a time
        // (unlike fill() which sets the value directly)
        // Useful for testing autocomplete or real-time validation
        page.locator("#title").type("Slow typing...");

        assertThat(page.locator("#title")).hasValue("Slow typing...");
    }

    // --- Mouse ---

    @Test
    void hoverOverElement() {
        page.navigate(url("/tasks"));

        // Trigger CSS :hover effects or tooltip visibility
        page.locator(".task-title").first().hover();

        // Just verify we can hover without error — hover effects are CSS-only here
        assertThat(page.locator(".task-title").first()).isVisible();
    }

    @Test
    void clickWithPosition() {
        page.navigate(url("/tasks/new"));

        // Click at a specific pixel offset within an element
        page.locator("#submit-btn").click(
                new com.microsoft.playwright.Locator.ClickOptions().setPosition(10, 5)
        );

        assertThat(page).hasURL(url("/tasks"));
    }

    @Test
    void doubleClick() {
        page.navigate(url("/tasks"));

        // Double-click — typically selects text or triggers a different action
        page.locator(".task-title").first().dblclick();
    }

    @Test
    void rightClick() {
        page.navigate(url("/tasks"));

        // Right-click opens context menu in a real browser
        page.locator(".task-title").first().click(
                new com.microsoft.playwright.Locator.ClickOptions()
                        .setButton(com.microsoft.playwright.options.MouseButton.RIGHT)
        );
    }

    @Test
    void mouseCoordinates() {
        page.navigate(url("/tasks"));

        // Low-level mouse control — move to absolute page coordinates then click
        page.mouse().move(100, 300);
        page.mouse().click(100, 300);
    }
}
