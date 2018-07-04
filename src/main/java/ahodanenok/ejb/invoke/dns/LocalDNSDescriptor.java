package ahodanenok.ejb.invoke.dns;

import sun.net.spi.nameservice.NameService;
import sun.net.spi.nameservice.NameServiceDescriptor;

import java.util.Map;

public class LocalDNSDescriptor implements NameServiceDescriptor {

    public static final String PROVIDER = "local";
    public static final String TYPE = "dns";
    public static final String ID = TYPE + "," + PROVIDER;

    private Map<String, String> hosts;

    public LocalDNSDescriptor(Map<String, String> hosts) {
        this.hosts = hosts;
    }

    @Override
    public NameService createNameService() throws Exception {
        return new LocalDNS(hosts);
    }

    @Override
    public String getProviderName() {
        return PROVIDER;
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
