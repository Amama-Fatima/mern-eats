pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'markhobson/maven-chrome:latest'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code from GitHub...'
                checkout scm
            }
        }
        
        stage('Pull Docker Image') {
            steps {
                echo 'Pulling Maven Chrome Docker image...'
                script {
                    docker.image("${DOCKER_IMAGE}").pull()
                }
            }
        }
        
        stage('Run Selenium Tests') {
            steps {
                echo 'Running Selenium tests in Docker container...'
                script {
                    docker.image("${DOCKER_IMAGE}").inside('--shm-size=2g -e MAVEN_OPTS="-Dmaven.repo.local=/var/lib/jenkins/workspace/.m2/repository"') {
                        dir('selenium-tests') {
                            sh '''
                                echo "Maven version:"
                                mvn --version
                                
                                echo "Google Chrome version:"
                                google-chrome --version || true
                                
                                echo "Running tests..."
                                mvn clean test -Dheadless=true -Dmaven.repo.local=/var/lib/jenkins/workspace/.m2/repository
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Publish Test Reports') {
            steps {
                echo 'Publishing test reports...'
                
                // Publish TestNG results
                step([
                    $class: 'Publisher',
                    reportFilenamePattern: 'selenium-tests/target/surefire-reports/testng-results.xml'
                ])
                
                // Publish HTML reports
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'selenium-tests/target/surefire-reports',
                    reportFiles: 'index.html',
                    reportName: 'Test Results',
                    reportTitles: 'Selenium Test Results'
                ])
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline execution completed.'
            
            // Archive test results
            archiveArtifacts artifacts: 'selenium-tests/target/surefire-reports/**/*', allowEmptyArchive: true
            
            // Clean workspace
            cleanWs()
        }
        
        success {
            echo 'All tests passed successfully! ✅'
        }
        
        failure {
            echo 'Tests failed! ❌'
        }
    }
}
