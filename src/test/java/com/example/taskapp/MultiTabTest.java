package com.example.taskapp;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Demonstrates working with multiple tabs and browser contexts.
 *
 * Context  = an isolated browser session (separate cookies, storage, login state)
 * Page     = a single tab within a context
 *
 * Use multiple contexts to simulate multiple users in the same test.
 * Use multiple pages (tabs) to test things like "open in new tab".
 */
class MultiTabTest extends PlaywrightBaseTest {

    @Test
    void openNewTab() {
        page.navigate(url("/tasks"));

        // Open a second tab in the same browser context (shares cookies/session)
        Page secondTab = context.newPage();
        secondTab.navigate(url("/"));

        assertThat(page.locator("#task-list")).isVisible();
        assertThat(secondTab.locator("#stats-box")).isVisible();

        // Both tabs are independent — actions on one don't affect the other
        assertEquals(url("/tasks"), page.url());
        assertEquals(url("/"), secondTab.url());

        secondTab.close();
    }

    @Test
    void switchBetweenTabs() {
        page.navigate(url("/"));

        Page tab2 = context.newPage();
        tab2.navigate(url("/tasks"));

        Page tab3 = context.newPage();
        tab3.navigate(url("/tasks/new"));

        // Switch focus between tabs by just using the Page reference
        assertThat(page.locator("#page-title")).hasText("Task Manager");
        assertThat(tab2.locator("#task-list")).isVisible();
        assertThat(tab3.locator("#task-form")).isVisible();

        tab2.close();
        tab3.close();
    }

    @Test
    void listAllOpenPages() {
        Page tab2 = context.newPage();
        tab2.navigate(url("/tasks"));

        List<Page> pages = context.pages();
        assertEquals(2, pages.size());

        tab2.close();
    }

    @Test
    void captureNewTabOpenedByClick() {
        page.navigate(url("/tasks"));

        // waitForPage() captures a tab that opens as a result of a user action
        // (e.g. clicking a link with target="_blank")
        // Here we simulate it by manually opening a page to show the pattern
        Page newTab = context.waitForPage(() -> {
            context.newPage().navigate(url("/"));
        });

        assertThat(newTab).hasURL(url("/"));
        newTab.close();
    }

    @Test
    void twoIsolatedUserSessions() {
        // Each BrowserContext is completely isolated — separate cookies, storage
        // Use this to simulate two different logged-in users in one test

        BrowserContext userAContext = browser.newContext();
        BrowserContext userBContext = browser.newContext();

        Page userA = userAContext.newPage();
        Page userB = userBContext.newPage();

        userA.navigate(url("/tasks"));
        userB.navigate(url("/"));

        // User A sees tasks list, User B sees home — fully independent sessions
        assertThat(userA.locator("#task-list")).isVisible();
        assertThat(userB.locator("#stats-box")).isVisible();

        userAContext.close();
        userBContext.close();
    }

    @Test
    void runActionsInParallelAcrossTabs() {
        // Navigate multiple tabs simultaneously instead of sequentially
        Page tab2 = context.newPage();
        Page tab3 = context.newPage();

        // All three navigate at the same time
        page.navigate(url("/"));
        tab2.navigate(url("/tasks"));
        tab3.navigate(url("/tasks/new"));

        // Then assert each one
        assertThat(page.locator("#page-title")).hasText("Task Manager");
        assertThat(tab2.locator("#task-list")).isVisible();
        assertThat(tab3.locator("#task-form")).isVisible();

        tab2.close();
        tab3.close();
    }
}
