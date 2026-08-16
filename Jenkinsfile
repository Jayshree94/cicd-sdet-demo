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
        // 2. AUTOMATED TESTS
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
                    echo "Building Docker image..."

                    docker build \
                        -t jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER} \
                        .

                    echo "Creating latest tag..."

                    docker tag \
                        jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER} \
                        jayshreekharate/cicd-sdet-demo:latest
                '''
            }
        }


        // ==========================================
        // 4. PUSH TO DOCKER HUB
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
                        echo "Logging into Docker Hub..."

                        echo "$DOCKER_PASSWORD" | \
                            docker login \
                            -u "$DOCKER_USERNAME" \
                            --password-stdin

                        echo "Pushing build image..."

                        docker push \
                            jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER}

                        echo "Pushing latest image..."

                        docker push \
                            jayshreekharate/cicd-sdet-demo:latest

                        echo "Logging out..."

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
                    echo "Pulling latest application image..."

                    docker pull \
                        jayshreekharate/cicd-sdet-demo:latest


                    echo "Removing old application container..."

                    docker rm -f cicd-app 2>/dev/null || true


                    echo "Starting new application container..."

                    docker run -d \
                        --name cicd-app \
                        --network cicd-network \
                        -p 8081:8081 \
                        jayshreekharate/cicd-sdet-demo:latest


                    echo "Application container started."

                    docker ps --filter name=cicd-app
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


                    for i in $(seq 1 30); do

                        echo "Attempt $i..."

                        if curl -fs http://cicd-app:8081/hello; then

                            echo ""
                            echo "======================================"
                            echo "Application is UP!"
                            echo "Deployment verification successful!"
                            echo "======================================"

                            exit 0
                        fi

                        sleep 2

                    done


                    echo "======================================"
                    echo "Application failed to start"
                    echo "======================================"

                    docker logs cicd-app

                    exit 1
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
            echo 'Build, tests, Docker push and deployment successful!'
        }

        failure {
            echo 'CI/CD pipeline failed!'
        }
    }
}