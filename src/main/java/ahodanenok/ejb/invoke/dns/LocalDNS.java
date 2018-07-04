package ahodanenok.ejb.invoke.dns;

import sun.net.spi.nameservice.NameService;
import sun.net.spi.nameservice.dns.DNSNameService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LocalDNS implements NameService {

    private static final Logger LOGGER = Logger.getLogger(LocalDNS.class.getName());

    private NameService defaultNameService;
    private Map<String, InetAddress> addresses;

    public LocalDNS(Map<String, String> hosts) throws Exception{
        this.defaultNameService = new DNSNameService();

        addresses = new HashMap<String, InetAddress>();
        if (hosts != null) {
            for (String host : hosts.keySet()) {
                addresses.put(host, InetAddress.getByAddress(ipToBytes(hosts.get(host))));
            }
        }
    }

    @Override
    public InetAddress[] lookupAllHostAddr(String host) throws UnknownHostException {
        LOGGER.info(String.format("Lookup up host address '%s'", host));

        InetAddress[] foundAddresses;
        if (addresses.containsKey(host)) {
            LOGGER.finer("Exists in local DNS");
            foundAddresses = new InetAddress[] { addresses.get(host) };
        } else {
            LOGGER.finer("Doesn't exist in local DNS");
            try {
                foundAddresses = defaultNameService.lookupAllHostAddr(host);
            } catch (UnknownHostException e) {
                LOGGER.warning(String.format("Unknown host '%s'", host));
                throw e;
            }
        }

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Found addresses: " + Arrays.toString(foundAddresses));
        }

        return foundAddresses;
    }

    @Override
    public String getHostByAddr(byte[] bytes) throws UnknownHostException {
        return defaultNameService.getHostByAddr(bytes);
    }

    private byte[] ipToBytes(String ip) {
        String[] parts = ip.trim().split("\\.");
        if (parts.length != 4) throw new IllegalArgumentException("Ip address must be in format: [0-255].[0-255].[0-255].[0-255]");

        byte[] bytes = new byte[4];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (Integer.parseInt(parts[i], 10) & 0xFF);
        }

        return bytes;
    }
}
