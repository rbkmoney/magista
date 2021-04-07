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
    def buildImage = "cbcce8565359cf40ee548c90fe42387ad066c01a"
    def databaseHostName = "localhost"

    javaServicePipeline(serviceName, "c0612d6052ac049496b72a23a04acb142035f249", buildImage, databaseHostName, mvnArgs)
}
