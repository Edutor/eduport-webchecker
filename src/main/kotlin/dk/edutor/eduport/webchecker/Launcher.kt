package dk.edutor.eduport.webchecker

import dk.edutor.eduport.LauncherTestResult
import dk.edutor.eduport.webchecker.challenges.WebChallenge2
import org.junit.platform.engine.discovery.ClassNameFilter.includeClassNamePatterns
import org.junit.platform.engine.discovery.DiscoverySelectors.selectPackage
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.discovery.DiscoverySelectors.selectClass
import org.junit.platform.launcher.core.LauncherFactory
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import org.junit.platform.launcher.TestExecutionListener
import org.junit.platform.launcher.TestIdentifier
import org.junit.platform.launcher.TestPlan
import org.junit.platform.launcher.listeners.SummaryGeneratingListener

class Launcher {
    var successfulPercentage : Double = 0.0;
    var successful = 0;
    var failed = 0;
    var tests = mutableMapOf<String, LauncherTestResult>()

    fun check(url : String, id : Int) {

        System.setProperty("WebCheckerURL", url)

        /*
        val rt = Runtime.getRuntime();
        var command = "gradle test --tests \"src\\test\\kotlin\\dk\\edutor\\eduport\\webchecker\\TestWebChecker.kt\"";
        command = "java -cp lib\\kotlin-test.jar src\\test\\kotlin\\dk\\edutor\\eduport\\webchecker\\TestWebChecker.kt";
        command = "gradle assemble testClasses";
        val pr = rt.exec(command);
        */

        val request = LauncherDiscoveryRequestBuilder
                .request()
                //.selectors(selectPackage("dk.edutor.eduport.webchecker"), selectClass(T_E_S_T_WebChecker::class.java))
                .selectors(selectPackage("dk.edutor.eduport.webchecker.challenges"))
                //.selectors(selectClass(WebChallenge2.javaClass))
                .filters(includeClassNamePatterns(".*WebChallenge" + id))
                .build()

        val launcher = LauncherFactory.create()

        launcher.registerTestExecutionListeners(ListenerResults(this))
        launcher.registerTestExecutionListeners(ListenerSummary(this))

        launcher.execute(request)
    }
}

class ListenerResults(val launcher : Launcher) : TestExecutionListener
{
    override fun executionFinished(identifier: TestIdentifier, result: TestExecutionResult) {
        super.executionFinished(identifier, result)

        if(identifier.isTest) {
            val ltr = LauncherTestResult()

            if(result.status.toString().equals("SUCCESSFUL"))
            {
                launcher.successful++;
                ltr.status = "SUCCESSFUL"
            }
            if(result.status.toString().equals("FAILED"))
            {
                launcher.failed++;
                ltr.status = "FAILED"
            }

            launcher.tests[identifier.displayName] = ltr
        }
    }

    override fun testPlanExecutionFinished(testPlan: TestPlan?) {
        super.testPlanExecutionFinished(testPlan)

        if (launcher.tests.size > 0) {
            launcher.successfulPercentage = (100.0 / launcher.tests.size) * launcher.successful
        }
    }
}

class ListenerSummary(val launcher : Launcher) : SummaryGeneratingListener()
{
    override fun testPlanExecutionFinished(plan: TestPlan?) {
        super.testPlanExecutionFinished(plan)

        for(failure in super.getSummary().failures)
        {
            val ltr = launcher.tests[failure.testIdentifier.displayName]
            ltr!!.message = failure.exception.message!!;
            //println("FailureStackTrace: " + failure.exception.printStackTrace())
        }
    }
}