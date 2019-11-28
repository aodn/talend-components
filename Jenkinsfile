pipeline {
    agent {
        dockerfile {
            args '-v ${HOME}/bin:${HOME}/bin'
            additionalBuildArgs '--build-arg BUILDER_UID=$(id -u)'
        }
    }
    stages {
        stage('clean') {
            steps {
                sh 'git reset --hard'
                sh 'git clean -xffd'
            }
        }
        stage('package') {
            steps {
                sh 'mvn clean test install'
            }
        }
    }
    post {
        success {
            dir('directory-build/target/') {
                archiveArtifacts artifacts: '*.zip', fingerprint: true, onlyIfSuccessful: true
            }
        }
    }
}
