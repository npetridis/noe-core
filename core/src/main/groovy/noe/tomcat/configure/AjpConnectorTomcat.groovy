package noe.tomcat.configure

/**
 * Abstraction for AJP connector to configure in Tomcat server.xml.
 * It is used for transfer data from user to `TomcatConfigurator`.
 * No default values are provided.
 *
 * IMPORTANT
 * <ul>
 *   <li>Not all connector attributes are supported. Only the most used ones.</li>
 *   <li>It is user responsibility to set values semantically, no validation is performed.</li>
 * <ul>
 *
 * @link https://tomcat.apache.org/tomcat-8.0-doc/config/http.html
 */
public class AjpConnectorTomcat extends ConnectorTomcatAbstract<AjpConnectorTomcat> {

}
