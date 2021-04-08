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
    def serviceImage = "c0612d6052ac049496b72a23a04acb142035f249"
    def buildImage = "697fb14e7aba79a3297f035a80de7e4362f47a34"
    def databaseHostName = "localhost"

    javaServicePipeline(serviceName, serviceImage, buildImage, databaseHostName, mvnArgs)
}
