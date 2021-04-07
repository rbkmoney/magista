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

    javaServicePipeline(serviceName, "c0612d6052ac049496b72a23a04acb142035f249", "92f43db084bae837f3f39ca3318a7aa02c6f6270", null, mvnArgs)
}
