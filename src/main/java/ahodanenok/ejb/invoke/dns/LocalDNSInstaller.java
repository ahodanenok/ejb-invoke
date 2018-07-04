package ahodanenok.ejb.invoke.dns;

import sun.net.spi.nameservice.NameService;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// todo: install for JAVA 1.6
public final class LocalDNSInstaller {

    private static Logger LOGGER = Logger.getLogger(LocalDNSInstaller.class.getName());

    private LocalDNSInstaller() { }

    public static void install(Map<String, String> hosts) {
        LOGGER.config(String.format("Installing name service '%s'", LocalDNSDescriptor.ID));

        NameService localNameService;
        try {
            localNameService = new LocalDNSDescriptor(hosts).createNameService();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format(
                    "Installation failed: can't create LocalDNS (%s)", e.getMessage()), e);
            return;
        }

        if (localNameService == null) {
            return;
        }

        installJava7Plus(localNameService);
    }

    private static void installJava7Plus(NameService nameService) {
        Field nameServicesField = null;
        try {
            nameServicesField = InetAddress.class.getDeclaredField("nameServices");
            nameServicesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<NameService> foundNameServices = (List<NameService>) nameServicesField.get(null);

            List<NameService> nameServices = new ArrayList<NameService>();
            nameServices.add(nameService);
            nameServices.addAll(foundNameServices);
            nameServicesField.set(null, nameServices);

            LOGGER.config("Name service was installed");
        } catch (NoSuchFieldException e) {
            LOGGER.log(Level.SEVERE, String.format(
                    "Installation failed: can't change nameServices field (%s)", e.getMessage()), e);
        } catch (IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, String.format(
                    "Installation failed: can't change nameServices field (%s)", e.getMessage()), e);
        } finally {
            if (nameServicesField != null) {
                nameServicesField.setAccessible(false);
            }
        }
    }
}
