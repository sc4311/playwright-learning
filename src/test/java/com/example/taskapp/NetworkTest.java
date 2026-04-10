package com.example.taskapp;

import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.Request;
import com.microsoft.playwright.Route;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstrates network interception in Playwright.
 *
 * Use cases:
 *   - Mock API responses so tests don't depend on real data
 *   - Simulate slow networks or failures
 *   - Assert that specific requests are made
 *   - Spy on request/response payloads
 */
class NetworkTest extends PlaywrightBaseTest {

    @Test
    void mockApiResponse() {
        // Intercept the REST endpoint and return a controlled response
        // This lets you test your UI independently of the backend
        page.route("**/api/tasks", route -> route.fulfill(
                new Route.FulfillOptions()
                        .setContentType("application/json")
                        .setBody("[{\"id\":99,\"title\":\"Mocked Task\",\"description\":\"From mock\",\"completed\":false}]")
        ));

        // The page still loads normally, but /api/tasks returns our fake data
        page.navigate(url("/api/tasks"));
        assertTrue(page.content().contains("Mocked Task"));
    }

    @Test
    void mockStatsEndpoint() {
        page.route("**/api/tasks/stats", route -> route.fulfill(
                new Route.FulfillOptions()
                        .setContentType("application/json")
                        .setBody("{\"total\":100,\"completed\":75,\"pending\":25}")
        ));

        page.navigate(url("/api/tasks/stats"));
        assertTrue(page.content().contains("100"));
        assertTrue(page.content().contains("75"));
    }

    @Test
    void simulateNetworkFailure() {
        // Abort a request to test how your app handles network errors
        page.route("**/api/tasks", Route::abort);

        // Aborting the route causes navigate() to throw a PlaywrightException
        // because the browser receives net::ERR_FAILED instead of a response
        assertThrows(PlaywrightException.class, () -> page.navigate(url("/api/tasks")));
    }

    @Test
    void blockImages() {
        // Block all image requests — speeds up tests that don't need images
        page.route("**/*.{png,jpg,jpeg,gif,svg}", Route::abort);

        page.navigate(url("/tasks"));
        assertThat(page.locator("#task-list")).isVisible();
    }

    @Test
    void spyOnRequests() {
        List<String> requestedUrls = new ArrayList<>();

        // Listen to all requests made by the page — useful for verifying
        // that your app calls the right endpoints
        page.onRequest(request -> requestedUrls.add(request.url()));

        page.navigate(url("/tasks"));

        assertTrue(requestedUrls.stream().anyMatch(u -> u.contains("/tasks")));
    }

    @Test
    void spyOnRequestMethod() {
        List<String> postRequests = new ArrayList<>();

        page.onRequest(request -> {
            if ("POST".equals(request.method())) {
                postRequests.add(request.url());
            }
        });

        page.navigate(url("/tasks"));
        page.locator(".btn-toggle").first().click();

        // After clicking toggle, the browser should have made a POST request
        assertFalse(postRequests.isEmpty(), "Expected a POST request after toggle");
    }

    @Test
    void assertResponseStatus() {
        page.navigate(url("/tasks"));

        // Make a direct API request from within the page context and check status
        int status = (int) page.evaluate("async () => { const r = await fetch('/api/tasks'); return r.status; }");
        assertEquals(200, status);
    }

    @Test
    void passThrough() {
        // Inspect the request then let it continue normally
        page.route("**/api/**", route -> {
            Request request = route.request();
            System.out.println("API call: " + request.method() + " " + request.url());
            route.resume(); // continue without modification
        });

        page.navigate(url("/api/tasks"));
        assertTrue(page.content().contains("title"));
    }
}
