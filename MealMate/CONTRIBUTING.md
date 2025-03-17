# Contributing to MealMate

First off, thank you for considering contributing to MealMate! It's people like you that make MealMate such a great tool.

## Code of Conduct

This project and everyone participating in it is governed by the MealMate Code of Conduct. By participating, you are expected to uphold this code.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the issue list as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

* Use a clear and descriptive title
* Describe the exact steps which reproduce the problem
* Provide specific examples to demonstrate the steps
* Describe the behavior you observed after following the steps
* Explain which behavior you expected to see instead and why
* Include screenshots if possible
* Include your Android version and device model

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* Use a clear and descriptive title
* Provide a step-by-step description of the suggested enhancement
* Provide specific examples to demonstrate the steps
* Describe the current behavior and explain which behavior you expected to see instead
* Explain why this enhancement would be useful
* List some other applications where this enhancement exists, if applicable

### Pull Requests

* Fill in the required template
* Do not include issue numbers in the PR title
* Follow the Android style guide
* Include screenshots in your pull request whenever possible
* Update the README.md with details of changes if applicable
* Update the CHANGELOG.md with a note describing your changes

## Style Guide

### Git Commit Messages

* Use the present tense ("Add feature" not "Added feature")
* Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
* Limit the first line to 72 characters or less
* Reference issues and pull requests liberally after the first line

### Kotlin Style Guide

* Follow the official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
* Use meaningful variable and function names
* Keep functions small and focused
* Document complex code sections
* Write unit tests for new code

### Android Guidelines

* Follow Material Design guidelines
* Support different screen sizes
* Handle configuration changes appropriately
* Consider accessibility
* Optimize performance
* Follow Android best practices

## Development Process

1. Fork the repo and create your branch from `main`
2. If you've added code that should be tested, add tests
3. If you've changed APIs, update the documentation
4. Ensure the test suite passes
5. Make sure your code lints
6. Issue that pull request

## Setting Up Your Development Environment

1. Install Android Studio
2. Clone the repository
3. Open the project in Android Studio
4. Sync the project with Gradle files
5. Run the app on an emulator or physical device

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/mealmate/
│   │   │   ├── app/
│   │   │   │   ├── ui/
│   │   │   │   │   ├── meals/
│   │   │   │   │   ├── shopping/
│   │   │   │   │   └── profile/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── LoginActivity.kt
│   │   │   │   └── HomeActivity.kt
│   │   │   └── data/
│   │   │       ├── model/
│   │   │       ├── dao/
│   │   │       └── MealMateDatabase.kt
│   │   └── res/
│   └── test/
└── build.gradle
```

## Testing

* Write unit tests for all new code
* Run existing tests before submitting PR
* Include both positive and negative test cases
* Mock external dependencies
* Test edge cases

## Documentation

* Update README.md if you change functionality
* Comment your code where necessary
* Update API documentation
* Include helpful comments in your PR

## Questions?

Feel free to open an issue with the tag `question` if you have any questions about contributing.

Thank you for contributing to MealMate!
