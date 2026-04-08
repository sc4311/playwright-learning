package com.example.taskapp;

import com.example.taskapp.service.TaskService;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Base class for all Playwright tests.
 *
 * Useful flags when running tests:
 *   Show the browser window:  ./gradlew cleanTest test -Dplaywright.headful=true
 *   Slow down each action:    ./gradlew cleanTest test -Dplaywright.headful=true -Dplaywright.slowmo=500
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class PlaywrightBaseTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TaskService taskService;

    static Playwright playwright;
    static Browser browser;

    BrowserContext context;
    Page page;

    // Read JVM system properties set via -D flags
    static final boolean HEADLESS = !Boolean.getBoolean("playwright.headful");
    static final int SLOW_MO = Integer.getInteger("playwright.slowmo", 0);

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(HEADLESS)
                .setSlowMo(SLOW_MO));
    }

    @AfterAll
    static void closeBrowser() {
        browser.close();
        playwright.close();
    }

    @BeforeEach
    void createPage() {
        taskService.reset();
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closePage() {
        context.close();
    }

    protected String url(String path) {
        return "http://localhost:" + port + path;
    }
}
