package noe.tomcat.configure

/**
 * Abstraction for secure HTTP connector to configure Tomcat server.xml.
 * It is used for transfer data from user to `TomcatConfigurator`.
 * Provides default values if needed.
 *
 * Secure HTTP connector has set attributes `secure="true", SSLEnabled = "true" always.
 *
 * IMPORTANT
 * <ul>
 *   <li>Not all connector attributes are supported. Only the most used ones.</li>
 *   <li>It is user responsibility to set values semantically, no validation is performed.</li>
 * <ul>
 *
 * @link https://tomcat.apache.org/tomcat-8.0-doc/config/http.html
 */
public class SecureHttp2ConnectorTomcat extends SecureHttpConnectorTomcatAbstract<SecureHttp2ConnectorTomcat> {

  private String upgradeProtocol

  public SecureHttp2ConnectorTomcat() {
    super()
//    setProtocol("org.apache.coyote.http2.Http2Protocol")
  }

  String getUpgradeProtocol() {
    return upgradeProtocol
  }

  public SecureHttp2ConnectorTomcat setUpgradeProtocol(String upgradeProtocol) {
    this.upgradeProtocol = upgradeProtocol
    return this
  }
}
