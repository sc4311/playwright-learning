package com.example.taskapp;

import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskPageTest extends PlaywrightBaseTest {

    @Test
    void homePageShowsStats() {
        page.navigate(url("/"));

        assertThat(page.locator("#page-title")).hasText("Task Manager");
        assertThat(page.locator("#stat-total .stat-number")).hasText("3");
        assertThat(page.locator("#stat-pending .stat-number")).hasText("3");
    }

    @Test
    void taskListShowsAllTasks() {
        page.navigate(url("/tasks"));

        assertThat(page.locator("#task-list")).isVisible();
        assertTrue(page.locator(".task-item").count() >= 4);
    }

    @Test
    void canCreateANewTask() {
        page.navigate(url("/tasks/new"));

        page.fill("#title", "My Playwright Task");
        page.fill("#description", "Created during a test");
        page.click("#submit-btn");

        // Should redirect to /tasks and show the new task
        assertThat(page).hasURL(url("/tasks"));
        assertThat(page.locator("#task-list")).containsText("My Playwright Task");
    }

    @Test
    void formShowsValidationErrorWhenTitleIsEmpty() {
        page.navigate(url("/tasks/new"));

        page.click("#submit-btn");

        assertThat(page.locator("#title-error")).isVisible();
        assertThat(page.locator("#title-error")).containsText("required");
    }

    @Test
    void canMarkTaskAsCompleted() {
        page.navigate(url("/tasks"));

        // Click the first "Done" button
        page.locator(".btn-toggle").first().click();

        // At least one task should now be marked completed
        assertTrue(page.locator(".task-item.completed").count() >= 1);
    }

    @Test
    void filterShowsOnlyPendingTasks() {
        page.navigate(url("/tasks?filter=pending"));

        assertThat(page.locator("#filter-pending")).hasClass("active");
        // No completed items should be visible
        assertTrue(page.locator(".task-item.completed").count() == 0);
    }

    @Test
    void taskDetailPageShowsCorrectInfo() {
        page.navigate(url("/tasks"));

        // Click the first task title link
        page.locator(".task-title").first().click();

        assertThat(page.locator("#task-status")).hasText("Pending");
        assertThat(page.locator("#toggle-btn")).isVisible();
        assertThat(page.locator("#delete-btn")).isVisible();
    }
}
