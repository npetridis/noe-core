package noe.tomcat.configure

import noe.common.utils.PathHelper
import noe.common.utils.Platform

/**
 * Abstraction of connector within Tomcat server.xml
 * It is used for transfer data from user to `TomcatConfigurator`.
 * No default values are provided.
 *
 * Also see wrapper classes: `NonSecureHttpConnector`, `SecureHttpConnector`, `AjpConnector`
 *
 * IMPORTANT
 * <ul>
 *   <li>Not all connector attributes are supported. Only the most used ones.</li>
 *   <li>It is user responsibility to set values semantically, no validation is performed.</li>
 * <ul>
 */
abstract public class SecureHttpConnectorTomcatAbstract<E extends SecureHttpConnectorTomcatAbstract> extends ConnectorTomcatAbstract<E> {
  // - vvv ------------------------------------------------------
  // IMPORTANT: When adding support for new argument udpate method `ConnectorAttributesFactory` as well
  //
  // SSL common
  private boolean sslEnabled

  // SSL BIO and NIO
  private String sslProtocol
  private String keystoreFile
  private String keystorePass
  private String clientAuth

  // SSL APR
  private String sslCertificateFile
  private String sslCertificateKeyFile
  private String sslCACertificateFile
  private String sslPassword
  // - ^^^ ------------------------------------------------------

  public SecureHttpConnectorTomcatAbstract() {
    super.setSecure(true)
    sslEnabled = true

    String sslStringDir = PathHelper.join(new Platform().tmpDir, "ssl", "self_signed")
    File sslCertDir = new File(sslStringDir)
    sslCertificateFile = new File(sslCertDir, "server.crt")
    sslCertificateKeyFile = new File(sslCertDir, "server.key")
    sslPassword = "changeit"
//    this.keystorePath = new File(sslCertDir, "server.jks").absolutePath
  }

  /**
   * Input argument secure is ignored secure is set `true` always.
   */
  @Override
  public E setSecure(boolean secure) {
    return (E) this
  }

  public Boolean getSslEnabled() {
    return this.sslEnabled
  }

  public Boolean getClientAuth() {
    return this.clientAuth
  }

  public String getSslProtocol() {
    return this.sslProtocol
  }

  public String getKeystoreFile() {
    return this.keystoreFile
  }

  public String getKeystorePass() {
    return this.keystorePass
  }

  public String getSslCertificateFile() {
    return this.sslCertificateFile
  }

  public String getSslCertificateKeyFile() {
    return this.sslCertificateKeyFile
  }

  public String getSslPassword() {
    return this.sslPassword
  }

  public String getSslCACertificateFile() {
    return sslCACertificateFile
  }

  public E setSslProtocol(String sslProtocol) {
    this.sslProtocol = sslProtocol
    return (E) this
  }

  public E setKeystoreFile(String keystoreFile) {
    this.keystoreFile = keystoreFile
    return (E) this
  }

  public E setKeystorePass(String keystorePass) {
    this.keystorePass = keystorePass
    return (E) this
  }

  public E setClientAuth(boolean clientAuth) {
    this.clientAuth = clientAuth
    return (E) this
  }

  public E setSslCertificateFile(String sslCertificateFile) {
    this.sslCertificateFile = sslCertificateFile
    return (E) this
  }

  public E setSslCertificateKeyFile(String sslCertificateKeyFile) {
    this.sslCertificateKeyFile = sslCertificateKeyFile
    return (E) this
  }

  public E setSslPassword(String sslPassword) {
    this.sslPassword = sslPassword
    return (E) this
  }

  public E setSslCACertificateFile(String sslCACertificateFile) {
    this.sslCACertificateFile = sslCACertificateFile
    return (E) this
  }

}
