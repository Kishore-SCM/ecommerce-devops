pipeline {
    agent any

    environment {
        AWS_REGION       = 'ap-south-1'
        AWS_ACCOUNT_ID   = credentials('aws-account-id')
        ECR_REGISTRY     = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        SONAR_TOKEN      = credentials('sonar-token')
        SLACK_WEBHOOK    = credentials('slack-webhook')
        APP_DOMAIN       = 'api.ecommerce.example.com'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 45, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                }
            }
        }

        stage('Maven Test') {
            steps {
                sh 'mvn clean test -pl product-service,order-service,user-service,api-gateway -am'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco(execPattern: '**/target/jacoco.exec')
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh '''
                        mvn sonar:sonar \
                          -Dsonar.projectKey=ecommerce-platform \
                          -Dsonar.token=${SONAR_TOKEN}
                    '''
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Maven Build') {
            steps {
                sh 'mvn package -DskipTests -pl product-service,order-service,user-service,api-gateway,notification-service -am'
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def services = ['api-gateway','product-service','order-service','user-service','notification-service','frontend']
                    sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REGISTRY}"
                    services.each { svc ->
                        sh """
                            docker build -f ${svc}/Dockerfile -t ${ECR_REGISTRY}/${svc}:${GIT_COMMIT_SHORT} .
                            docker push ${ECR_REGISTRY}/${svc}:${GIT_COMMIT_SHORT}
                            docker tag  ${ECR_REGISTRY}/${svc}:${GIT_COMMIT_SHORT} ${ECR_REGISTRY}/${svc}:latest
                            docker push ${ECR_REGISTRY}/${svc}:latest
                        """
                    }
                }
            }
        }

        stage('Trivy Security Scan') {
            steps {
                sh "trivy image --exit-code 1 --severity HIGH,CRITICAL ${ECR_REGISTRY}/product-service:${GIT_COMMIT_SHORT}"
            }
        }

        stage('Terraform Plan') {
            when { branch 'main' }
            steps {
                dir('infrastructure/terraform') {
                    sh 'terraform init -backend-config=backend.hcl'
                    sh 'terraform plan -out=tfplan'
                }
            }
        }

        stage('Terraform Apply') {
            when { branch 'main' }
            input { message "Apply infrastructure changes to production?" }
            steps {
                dir('infrastructure/terraform') {
                    sh 'terraform apply tfplan'
                }
            }
        }

        stage('Helm Deploy') {
            when { branch 'main' }
            steps {
                withCredentials([file(credentialsId: 'eks-kubeconfig', variable: 'KUBECONFIG')]) {
                    sh """
                        helm upgrade --install ecommerce ./helm/ecommerce \
                          --namespace production \
                          --create-namespace \
                          --set global.imageTag=${GIT_COMMIT_SHORT} \
                          --set global.registry=${ECR_REGISTRY} \
                          --atomic --timeout 10m --wait
                    """
                }
            }
        }

        stage('Smoke Test') {
            when { branch 'main' }
            steps {
                sh '''
                    sleep 30
                    STATUS=$(curl -s -o /dev/null -w "%{http_code}" https://${APP_DOMAIN}/actuator/health)
                    [ "${STATUS}" = "200" ] || (echo "Health check FAILED: ${STATUS}" && exit 1)
                '''
            }
        }
    }

    post {
        success {
            slackSend(color: 'good', message: "*Deploy SUCCESS* | ${env.BRANCH_NAME} @ ${GIT_COMMIT_SHORT} | Build #${env.BUILD_NUMBER}")
        }
        failure {
            slackSend(color: 'danger', message: "*Deploy FAILED* | ${env.BRANCH_NAME} @ ${GIT_COMMIT_SHORT} | Build #${env.BUILD_NUMBER}")
        }
        always {
            cleanWs()
        }
    }
}
