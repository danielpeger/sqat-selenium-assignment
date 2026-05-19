# Selenium Assignment — Vimeo

Selenium-based UI tests for `vimeo.com`, written in Java with JUnit 4 and the Page Object Model.
The project is built and executed with Gradle.

- Tests live in `src/test/java/`
  - `BasicTasks.java` — basic Selenium tasks (forms, waits, XPath, dropdowns, etc.)
  - `AdvancedTasks.java` — advanced tasks (cookies, hover, drag & drop, upload, JS executor, cross-browser, headless, …)
  - `pages/` — Page Object classes (`BasePage`, `HomePage`, `LoginPage`, `SignupPage`, `SearchResultsPage`, `UploadPage`)
  - `util/` — `WebDriverFactory`, `ConfigReader`, `TestDataGenerator`

## Prerequisites

- **Java 17** (the Gradle toolchain is pinned to 17 in `build.gradle`)
- **Gradle** — the included `gradlew` / `gradlew.bat` wrapper is used, no global install needed
- **Google Chrome** installed (used by default; Selenium 4 auto-resolves the matching driver via Selenium Manager)
- _(optional)_ **Safari** on macOS, if you want to run the Safari cross-browser test
  - Enable once via: Safari → Settings → Advanced → "Show Develop menu" → Develop → "Allow Remote Automation"
- A **real Vimeo account** for the tests that require login (login, logout, settings, upload, etc.)

## Configuration

Test settings (URLs, credentials, browser, waits) live in a properties file that is **not** checked into git.

1. Copy the example file:

   ```bash
   cp src/test/resources/test.properties.example src/test/resources/test.properties
   ```

2. Open `src/test/resources/test.properties` and set all keys used by the tests:

   ```properties
   base.url=https://vimeo.com
   login.url=https://vimeo.com/log_in
   signup.url=https://vimeo.com/join
   upload.url=https://vimeo.com/upload
   upload.defaults.url=https://vimeo.com/settings/videos/upload_defaults
   settings.url=https://vimeo.com/settings
   profile.url=https://vimeo.com/settings/profile/general
   about.url=https://vimeo.com/about
   video.page.url=https://vimeo.com/manage/videos/YOUR_VIDEO_ID

   user.email=YOUR_VIMEO_EMAIL
   user.password=YOUR_VIMEO_PASSWORD

   browser=chrome          # chrome | safari (unknown values fall back to chrome)
   headless=false          # true to run Chrome headless
   implicit.wait.seconds=5
   explicit.wait.seconds=15
   ```

   `test.properties` is git-ignored, so your credentials stay local.

   `ConfigReader` fails fast with a clear error if the file is missing and throws if required keys are not present.

## Running the tests

From the project root:

```bash
# Run the whole test suite
./gradlew test

# Run only the basic tasks
./gradlew test --tests BasicTasks

# Run only the advanced tasks
./gradlew test --tests AdvancedTasks

# Run a single test method
./gradlew test --tests "BasicTasks.homePageTitleContainsVimeo"
```

On Windows, use `gradlew.bat` instead of `./gradlew`.

### Headless mode

To run Chrome without a visible window, set `headless=true` in `test.properties`.
The `headlessChromeCanLoadHomePage` test always runs headless, regardless of this setting.

### Switching browser

Change `browser` in `test.properties` to `chrome` or `safari`.
The cross-browser tests in `AdvancedTasks` (`homePageLoadsInChromeBrowser`, `homePageLoadsInSafariBrowser`) override this at runtime.

## Test reports

After a run, the HTML report is generated at:

```
build/reports/tests/test/index.html
```

Open it in a browser for a per-test breakdown, stack traces, and stdout/stderr.
