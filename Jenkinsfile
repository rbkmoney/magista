#!groovy

def finalHook = {
    archive 'target/surefire-reports/'
}

build('magista', 'docker-host', finalHook) {
    checkoutRepo()
    loadBuildUtils()

    def pipeDefault
    runStage('load pipeline') {
        env.JENKINS_LIB = "build_utils/jenkins_lib"
        pipeDefault = load("${env.JENKINS_LIB}/pipeDefault.groovy")
    }

    pipeDefault() {

        if (env.BRANCH_NAME == 'master') {
            runStage('release') {
                withCredentials([[$class: 'FileBinding', credentialsId: 'java-maven-settings.xml', variable: 'SETTINGS_XML']]) {
                    sh 'make wc_release ${SETTINGS_XML}'
            }
        }
        runStage('build image') {
            sh "make build_image"
        }
        runStage('push image') {
            sh "make push_image"
        }
        } else {
            runStage('package') {
                withCredentials([[$class: 'FileBinding', credentialsId: 'java-maven-settings.xml', variable: 'SETTINGS_XML']]) {
                    sh 'make wc_package ${SETTINGS_XML}'
                }
            }
        }

    }
}
