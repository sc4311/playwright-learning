package com.example.taskapp;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Demonstrates tracing in Playwright.
 *
 * A trace records everything that happened during a test:
 *   - Every action (clicks, fills, navigations)
 *   - Screenshots at each step
 *   - Network requests and responses
 *   - Console logs
 *   - DOM snapshots
 *
 * To view a trace after tests run:
 *   .\gradlew.bat playwright --args="show-trace build/playwright-output/traces/trace.zip"
 *
 * Or upload to: https://trace.playwright.dev
 *
 * Output saved to: build/playwright-output/traces/
 */
class TracingTest extends PlaywrightBaseTest {

    private static final String OUTPUT = "build/playwright-output/traces/";

    @Test
    void recordFullTrace() {
        // Tracing is configured on the BrowserContext, not the Page
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)  // screenshot at every action
                .setSnapshots(true)    // DOM snapshot for timeline
                .setSources(true));    // include source code references

        // Everything from here is recorded in the trace
        page.navigate(url("/tasks"));
        page.locator(".btn-toggle").first().click();
        page.navigate(url("/"));

        // Stop and save — open this file in the Playwright Trace Viewer
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get(OUTPUT + "trace.zip")));

        assertThat(page.locator("#stats-box")).isVisible();
    }

    @Test
    void traceOnlyOnFailure() {
        // Common pattern: start tracing, run the test, only save if it fails
        // This avoids saving large trace files for every passing test
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        try {
            page.navigate(url("/tasks"));
            assertThat(page.locator("#task-list")).isVisible();
            assertThat(page.locator(".task-item")).hasCount(4);

            // Test passed — discard the trace (pass null path to not save)
            context.tracing().stop(new Tracing.StopOptions());

        } catch (AssertionError e) {
            // Test failed — save the trace so you can investigate
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get(OUTPUT + "failure-trace.zip")));
            throw e; // re-throw so the test still fails
        }
    }

    @Test
    void traceWithChunks() {
        // Start a persistent trace across multiple contexts using chunks
        // Useful for long test suites where you want a trace for each test
        BrowserContext ctx = browser.newContext();
        ctx.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        Page p = ctx.newPage();
        p.navigate(url("/"));

        // Save a chunk mid-test without stopping the trace
        ctx.tracing().stopChunk(new Tracing.StopChunkOptions()
                .setPath(Paths.get(OUTPUT + "chunk-home.zip")));

        // Start the next chunk
        ctx.tracing().startChunk();
        p.navigate(url("/tasks"));

        ctx.tracing().stopChunk(new Tracing.StopChunkOptions()
                .setPath(Paths.get(OUTPUT + "chunk-tasks.zip")));

        ctx.close();
    }
}
