pipeline {
    agent {
        dockerfile {
            args '-v ${HOME}/bin:${HOME}/bin'
            additionalBuildArgs '--build-arg BUILDER_UID=$(id -u)'
        }
    }
    stages {
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
