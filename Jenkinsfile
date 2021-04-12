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
    def buildImage = "d73d2150ffe13f206bbe4e047d99e1ff799e6f78"

    javaServicePipeline(serviceName, serviceImage, buildImage, mvnArgs)
}
