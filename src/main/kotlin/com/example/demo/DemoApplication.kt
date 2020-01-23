package com.example.demo

import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.*

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	val channelSftp = setupJsch()
	channelSftp.connect()

	val remoteFile = "welcome.txt"
	val localDir = "src/main/resources/"

	channelSftp[remoteFile, localDir + "jschFile.txt"]

	channelSftp.exit()

	runApplication<DemoApplication>(*args)
}

fun setupJsch(): ChannelSftp {
	val jsch: JSch = JSch()
	val config = Hashtable<Any, Any>()
	config["StrictHostKeyChecking"] = "no"
	JSch.setConfig(config)

	jsch.setKnownHosts("/Users/john/.ssh/known_hosts");
	val session: Session = jsch.getSession("remote-username", "localhost", 22999)
	session.setPassword("remote-password")
	session.connect()
	return session.openChannel("sftp") as ChannelSftp;
}