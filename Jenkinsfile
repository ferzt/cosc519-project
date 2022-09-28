pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                bat 'mvn package'
            }
        }
        stage('2') {
            steps {
                catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                    bat "exit 1"
                }
            }
        }
        stage('3') {
            steps {
                bat 'exit 0'
            }
        }
    }
}
