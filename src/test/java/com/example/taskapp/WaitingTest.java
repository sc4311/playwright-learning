package com.example.taskapp;

import com.microsoft.playwright.options.LoadState;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Demonstrates waiting strategies in Playwright.
 *
 * KEY CONCEPT: Playwright has built-in auto-waiting.
 * Most actions (click, fill, assertThat) automatically wait up to 30s
 * for the element to be visible, enabled, and stable before acting.
 * You rarely need explicit waits — but knowing them is important for interviews.
 */
class WaitingTest extends PlaywrightBaseTest {

    @Test
    void autoWaiting() {
        page.navigate(url("/tasks"));

        // click() automatically waits for the button to be:
        //   - attached to DOM
        //   - visible
        //   - not disabled
        //   - not animating
        // No explicit wait needed — this is Playwright's biggest advantage over Selenium
        page.locator(".btn-toggle").first().click();

        assertThat(page.locator(".task-item.completed").first()).isVisible();
    }

    @Test
    void waitForUrl() {
        page.navigate(url("/tasks/new"));
        page.fill("#title", "Waiting Test Task");
        page.click("#submit-btn");

        // Block until the URL matches the pattern — useful after form submissions
        page.waitForURL("**/tasks");

        assertThat(page).hasURL(url("/tasks"));
    }

    @Test
    void waitForSelector() {
        page.navigate(url("/tasks"));

        // Explicit wait for an element to appear in the DOM
        // Use when an element is added dynamically (e.g. after an AJAX call)
        page.waitForSelector(".task-item");

        assertThat(page.locator(".task-item").first()).isVisible();
    }

    @Test
    void waitForLoadState() {
        // "load"        — window.onload fired (images, CSS fully loaded)
        // "domcontentloaded" — HTML parsed, scripts executed
        // "networkidle"  — no network requests for 500ms (good for SPAs)
        page.navigate(url("/"));
        page.waitForLoadState(LoadState.NETWORKIDLE);

        assertThat(page.locator("#stats-box")).isVisible();
    }

    @Test
    void assertThatAutoWaits() {
        page.navigate(url("/tasks"));

        // assertThat() assertions also auto-wait and retry until the condition
        // is true or the timeout is hit — no flaky sleep() calls needed
        assertThat(page.locator(".task-item")).hasCount(3);
    }

    @Test
    void waitForNavigation() {
        page.navigate(url("/tasks"));

        // waitForNavigation wraps an action that triggers page navigation
        // Ensures you don't proceed before the next page has loaded
        page.waitForNavigation(() -> page.click("#nav-home"));

        assertThat(page).hasURL(url("/"));
    }

    @Test
    void customTimeout() {
        page.navigate(url("/tasks"));

        // Override the default 30s timeout for a specific assertion
        assertThat(page.locator(".task-item").first())
                .isVisible(new com.microsoft.playwright.assertions.LocatorAssertions
                        .IsVisibleOptions().setTimeout(5000));
    }
}
