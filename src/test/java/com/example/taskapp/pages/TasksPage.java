package com.example.taskapp.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object for the /tasks page.
 *
 * The Page Object Model (POM) is the most important design pattern in Playwright.
 * It wraps a page's elements and actions into a reusable class so that:
 *   - Tests read like plain English
 *   - When the UI changes, you fix one class instead of every test
 *   - Test logic is separated from page structure
 *
 * Rule: Page Objects contain NO assertions — that belongs in the test.
 */
public class TasksPage {

    private final Page page;
    private final String baseUrl;

    public TasksPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    // --- Navigation ---

    public TasksPage navigate() {
        page.navigate(baseUrl + "/tasks");
        return this;
    }

    public TaskFormPage clickAddTask() {
        page.click("#add-task-btn");
        return new TaskFormPage(page, baseUrl);
    }

    public TaskDetailPage clickTask(String title) {
        page.locator(".task-title")
                .filter(new Locator.FilterOptions().setHasText(title))
                .click();
        return new TaskDetailPage(page, baseUrl);
    }

    // --- Actions ---

    public TasksPage filterByAll() {
        page.click("#filter-all");
        return this;
    }

    public TasksPage filterByPending() {
        page.click("#filter-pending");
        return this;
    }

    public TasksPage filterByCompleted() {
        page.click("#filter-completed");
        return this;
    }

    public TasksPage toggleFirstTask() {
        page.locator(".btn-toggle").first().click();
        return this;
    }

    public TasksPage deleteFirstTask() {
        page.locator(".btn-delete").first().click();
        return this;
    }

    // --- Queries (return data, no assertions) ---

    public int getTaskCount() {
        return page.locator(".task-item").count();
    }

    public int getCompletedCount() {
        return page.locator(".task-item.completed").count();
    }

    public boolean hasTask(String title) {
        return page.locator(".task-title")
                .filter(new Locator.FilterOptions().setHasText(title))
                .count() > 0;
    }

    public String getActiveFilter() {
        return page.locator(".filters a.active").textContent();
    }
}
