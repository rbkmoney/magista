#!groovy
build('magista', 'java-maven') {
    checkoutRepo()
    loadBuildUtils()

    def javaServicePipeline
    runStage('load JavaService pipeline') {
        javaServicePipeline = load("build_utils/jenkins_lib/pipeJavaServiceInsideDocker.groovy")
    }

    def serviceName = env.REPO_NAME
    def mvnArgs = '-DjvmArgs="-Xmx256m"'
    //todo update serviceImage DON'T APPROVE IT
    def serviceImage = "c0612d6052ac049496b72a23a04acb142035f249"
    def buildImage = "e7aa5e079baeee7bdecdae2134b5602972c40b59"

    javaServicePipeline(serviceName, serviceImage, buildImage, mvnArgs)
}
