package com.example.taskapp.pages;

import com.microsoft.playwright.Page;

/**
 * Page Object for the /tasks/new form page.
 */
public class TaskFormPage {

    private final Page page;
    private final String baseUrl;

    public TaskFormPage(Page page, String baseUrl) {
        this.page = page;
        this.baseUrl = baseUrl;
    }

    public TaskFormPage navigate() {
        page.navigate(baseUrl + "/tasks/new");
        return this;
    }

    public TaskFormPage fillTitle(String title) {
        page.fill("#title", title);
        return this;
    }

    public TaskFormPage fillDescription(String description) {
        page.fill("#description", description);
        return this;
    }

    // Returns TasksPage because a successful submit redirects there
    public TasksPage submit() {
        page.click("#submit-btn");
        return new TasksPage(page, baseUrl);
    }

    // Stay on form page (e.g. after a validation error)
    public TaskFormPage submitExpectingError() {
        page.click("#submit-btn");
        return this;
    }

    public boolean hasTitleError() {
        return page.locator("#title-error").isVisible();
    }

    public String getTitleError() {
        return page.locator("#title-error").textContent();
    }
}
