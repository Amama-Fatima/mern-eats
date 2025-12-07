pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'markhobson/maven-chrome:latest'
        MAVEN_OPTS = '-Duser.home=$WORKSPACE'
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
                    docker.image("${DOCKER_IMAGE}").inside('--shm-size=2g --ulimit nofile=1024:1024') {
                        dir('selenium-tests') {
                            sh '''
                                # Kill any existing Chrome/chromedriver processes
                                echo "Killing existing Chrome processes..."
                                pkill -9 chrome || true
                                pkill -9 chromedriver || true
                                pkill -9 Google || true
                                
                                # Clean all workspace directories
                                echo "Cleaning workspace directories..."
                                rm -rf $WORKSPACE/.m2 || true
                                rm -rf $WORKSPACE/.wdm || true
                                rm -rf $WORKSPACE/tmp || true
                                rm -rf $WORKSPACE/chrome-profiles || true
                                rm -rf /tmp/.com.google.Chrome* || true
                                rm -rf /tmp/.org.chromium.Chromium* || true
                                
                                # Create fresh directories with proper permissions
                                echo "Creating fresh directories..."
                                mkdir -p $WORKSPACE/.m2/repository
                                mkdir -p $WORKSPACE/.wdm
                                mkdir -p $WORKSPACE/tmp
                                mkdir -p $WORKSPACE/chrome-profiles
                                
                                # Set proper permissions
                                chmod -R 755 $WORKSPACE/.m2
                                chmod -R 755 $WORKSPACE/.wdm
                                chmod -R 755 $WORKSPACE/tmp
                                chmod -R 755 $WORKSPACE/chrome-profiles
                                
                                echo "Maven version:"
                                mvn --version
                                
                                echo "Google Chrome version:"
                                google-chrome --version || true
                                
                                echo "Java version:"
                                java -version
                                
                                echo "Running tests with single thread execution..."
                                # Run tests with single thread and proper cleanup
                                mvn clean test \
                                    -Dheadless=true \
                                    -Dbrowser=chrome \
                                    -DbaseUrl=http://localhost:5173 \
                                    -Dmaven.repo.local=$WORKSPACE/.m2/repository \
                                    -Dwdm.cachePath=$WORKSPACE/.wdm \
                                    -Dwdm.forceDownload=false \
                                    -Djava.io.tmpdir=$WORKSPACE/tmp \
                                    -Dchrome.userDataDir=$WORKSPACE/chrome-profiles \
                                    -Dtestng.thread.count=1 \
                                    -Dsuite.thread.count=1 \
                                    -Ddataproviderthreadcount=1 \
                                    -Dsurefire.threadCount=1 \
                                    -Dparallel=none \
                                    -DforkCount=1 \
                                    -DreuseForks=false \
                                    -DskipTests=false \
                                    -Dmaven.test.failure.ignore=false
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
                    allowMissing: true,
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
            
            // Archive test results (including screenshots if any)
            archiveArtifacts artifacts: 'selenium-tests/target/surefire-reports/**/*', allowEmptyArchive: true
            archiveArtifacts artifacts: 'selenium-tests/screenshots/**/*', allowEmptyArchive: true
            
            // Clean workspace
            cleanWs()
        }
        
        success {
            echo 'All tests passed successfully! ✅'
        }
        
        failure {
            echo 'Tests failed! ❌'
            
            // Additional failure logging
            script {
                sh '''
                    echo "=== FAILURE DETAILS ==="
                    echo "Checking for test reports..."
                    ls -la selenium-tests/target/surefire-reports/ || echo "No test reports found"
                '''
            }
        }
    }
}