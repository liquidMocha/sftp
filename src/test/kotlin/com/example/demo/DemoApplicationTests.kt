package com.example.demo

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.apache.sshd.SshServer
import org.apache.sshd.common.NamedFactory
import org.apache.sshd.server.Command
import org.apache.sshd.server.CommandFactory
import org.apache.sshd.server.command.ScpCommandFactory
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider
import org.apache.sshd.server.sftp.SftpSubsystem
import org.junit.Test
import org.junit.jupiter.api.BeforeEach
import org.springframework.boot.test.context.SpringBootTest
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {
//	lateinit var sshd: SshServer
	@BeforeEach
	@Throws(Exception::class)
	fun beforeTestSetup() {
		println("-------------------------------")
		val sshd = SshServer.setUpDefaultServer()
		sshd.port = 22999
//		sshd.keyPairProvider = SimpleGeneratorHostKeyProvider("hostkey.ser")
		sshd.setPasswordAuthenticator { username, password, session ->
			true
		}
		val myCommandFactory: CommandFactory = CommandFactory { command ->
			println("Command: $command")
			null
		}
		sshd.commandFactory = ScpCommandFactory(myCommandFactory)

		val namedFactoryList: MutableList<NamedFactory<Command>> = ArrayList()
		namedFactoryList.add(SftpSubsystem.Factory())
		sshd.subsystemFactories = namedFactoryList
		sshd.start()
	}

	@org.junit.After
	@Throws(Exception::class)
	fun teardown() {
//		sshd.stop()
	}


	@Throws(java.lang.Exception::class)
	@Test
	fun testPutAndGetFile() {
		val sshd = SshServer.setUpDefaultServer()
		sshd.port = 22999
		val provider = SimpleGeneratorHostKeyProvider()
		provider.algorithm = "RSA"
		sshd.keyPairProvider = provider
//		sshd.keyPairProvider = SimpleGeneratorHostKeyProvider("hostkey.ser")
		sshd.setPasswordAuthenticator { username, password, session ->
			true
		}
		val myCommandFactory: CommandFactory = CommandFactory { command ->
			println("Command: $command")
			null
		}
		sshd.commandFactory = ScpCommandFactory(myCommandFactory)

		val namedFactoryList: MutableList<NamedFactory<Command>> = ArrayList()
		namedFactoryList.add(SftpSubsystem.Factory())
		sshd.subsystemFactories = namedFactoryList
		sshd.start()


		val jsch = JSch()

		val session: Session = jsch.getSession("remote-username", "localhost", 22999)
		session.setPassword("remote-password")

		val config = Properties()
		config["StrictHostKeyChecking"] = "no"

		session.setConfig(config)

		session.connect()

		val channel: Channel = session.openChannel("sftp")
		channel.connect()
		val sftpChannel = channel as ChannelSftp
		val testFileContents = "some file contents"
		val uploadedFileName = "uploadFile"
		sftpChannel.put(ByteArrayInputStream(testFileContents.toByteArray()), uploadedFileName)
		val downloadedFileName = "downLoadFile"
		sftpChannel[uploadedFileName, downloadedFileName]
		val downloadedFile = File(downloadedFileName)

		println(downloadedFile.forEachLine { println(it) });
//		downloadedFile.readLines().forEach(
//				println('line: $it')
//		)


//		assertTrue(downloadedFile.exists())
//		val fileData: String = getFileContents(downloadedFile)
//		assertEquals(testFileContents, fileData)
//		if (sftpChannel.isConnected) {
//			sftpChannel.exit()
//			logger.debug("Disconnected channel")
//		}
//		if (session.isConnected()) {
//			session.disconnect()
//			logger.debug("Disconnected session")
//		}
	}

}
