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
    def buildImage = "c4894d8c1f32255cb83cfd82c96183ff6a61d351"

    javaServicePipeline(serviceName, serviceImage, buildImage, mvnArgs)
}
