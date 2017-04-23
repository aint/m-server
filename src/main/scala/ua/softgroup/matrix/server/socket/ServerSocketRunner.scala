package ua.softgroup.matrix.server.socket

import java.io.IOException
import java.security.{GeneralSecurityException, KeyStore, SecureRandom}
import javax.net.ssl.{KeyManagerFactory, SSLContext, SSLServerSocket, TrustManagerFactory}

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.core.env.Environment
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Component

/**
  * @author Oleksandr Tyshkovets <olexandr.tyshkovets@gmail.com>
  */
@Component
class ServerSocketRunner @Autowired() (matrixServerApi: ServerSocketApi,
                                       environment: Environment) extends CommandLineRunner {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val SOCKET_PORT = "socket.server.port"

  private val PRIVATE_PASSPHRASE = "make_matrix_great_again".toCharArray
  private val PUBLIC_PASSPHRASE = "public".toCharArray

  private val PUBLIC_KEY_FILE = "keys/client.public"
  private val PRIVATE_KEY_FILE = "keys/server.private"

  private val KEYSTORE_TYPE = "JKS"
  private val ALGORITHM = "SunX509"

  override def run(args: String*): Unit = {
    val serverPort = environment.getRequiredProperty(SOCKET_PORT).toInt
    val serverSocket = setupSSLContext.getServerSocketFactory.createServerSocket(serverPort).asInstanceOf[SSLServerSocket]
    serverSocket.setNeedClientAuth(true)

    logger.info("Matrix Server is up")

    while (true) new Thread(new ClientSocketHandler(serverSocket.accept, matrixServerApi)).start()
  }

  @throws[GeneralSecurityException]
  @throws[IOException]
  private def setupClientKeyStore(): KeyStore = {
    val clientKeyStore = KeyStore.getInstance(KEYSTORE_TYPE)
    clientKeyStore.load(new ClassPathResource(PUBLIC_KEY_FILE).getInputStream, PUBLIC_PASSPHRASE)

    clientKeyStore
  }

  @throws[GeneralSecurityException]
  @throws[IOException]
  private def setupServerKeystore(): KeyStore = {
    val serverKeyStore = KeyStore.getInstance(KEYSTORE_TYPE)
    serverKeyStore.load(new ClassPathResource(PRIVATE_KEY_FILE).getInputStream, PRIVATE_PASSPHRASE)

    serverKeyStore
  }

  @throws[GeneralSecurityException]
  @throws[IOException]
  private def setupSSLContext = {
    val tmf = TrustManagerFactory.getInstance(ALGORITHM)
    tmf.init(setupClientKeyStore())

    val kmf = KeyManagerFactory.getInstance(ALGORITHM)
    kmf.init(setupServerKeystore(), PRIVATE_PASSPHRASE)

    val secureRandom = new SecureRandom
    secureRandom.nextInt

    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(kmf.getKeyManagers, tmf.getTrustManagers, secureRandom)

    sslContext
  }

}
