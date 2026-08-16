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
        // 3. DOCKER BUILD
        // ==========================================
        stage('Docker Build') {

            steps {

                sh '''
                    echo "======================================"
                    echo "Building Docker image"
                    echo "======================================"

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
        // 4. DOCKER PUSH
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
                        echo "======================================"
                        echo "Logging into Docker Hub"
                        echo "======================================"

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
                    echo "======================================"
                    echo "Deploying application"
                    echo "======================================"


                    echo "Pulling latest image..."

                    docker pull \
                        jayshreekharate/cicd-sdet-demo:latest


                    echo "Removing old container..."

                    docker rm -f cicd-app 2>/dev/null || true


                    echo "Starting new application container..."

                    docker run -d \
                        --name cicd-app \
                        --network cicd-network \
                        jayshreekharate/cicd-sdet-demo:latest


                    echo "Container started."


                    echo "Container status:"

                    docker ps \
                        --filter name=cicd-app
                '''
            }
        }


        // ==========================================
        // 6. VERIFY DEPLOYMENT
        // ==========================================
        stage('Verify Deployment') {

            steps {

                sh '''
                    echo "======================================"
                    echo "Verifying application"
                    echo "======================================"


                    echo "Waiting for Spring Boot application..."


                    for i in $(seq 1 30); do

                        echo "Attempt $i..."


                        if curl -fs http://cicd-app:8081/hello; then

                            echo ""
                            echo "======================================"
                            echo "Application is UP!"
                            echo "Deployment verification SUCCESS!"
                            echo "======================================"

                            exit 0
                        fi


                        sleep 2

                    done


                    echo "======================================"
                    echo "APPLICATION FAILED TO START"
                    echo "======================================"


                    echo "Container status:"

                    docker ps -a \
                        --filter name=cicd-app


                    echo "Application logs:"

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
            echo '======================================'
            echo 'CI/CD pipeline completed'
            echo '======================================'
        }

        success {
            echo '======================================'
            echo 'SUCCESS!'
            echo 'Build + Tests + Docker + Deployment + Verification passed'
            echo '======================================'
        }

        failure {
            echo '======================================'
            echo 'PIPELINE FAILED'
            echo 'Check the stage that failed'
            echo '======================================'
        }
    }
}