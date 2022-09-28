pipeline {
    agent any

    stages {
        stage('1') {
            steps {
                bat 'exit 0'
            }
        }
        stage('2') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
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
