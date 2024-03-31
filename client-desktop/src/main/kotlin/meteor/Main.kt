package meteor

import jagex2.client.Configuration
import jagex2.client.Client
import sign.signlink

object Main {
    lateinit var client: Client

    @JvmStatic
    fun main(args: Array<String>) {
        initRS2()
        client = Client()
        client.initApplication(789, 531)
    }

    /**
     * All necessary initializations for rs2
     */
    private fun initRS2() {
        Client.nodeId = 10
        Client.portOffset = Configuration.PORT_OFFSET
        Client.setHighMemory()
        Client.members = true
        signlink.startDaemon()
    }
}