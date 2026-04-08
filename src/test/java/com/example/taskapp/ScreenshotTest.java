package com.example.taskapp;

import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Demonstrates screenshots in Playwright.
 *
 * Screenshots are mainly used for:
 *   - Debugging failing tests (capture what the browser saw)
 *   - Visual regression testing (compare against a baseline)
 *   - Generating documentation
 *
 * Output saved to: build/playwright-output/screenshots/
 */
class ScreenshotTest extends PlaywrightBaseTest {

    private static final String OUTPUT = "build/playwright-output/screenshots/";

    @Test
    void fullPageScreenshot() {
        page.navigate(url("/tasks"));

        // Captures the entire scrollable page, not just the visible viewport
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(OUTPUT + "full-page.png"))
                .setFullPage(true));

        assertThat(page.locator("#task-list")).isVisible();
    }

    @Test
    void viewportScreenshot() {
        page.navigate(url("/"));

        // Captures only what's visible in the browser window (default)
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(OUTPUT + "viewport.png")));

        assertThat(page.locator("#stats-box")).isVisible();
    }

    @Test
    void elementScreenshot() {
        page.navigate(url("/tasks"));

        // Capture a single element — useful for component-level visual tests
        page.locator(".task-item").first()
                .screenshot(new com.microsoft.playwright.Locator.ScreenshotOptions()
                        .setPath(Paths.get(OUTPUT + "task-item.png")));

        assertThat(page.locator(".task-item").first()).isVisible();
    }

    @Test
    void screenshotOnFailure() {
        page.navigate(url("/tasks"));

        try {
            // Simulate a test step that might fail
            assertThat(page.locator("#non-existent")).isVisible(
                    new com.microsoft.playwright.assertions.LocatorAssertions
                            .IsVisibleOptions().setTimeout(1000));
        } catch (AssertionError e) {
            // Capture what the page looked like when the test failed
            page.screenshot(new Page.ScreenshotOptions()
                    .setPath(Paths.get(OUTPUT + "on-failure.png")));
            // In real projects, hook this into @AfterEach so it runs automatically
        }
    }

    @Test
    void screenshotWithClip() {
        page.navigate(url("/"));

        // Capture a specific region of the page by pixel coordinates
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(OUTPUT + "clipped.png"))
                .setClip(0, 0, 400, 200)); // x, y, width, height
    }
}
