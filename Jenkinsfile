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
                                # Clean workspace
                                rm -rf $WORKSPACE/.m2 || true
                                rm -rf $WORKSPACE/.wdm || true
                                rm -rf $WORKSPACE/tmp || true
                                
                                # Create fresh directories
                                mkdir -p $WORKSPACE/.m2/repository
                                mkdir -p $WORKSPACE/.wdm
                                mkdir -p $WORKSPACE/tmp
                                
                                echo "Running tests..."
                                mvn clean test \
                                    -Dheadless=true \
                                    -Dbrowser=chrome \
                                    -DbaseUrl=http://localhost:5173 \
                                    -Dmaven.repo.local=$WORKSPACE/.m2/repository \
                                    -Dwdm.cachePath=$WORKSPACE/.wdm \
                                    -Djava.io.tmpdir=$WORKSPACE/tmp \
                                    -Dtestng.dtd.http=true
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
            
            // Archive test results
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
        }
    }
}