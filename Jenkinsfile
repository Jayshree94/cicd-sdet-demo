pipeline {

    agent any

    tools {
        maven 'Maven-3.9.9'
    }

    stages {

        // ==========================================
        // 1. BUILD
        // ==========================================
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        // ==========================================
        // 2. RUN TESTS IN PARALLEL
        // ==========================================
        stage('Automated Tests') {

            parallel {

                stage('Smoke Tests') {
                    steps {
                        sh 'mvn clean test -Dgroups=smoke'
                    }
                }

                stage('Regression Tests') {
                    steps {
                        sh 'mvn test -Dgroups=regression'
                    }
                }
            }
        }

        // ==========================================
        // 3. BUILD DOCKER IMAGE
        // ==========================================
        stage('Docker Build') {
            steps {
                sh '''
                    docker build \
                        -t jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER} \
                        .

                    docker tag \
                        jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER} \
                        jayshreekharate/cicd-sdet-demo:latest
                '''
            }
        }

        // ==========================================
        // 4. PUSH IMAGE TO DOCKER HUB
        // ==========================================
        stage('Docker Push') {
            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-creds',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {

                    sh '''
                        echo "$DOCKER_PASSWORD" | \
                            docker login \
                            -u "$DOCKER_USERNAME" \
                            --password-stdin

                        docker push \
                            jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER}

                        docker push \
                            jayshreekharate/cicd-sdet-demo:latest

                        docker logout
                    '''
                }
            }
        }

        // ==========================================
        // 5. DEPLOY
        // ==========================================
        stage('Deploy') {
            steps {
                sh '''
                    echo "Pulling latest image..."

                    docker pull \
                        jayshreekharate/cicd-sdet-demo:latest

                    echo "Stopping old application container..."

                    docker rm -f cicd-app 2>/dev/null || true

                    echo "Starting new application container..."

                    docker run -d \
                        --name cicd-app \
                        -p 8081:8081 \
                        jayshreekharate/cicd-sdet-demo:latest

                    echo "Application container started."
                '''
            }
        }

        // ==========================================
        // 6. VERIFY DEPLOYMENT
        // ==========================================
        stage('Verify Deployment') {
            steps {
                sh '''
                    echo "Waiting for application to start..."

                    sleep 5

                    echo "Checking application..."

                    curl -f http://localhost:8081/hello

                    echo ""
                    echo "Deployment verification successful!"
                '''
            }
        }
    }

    // ==========================================
    // POST ACTIONS
    // ==========================================
    post {

        always {
            echo 'CI/CD pipeline completed'
        }

        success {
            echo 'Build, testts, Docker push and deployment successful!'
        }

        failure {
            echo 'CI/CD pipeline failed!'
        }
    }
}