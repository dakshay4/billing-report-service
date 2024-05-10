def VERSION
pipeline {
    agent {
        kubernetes {
            cloud 'Facets-k8s'
            yaml """
                apiVersion: v1
                kind: Pod
                metadata:
                  name: k8s-java
                  labels:
                    app: k8s-java
                spec:
                  containers:
                  - name: k8s-java
                    image: 050879484863.dkr.ecr.ap-south-1.amazonaws.com/mis-java-17:mvn3.5
                    imagePullPolicy: Always
                    command:
                    - cat
                    tty: true
                    resources:
                      requests:
                        memory: "1Gi"
                        cpu: "600m"
                      limits:
                        memory: "2000Mi"
                        cpu: "800m"
            """
        }
    }
    stages {
        stage('SCM') {
            steps {
                container('k8s-java') {
                    script {
                            checkout scm
                        }
                    }
                }
            }
        stage('Set Version'){
            steps {
                script {
                    env.HF_NUMBER = "${BUILD_NUMBER}"
                }
            }
        }
        stage('Build') {
            steps {
                container('k8s-java') {
                    sh """
                        echo ${env.HF_NUMBER}
                        cd $WORKSPACE && mvn clean package -U -Dmaven.test.skip=true -s /opt/maven_settings/settings.xml
                    """
                    sh """
                       VERSION=\$(ls $WORKSPACE/target/*.jar | awk -F '/' '{print \$NF}'  | awk -F 'billingreportservice-' '{print \$NF}' | awk -F '.jar' '{print \$1}')
                       echo \${VERSION} > jar_version.txt
                    """
                    script{
                        VERSION = readFile('jar_version.txt').trim()
                    }
                }
            }
        }
        stage('Run Sonar Scanner') {
            steps {
                container('k8s-java') {
                    script {
                        def scannerHome = tool name: 'sonarqube', type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                        // Define the keys
                        def prKey = "-Dsonar.pullrequest.key=${env.CHANGE_ID}"
                        def prBranch = "-Dsonar.pullrequest.branch=${env.CHANGE_BRANCH}"
                        def prBase = "-Dsonar.pullrequest.base=${env.CHANGE_TARGET}"
                        withSonarQubeEnv("new-sonarqube") {
                            sh """
                                ${scannerHome}/bin/sonar-scanner \
                                     -Dsonar.projectName=billing-report-service -Dsonar.projectKey=billing-report-service -bitbucket-pr-analysis ${prKey} ${prBranch} ${prBase} -Dsonar.java.binaries=target/classes -Dsonar.sources=src -Dsonar.java.libraries=/root/.m2/repository/org/projectlombok/lombok/*/*.jar -Dsonar.projectVersion=${VERSION}
                                """
                        }
                    }
                }
            }
        }
        stage('Sleep'){
            steps {
                sleep 1000
            }
        }
    }
}