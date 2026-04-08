package com.example.taskapp.pages;

import com.microsoft.playwright.Page;

/**
 * Page Object for the /tasks/{id} detail page.
 */
public class TaskDetailPage {

    private final Page page;
    private final String baseUrl;

    public TaskDetailPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public String getStatus() {
        return page.locator("#task-status").textContent();
    }

    public String getTitle() {
        return page.locator("#page-title").textContent();
    }

    public TaskDetailPage toggle() {
        page.click("#toggle-btn");
        return this;
    }

    public TasksPage delete() {
        page.click("#delete-btn");
        return new TasksPage(page, baseUrl);
    }

    public TasksPage goBack() {
        page.click("#back-btn");
        return new TasksPage(page, baseUrl);
    }
}
