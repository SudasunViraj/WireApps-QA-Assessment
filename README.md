# 🧪 Google Translate Automation Framework - WireApps QA Assessment

## 📌 Project Overview

This project is a UI automation framework developed using:

* **Java 21**
* **Selenium 4**
* **Cucumber (BDD)**
* **JUnit 4 Runner**
* **Maven**

The framework automates core functionalities of the Google Translate web application, including:

* Text translation
* Language swapping
* Image translation
* Document translation
* Website translation

The framework supports:

* ✅ Running individual test cases
* ✅ Running full regression suite
* ✅ Tag-based execution
* ✅ HTML & JSON reporting

---

# 🏗️ Framework Architecture

The framework follows a clean and simple layered structure:

```
selenium-bdd/
│
├── pom.xml
│
└── src/
    └── test/
        ├── java/
        │   ├── core/
        │   │     DriverFactory.java
        │   │
        │   ├── pages/
        │   │     GoogleTranslatePage.java
        │   │
        │   ├── steps/
        │   │     RegressionSteps.java
        │   │
        │   ├── hooks/
        │   │     Hooks.java
        │   │
        │   └── runner/
        │         RunCucumberTest.java
        │
        └── resources/
            ├── features/
            │     regression.feature
            │
            ├── testdata/
            │     sinhala.png
            │     sinhala.docx
            │
            └── config.properties
```

---

# ⚙️ Setup Instructions

## 1️⃣ Prerequisites

* Java 21 (or 17+)
* Maven 3.8+
* Chrome / Firefox installed
* Internet connection

Verify installation:

```bash
java -version
mvn -version
```

---

## 2️⃣ Clone the Repository

```bash
git clone <your-repository-url>
cd selenium-bdd
```

---

## 3️⃣ Install Dependencies

```bash
mvn clean install
```

---

# 🚀 How to Execute Tests

---

## ▶ Run Full Regression Suite

```bash
mvn clean test -Pregression
```

OR

```bash
mvn clean test -Dcucumber.filter.tags="@regression"
```

---

## ▶ Run Individual Test Case

| Test Case            | Command                    |
| -------------------- | -------------------------- |
| English → Sinhala    | `mvn test -Ptc_eng_si`     |
| English → Tamil      | `mvn test -Ptc_eng_ta`     |
| Swap Languages       | `mvn test -Ptc_swap_en_si` |
| Image Translation    | `mvn test -Ptc_img_si_en`  |
| Document Translation | `mvn test -Ptc_doc_si_en`  |
| Website Translation  | `mvn test -Ptc_web_en_si`  |

Or using tag directly:

```bash
mvn test -Dcucumber.filter.tags="@TC_ENG_SI"
```

---

# 📊 Test Coverage

### ✅ Automated Test Scenarios

1. Translate English sentence to Sinhala
2. Translate English sentence to Tamil
3. Swap EN/SI languages reverses correctly
4. Upload image with Sinhala text and translate to English
5. Upload document with Sinhala text and translate to English
6. Translate a valid English website to Sinhala

---

# 🧩 Configuration

Configuration file:

```
src/test/resources/config.properties
```

Example:

```properties
browser=chrome
headless=false
websiteUrl=https://example.com
```

To run in headless mode:

```bash
mvn test -Dheadless=true
```

---

# 📈 Reporting

After execution, reports are generated under:

```
target/cucumber-report.html
target/cucumber.json
```

Open:

```
target/cucumber-report.html
```

In your browser to view execution summary.

---

# 🛠 Design Decisions

### ✔ Page Object Model (POM)

Separates UI locators and interactions from step definitions.

### ✔ DriverFactory

Centralized WebDriver management using ThreadLocal.

### ✔ Hooks

* Initializes driver before each scenario
* Closes driver after execution
* Can be extended for screenshots on failure

### ✔ Tag-Based Execution

Supports:

* Individual test execution
* Regression suite execution
* Selective test runs

---

# ⚠️ Limitations

* Google Translate UI is dynamic and may change
* Translation results may slightly vary (AI-based)
* Image/document translation depends on Google UI stability
* No backend/API validation (UI-level testing only)

---

# 📌 Future Enhancements

* Parallel execution
* CI/CD integration (GitHub Actions)
* Allure reporting
* Retry mechanism
* Cross-browser matrix execution

---

# 👨‍💻 Author

Developed as part of a QA Automation technical assessment.

---

# 🏁 Final Notes

This framework is intentionally designed to be:

* Simple
* Readable
* Scalable
* Easy to extend

It demonstrates:

* Clean architecture
* Proper separation of concerns
* Maintainable automation structure
* Professional regression suite design

---