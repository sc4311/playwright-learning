package com.example.taskapp;

import com.example.taskapp.pages.TaskFormPage;
import com.example.taskapp.pages.TasksPage;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests written using the Page Object Model.
 *
 * Compare these tests to TaskPageTest — notice how they read like
 * plain English business requirements rather than low-level UI steps.
 * This is the main benefit of POM.
 *
 * The fluent API (methods return Page Objects) lets you chain actions:
 *   tasksPage.clickAddTask().fillTitle("x").fillDescription("y").submit()
 */
class PageObjectTest extends PlaywrightBaseTest {

    private TasksPage tasksPage() {
        return new TasksPage(page, url(""));
    }

    private TaskFormPage taskForm() {
        return new TaskFormPage(page, url(""));
    }

    @Test
    void threeTasksAreShownByDefault() {
        TasksPage tasks = tasksPage().navigate();

        assertEquals(3, tasks.getTaskCount());
    }

    @Test
    void canCreateATask() {
        TasksPage tasks = taskForm()
                .navigate()
                .fillTitle("Interview Prep")
                .fillDescription("Study Playwright POM")
                .submit();

        assertTrue(tasks.hasTask("Interview Prep"));
        assertEquals(4, tasks.getTaskCount());
    }

    @Test
    void emptyTitleShowsValidationError() {
        TaskFormPage form = taskForm()
                .navigate()
                .submitExpectingError();

        assertTrue(form.hasTitleError());
        assertTrue(form.getTitleError().contains("required"));
    }

    @Test
    void canToggleTaskToCompleted() {
        TasksPage tasks = tasksPage().navigate();

        assertEquals(0, tasks.getCompletedCount());
        tasks.toggleFirstTask();
        assertEquals(1, tasks.getCompletedCount());
    }

    @Test
    void filterShowsOnlyPendingTasks() {
        tasksPage().navigate().toggleFirstTask(); // make one completed

        TasksPage tasks = tasksPage().navigate().filterByPending();

        assertEquals("Pending", tasks.getActiveFilter());
        assertEquals(0, tasks.getCompletedCount());
    }

    @Test
    void canDeleteATask() {
        TasksPage tasks = tasksPage().navigate();
        int before = tasks.getTaskCount();

        tasks.deleteFirstTask();

        assertEquals(before - 1, tasks.getTaskCount());
    }

    @Test
    void taskDetailShowsCorrectStatus() {
        // Fluent chain: navigate → click task → read detail page
        String status = tasksPage()
                .navigate()
                .clickTask("Buy groceries")
                .getStatus();

        assertEquals("Pending", status);
    }

    @Test
    void canToggleFromDetailPage() {
        String status = tasksPage()
                .navigate()
                .clickTask("Buy groceries")
                .toggle()
                .getStatus();

        assertEquals("Completed", status);
    }
}
