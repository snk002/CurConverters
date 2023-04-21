import data.tools.MailChecker
import domain.models.Sources

fun main() {
    println("ConvTools v. 0.2 MP/MT\nWindows limitations: only image operations supported.\n")
    printHelp(null)
    readLine()?.let {
        if (it == "0" || it.toIntOrNull() == null) {
            println("Bye!")
            return
        }
        if (it=="5") {
            println(MailChecker.checkMail("nobody@curator.org"))
            return
        }
        val src = when (it.toInt()) {
            1 -> Sources.SR
            2 -> Sources.IN
            3 -> Sources.LA
            9 -> Sources.ALL
            else -> {
                println("Wrong selection! Bye!")
                return
            }
        }
        printHelp(src)
        val action = readLine()?.let { actionStr ->
            if (actionStr == "0" || actionStr.toIntOrNull() == null) {
                println("Bye!")
                return
            }
            actionStr.toInt()
        } ?: 0
        if (action !in (1..9)) {
            println("Wrong selection! Bye!")
            return
        }
        AppClass(src).processCommand(action)
    }
}

