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
                    docker.image("${DOCKER_IMAGE}").inside('--shm-size=2g') {
                        dir('selenium-tests') {
                            sh '''
                                # Create Maven local repository directory
                                mkdir -p $WORKSPACE/.m2/repository
                                
                                # Create WebDriverManager cache directory
                                mkdir -p $WORKSPACE/.wdm
                                
                                # Create temp directory for WebDriverManager
                                mkdir -p $WORKSPACE/tmp
                                
                                echo "Maven version:"
                                mvn --version
                                
                                echo "Google Chrome version:"
                                google-chrome --version || true
                                
                                echo "Running tests..."
                                mvn clean test \
                                    -Dheadless=true \
                                    -Dmaven.repo.local=$WORKSPACE/.m2/repository \
                                    -Dwdm.cachePath=$WORKSPACE/.wdm \
                                    -Dwdm.forceDownload=false \
                                    -Djava.io.tmpdir=$WORKSPACE/tmp
                            '''
                        }
                    }
                }
            }
        }
        
        stage('Publish Test Reports') {
            steps {
                echo 'Publishing test reports...'
                
                step([
                    $class: 'Publisher',
                    reportFilenamePattern: 'selenium-tests/target/surefire-reports/testng-results.xml'
                ])
                
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
            
            archiveArtifacts artifacts: 'selenium-tests/target/surefire-reports/**/*', allowEmptyArchive: true
            
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