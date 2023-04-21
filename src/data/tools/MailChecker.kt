package data.tools

import java.io.*
import java.net.*
import javax.naming.NamingException
import javax.naming.directory.Attribute
import javax.naming.directory.Attributes
import javax.naming.directory.InitialDirContext


class MailChecker {

    companion object {

        private fun resolveHost(host: String): List<String> {
            val domain = host.replaceBefore('@',"").drop(1)
            println("Checking $domain domain")
            return MailHostsLookup.lookupMailHosts(domain)
        }

        private fun readUntilOk(inStream: BufferedReader): Boolean {
            var s = ""
            var lim = 5
            while (!s.startsWith("250")) {
                s = inStream.readLine()
                println(s)
                if (--lim == 0) return false
            }
            return true
        }

        fun checkMail(mail: String): Boolean {
            val mailServers = resolveHost(mail)
            mailServers.forEach {
                val mailSocket = Socket(it, 25)
                val outStream = PrintWriter(mailSocket.getOutputStream(), true)
                val inStream = BufferedReader(InputStreamReader(mailSocket.getInputStream()))
                outStream.println("helo hi")
                if (!readUntilOk(inStream)) return false
                outStream.println("mail from: <postmaster@gmail.com>")
                if (!readUntilOk(inStream)) return false
                outStream.println("rcpt to: <$mail>")
                val s = inStream.readLine()
                println(s)
                outStream.println("quit")
                outStream.close()
                inStream.close()
                return s.startsWith("250")
            }
            return false
        }
    }
}

// see: RFC 974 - Mail routing and the domain system
// see: RFC 1034 - Domain names - concepts and facilities
// see: http://java.sun.com/j2se/1.5.0/docs/guide/jndi/jndi-dns.html
//    - DNS Service Provider for the Java Naming Directory Interface (JNDI)
object MailHostsLookup {

    // returns a String array of mail exchange servers (mail hosts)
    //     sorted from most preferred to least preferred
    @Throws(NamingException::class)
    fun lookupMailHosts(domainName: String): List<String> {
        // get the default initial Directory Context
        val iDirC = InitialDirContext()
        // get the MX records from the default DNS directory service provider
        // NamingException thrown if no DNS record found for domainName
        val attributes: Attributes = iDirC.getAttributes("dns:/$domainName", arrayOf("MX"))
        // attributeMX is an attribute ('list') of the Mail Exchange(MX) Resource Records(RR)
        val attributeMX: Attribute = attributes.get("MX") ?: return listOf(domainName)
        println(attributeMX)
        // if there are no MX RRs then default to domainName (see: RFC 974)
        // split MX RRs into Preference Values(pvhn[0]) and Host Names(pvhn[1])
        val hosts = mutableListOf<String>()
        for (i in 0 until attributeMX.size()) {
            hosts.add(attributeMX[i].toString().split(" ").last().dropLast(1))
        }
        return hosts
    }
}