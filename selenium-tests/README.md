# MERN Eats - Selenium Test Suite (Java)

Comprehensive automated test suite for MERN Eats application using Selenium WebDriver, Java 21, Maven, and TestNG.

## 📋 Test Suite Overview

### **22 Automated Test Cases**

#### Authentication Tests (7 tests)

1. **test01_homepageLoadsSuccessfully** - Verify home page loads
2. **test02_navigateToRegisterPage** - Navigate to registration
3. **test03_registerNewUser** - Register new user account
4. **test04_registerWithInvalidEmail** - Test email validation
5. **test05_navigateToLoginPage** - Navigate to login
6. **test06_loginWithValidCredentials** - Login with valid credentials
7. **test07_loginWithInvalidCredentials** - Test login error handling

#### Restaurant Search Tests (6 tests)

8. **test08_searchRestaurantByCity** - Search by city
9. **test09_viewSearchResults** - View search results
10. **test10_filterRestaurantsByCuisine** - Apply cuisine filters
11. **test11_sortSearchResults** - Sort restaurant listings
12. **test12_viewRestaurantDetails** - View restaurant details
13. **test13_paginationNavigation** - Navigate pagination

#### User Profile Tests (3 tests)

14. **test14_accessUserProfilePage** - Access user profile
15. **test15_viewUserProfileInformation** - View profile data
16. **test16_updateUserProfile** - Update profile information

#### Restaurant Management Tests (6 tests)

17. **test17_accessManageRestaurantPage** - Access restaurant management
18. **test18_viewRestaurantForm** - View restaurant form
19. **test19_fillRestaurantDetails** - Fill restaurant details
20. **test20_selectRestaurantCuisines** - Select cuisine types
21. **test21_addMenuItem** - Add menu items
22. **test22_uploadRestaurantImage** - Upload restaurant image

## 🚀 Prerequisites

- **Java 21** (OpenJDK 21.0.9 or later) ✅
- **Maven 3.6+** (for dependency management)
- **Google Chrome** browser installed
- **Git** (optional, for version control)

## 📦 Installation & Setup

### 1. Verify Java Installation

```powershell
java --version
# Should show: openjdk 21.0.9 or later
```

### 2. Install Maven (if not installed)

Download from: https://maven.apache.org/download.cgi

Or use Chocolatey:

```powershell
choco install maven
```

Verify installation:

```powershell
mvn --version
```

### 3. Navigate to Test Directory

```powershell
cd selenium-tests
```

### 4. Install Dependencies

```powershell
mvn clean install -DskipTests
```

This will download all required dependencies:

- Selenium WebDriver 4.27.0
- TestNG 7.10.2
- WebDriverManager 5.9.2
- ExtentReports 5.1.2
- Log4j 2.23.1

### 5. Configure Test Settings

Edit `src/test/resources/config.properties`:

```properties
baseUrl=http://localhost:5173
apiUrl=http://localhost:7000
headless=true
testUserEmail=testuser@example.com
testUserPassword=TestPassword123!
```

## 🧪 Running Tests

### Run All Tests

```powershell
mvn test
```

### Run in Headed Mode (see browser)

```powershell
mvn test -Dheadless=false
```

### Run in Headless Mode (for CI/CD)

```powershell
mvn test -Dheadless=true
```

### Run Specific Test Class

```powershell
# Run only authentication tests
mvn test -Dtest=AuthenticationTests

# Run only search tests
mvn test -Dtest=RestaurantSearchTests

# Run only profile tests
mvn test -Dtest=UserProfileTests

# Run only restaurant management tests
mvn test -Dtest=RestaurantManagementTests
```

### Run Specific Test Method

```powershell
mvn test -Dtest=AuthenticationTests#test01_homepageLoadsSuccessfully
```

## 📊 Test Reports

After running tests, reports are generated in:

- **TestNG HTML Report**: `target/surefire-reports/index.html`
- **TestNG XML Report**: `target/surefire-reports/testng-results.xml`
- **Console Output**: Detailed logs in terminal

Open HTML report:

```powershell
start target/surefire-reports/index.html
```

## 📸 Screenshots

Failed test screenshots are automatically saved to:

```
screenshots/FAILURE_testName_timestamp.png
```

## 🏗️ Project Structure

```
selenium-tests/
├── pom.xml                                 # Maven configuration
├── src/
│   ├── main/
│   │   └── java/com/merneats/
│   │       ├── config/
│   │       │   └── ConfigManager.java     # Configuration management
│   │       ├── utils/
│   │       │   ├── DriverManager.java     # WebDriver management
│   │       │   └── BaseTest.java          # Base test class
│   │       └── pages/                      # Page Object Model (future)
│   └── test/
│       ├── java/com/merneats/tests/
│       │   ├── AuthenticationTests.java
│       │   ├── RestaurantSearchTests.java
│       │   ├── UserProfileTests.java
│       │   └── RestaurantManagementTests.java
│       └── resources/
│           ├── config.properties           # Test configuration
│           └── testng.xml                  # TestNG suite configuration
├── screenshots/                             # Auto-generated screenshots
└── test-output/                            # TestNG output directory
```

## 🐳 Running with Docker

If your application is containerized:

```powershell
# Start application
docker-compose up -d

# Wait for services
Start-Sleep -Seconds 10

# Run tests
cd selenium-tests
mvn test
```

## ☁️ Jenkins Integration

### Jenkinsfile Example

```groovy
pipeline {
    agent any

    tools {
        maven 'Maven 3.9'
        jdk 'JDK 21'
    }

    stages {
        stage('Checkout') {
            steps {
                git 'your-repository-url'
            }
        }

        stage('Build') {
            steps {
                dir('selenium-tests') {
                    sh 'mvn clean compile'
                }
            }
        }

        stage('Run Tests') {
            steps {
                dir('selenium-tests') {
                    sh 'mvn test -Dheadless=true'
                }
            }
        }

        stage('Publish Reports') {
            steps {
                publishHTML([
                    reportDir: 'selenium-tests/target/surefire-reports',
                    reportFiles: 'index.html',
                    reportName: 'Test Report'
                ])
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'selenium-tests/screenshots/*.png', allowEmptyArchive: true
            junit 'selenium-tests/target/surefire-reports/*.xml'
        }
    }
}
```

## 🔧 Troubleshooting

### Issue: "ChromeDriver not found"

**Solution**: WebDriverManager automatically downloads ChromeDriver. Ensure internet connection on first run.

### Issue: "Element not found"

**Solution**: Increase wait times in `config.properties`:

```properties
implicitWait=15
explicitWait=20
```

### Issue: Tests fail in headless mode

**Solution**: Run in headed mode to debug:

```powershell
mvn test -Dheadless=false
```

### Issue: Connection refused

**Solution**: Ensure frontend (port 5173) and backend (port 7000) are running:

```powershell
# Terminal 1: Start backend
cd backend-mern-eats
npm run dev

# Terminal 2: Start frontend
cd frontend-mern-eats
npm run dev

# Terminal 3: Run tests
cd selenium-tests
mvn test
```

## 🎯 Best Practices

1. **Always run application before tests**
2. **Use headless mode for CI/CD**
3. **Check screenshots when tests fail**
4. **Update wait times if application is slow**
5. **Run specific tests during development**
6. **Run full suite before deployment**

## 📚 Technologies Used

- **Java 21** - Programming language
- **Selenium WebDriver 4.27.0** - Browser automation
- **TestNG 7.10.2** - Testing framework
- **Maven** - Build & dependency management
- **WebDriverManager 5.9.2** - Automatic driver management
- **Chrome (Headless)** - Test browser

## 🔄 Continuous Integration

### GitHub Actions Example

```yaml
name: Selenium Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest

    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: "21"
          distribution: "temurin"

      - name: Run tests
        run: |
          cd selenium-tests
          mvn clean test -Dheadless=true

      - name: Upload screenshots
        if: failure()
        uses: actions/upload-artifact@v3
        with:
          name: screenshots
          path: selenium-tests/screenshots/
```

## 📝 Notes

- Tests are designed to be **idempotent** (can run multiple times)
- **Headless Chrome** is used by default for AWS EC2/CI-CD compatibility
- Tests include **automatic screenshots** on failure
- **WebDriverManager** handles ChromeDriver version automatically
- All tests are **independent** and can run in any order

## 👥 Support

For issues or questions:

1. Check test output and screenshots
2. Review configuration in `config.properties`
3. Verify application is running
4. Check Java and Maven versions

---

**Created for**: MERN Eats Application  
**Framework**: Selenium + Java + TestNG + Maven  
**Compatible with**: Java 21, AWS EC2, Jenkins CI/CD
