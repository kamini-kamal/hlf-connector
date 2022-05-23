package hlf.java.rest.client.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * FabricProperties reads and binds with the application.yml and provide all the configuration as a
 * bean configuration To use any configuration, just autowire and call the associated "get" method
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "fabric")
@RefreshScope
public class FabricProperties {

  private static final String SYSTEM_PROP_FABRIC_SERVICE_DISCOVERY_LOCALHOST =
      "org.hyperledger.fabric.sdk.service_discovery.as_localhost";

  private boolean localhostReportAddress;
  private WalletConfig wallet;
  private OrgConnectionConfig orgConnectionConfig;
  private Client client;
  private Events events;

  @Data
  public static class WalletConfig {
    private String path;
    private AdminUser adminUser;
    private ClientUser clientUser;

    @Data
    public static class AdminUser {
      private String name;
    }

    @Data
    public static class ClientUser {
      private String name;
    }
  }

  @Data
  public static class OrgConnectionConfig {
    private String path;
    private String filename;
  }

  @Data
  public static class Client {
    private Rest rest;

    @Data
    public static class Rest {
      private String apikey;
    }
  }

  @Data
  @ConditionalOnProperty(prefix = "fabric.events", name = "enable", havingValue = "true")
  public static class Events {
    private boolean enable;
    private List<String> chaincode;
    private List<String> block;
  }

  /**
   * Forces Hyperledger Service Discovery to report the localhost address for all nodes (peers &
   * ordering service) when client is running on local machine. This property allows client to node
   * connectivity when nodes cannot be accessed directly by the client on their public network
   * address as in the case above.
   */
  @PostConstruct
  private void systemVariableSetup() {
    System.setProperty(
        SYSTEM_PROP_FABRIC_SERVICE_DISCOVERY_LOCALHOST,
        String.valueOf(this.isLocalhostReportAddress()));
  }
}
