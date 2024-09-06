# SeleniumDemo

This is a Selenium automation project that demonstrates the usage of Selenium WebDriver with Java and Maven.

## Project Structure

- `pom.xml`: Contains project dependencies and Maven compiler configurations.
- `src/test/java/org/example/Main.java`: Contains the main class that runs the Selenium tests.

## Dependencies

- Selenium WebDriver
- WebDriverManager
- ExtentReports
- JUnit

## Setup

1. Clone the repository.
2. Open the project in IntelliJ IDEA 2022.3.3 or any other IDE that supports Maven projects.
3. Run the `Main.java` file to execute the tests.

## Features

This project includes tests for the following functionalities:

- Login
- Setting default billing address
- Adding items to cart
- Proceeding to checkout
- Printing the order

## Reporting

The project uses ExtentReports for reporting. The report is generated in the `reports` directory in the project root.

## Note

Make sure to update the WebDriverManager setup in the `Main.java` file to match your browser version.