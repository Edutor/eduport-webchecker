package dk.edutor.eduport.webchecker

import dk.edutor.eduport.*

class WebChecker : Port {

    override fun check(solution: Solution): WebAssessment {
        val launcher = Launcher();
        val ws: WebSolution = solution as WebSolution;

        launcher.check(ws.url, ws.challenge.id)

        println("Tests: " + launcher.tests.size)
        println("Successful: " + launcher.successful)
        println("Failed: " + launcher.failed)
        println("Percentage: " + launcher.successfulPercentage)

        for ((key, value) in launcher.tests) {
            println("Test - Name: " + key + " Status: " + value.status + " Message: " + value.message)
        }

        val grade : Double = (12.0 / 100) * launcher.successfulPercentage

        return WebAssessment(ws, grade, launcher.tests.size, launcher.successful, launcher.failed, launcher.successfulPercentage, launcher.tests)
    }

    override fun sayHello(text: String) = ""
}