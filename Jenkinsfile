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
stage('Kubernetes Tests') {
    steps {
        sh '''
            echo "======================================"
            echo "Starting Kubernetes Test Execution"
            echo "======================================"

            echo "Deleting previous test Jobs..."
            kubectl delete job smoke-test regression-test --ignore-not-found=true

            echo "Starting Smoke Test Job..."
            kubectl apply -f k8s/test-job.yaml

            echo "Starting Regression Test Job..."
            kubectl apply -f k8s/regression-job.yaml

            echo "Waiting for Smoke Test..."
            kubectl wait --for=condition=complete job/smoke-test --timeout=120s

            echo "Waiting for Regression Test..."
            kubectl wait --for=condition=complete job/regression-test --timeout=120s

            echo "======================================"
            echo "SMOKE TEST RESULTS"
            echo "======================================"

            kubectl logs job/smoke-test

            echo "======================================"
            echo "REGRESSION TEST RESULTS"
            echo "======================================"

            kubectl logs job/regression-test

            echo "======================================"
            echo "KUBERNETES TESTS COMPLETED"
            echo "======================================"
        '''
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
        stage('Deploy to Kubernetes') {
            steps {
                sh '''
                    echo "======================================"
                    echo "Deploying application to Kubernetes"
                    echo "======================================"

                    echo "Updating Kubernetes Deployment image..."

                    kubectl set image deployment/cicd-app \
                        cicd-app=jayshreekharate/cicd-sdet-demo:${BUILD_NUMBER}

                    echo "Waiting for rollout..."

                    kubectl rollout status deployment/cicd-app --timeout=120s

                    echo "======================================"
                    echo "Deployment successful"
                    echo "======================================"

                    kubectl get deployment cicd-app
                    kubectl get pods -l app=cicd-app
                    kubectl get service cicd-app
                '''
            }
        }
                // ==========================================
        // 6. VERIFY DEPLOYMENT
        // ==========================================
stage('Verify Application') {
    steps {
        sh '''
            echo "======================================"
            echo "Verifying Kubernetes Deployment"
            echo "======================================"

            echo "Checking pods..."
            kubectl get pods -l app=cicd-app

            echo "Checking service..."
            kubectl get service cicd-app

            echo "Testing application from inside Kubernetes..."

            kubectl run api-verification \
                --rm \
                --restart=Never \
                --image=curlimages/curl:8.10.1 \
                -- \
                curl -f http://cicd-app:8081/hello

            echo "======================================"
            echo "APPLICATION VERIFICATION SUCCESS"
            echo "======================================"
        '''
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
            echo '=================================='
            echo 'PIPELINE FAILED'
            echo 'Check the stage that failed'
            echo '======================================'
        }
    }
}