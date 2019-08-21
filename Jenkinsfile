pipeline {
    agent {
        dockerfile {
            args '-v ${HOME}/.m2:/home/builder/.m2'
            additionalBuildArgs '--build-arg BUILDER_UID=${JENKINS_UID:-9999}'
        }
    }
    stages {
        stage('set_version_build') {
            when { not { branch "master" } }
            steps {
                sh './bumpversion.sh build'
            }
        }
        stage('set_version_release') {
            when { branch "master" }
            steps {
                sh './bumpversion.sh release'
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
            dir('dist/') {
                archiveArtifacts artifacts: 'directory-build/target/*.zip', fingerprint: true, onlyIfSuccessful: true
            }
        }
    }
}