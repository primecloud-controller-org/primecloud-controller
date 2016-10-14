package jp.primecloud.auto.vmware;

import java.util.Properties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.VirtualMachine;

public class VmwareClientTest {

    private VmwareClient client;

    @Before
    public void setUp() throws Exception {
        if (client == null) {
            Properties properties = new Properties();
            properties.load(getClass().getResourceAsStream("/test.properties"));

            String url = properties.getProperty("vmware.url");
            String username = properties.getProperty("vmware.username");
            String password = properties.getProperty("vmware.password");
            boolean ignoreCert = BooleanUtils.toBoolean(properties.getProperty("vmware.ignoreCert"));
            String datacenterName = StringUtils.defaultIfEmpty(properties.getProperty("vmware.datacenterName"), null);

            VmwareClientFactory factory = new VmwareClientFactory();
            factory.setUrl(url);
            factory.setUsername(username);
            factory.setPassword(password);
            factory.setIgnoreCert(ignoreCert);
            factory.setDatacenterName(datacenterName);

            client = factory.createVmwareClient();
        }
    }

    @Test
    @Ignore
    public void testSearch() {
        System.out.println("===== DataCenter =====");
        ManagedEntity[] datacenters = client.searchByType(Datacenter.class);
        for (ManagedEntity entity : datacenters) {
            Datacenter datacenter = Datacenter.class.cast(entity);
            System.out.println(datacenter.getName());
        }

        System.out.println("===== ComputeResource =====");
        ManagedEntity[] computeResources = client.searchByType(ComputeResource.class);
        for (ManagedEntity entity : computeResources) {
            ComputeResource computeResource = ComputeResource.class.cast(entity);
            System.out.println(computeResource.getName());
        }

        System.out.println("===== HostSystem =====");
        ManagedEntity[] hostSystems = client.searchByType(HostSystem.class);
        for (ManagedEntity entity : hostSystems) {
            HostSystem hostSystem = HostSystem.class.cast(entity);
            System.out.println(hostSystem.getName());
        }

        System.out.println("===== VirtualMachine =====");
        ManagedEntity[] virtualMachines = client.searchByType(VirtualMachine.class);
        for (ManagedEntity entity : virtualMachines) {
            VirtualMachine virtualMachine = VirtualMachine.class.cast(entity);
            System.out.println(virtualMachine.getName());
        }

        System.out.println("===== Datastore =====");
        ManagedEntity[] datastores = client.searchByType(Datastore.class);
        for (ManagedEntity entity : datastores) {
            Datastore datastore = Datastore.class.cast(entity);
            System.out.println(datastore.getName());
        }

        System.out.println("===== Network =====");
        ManagedEntity[] networks = client.searchByType(Network.class);
        for (ManagedEntity entity : networks) {
            Network network = Network.class.cast(entity);
            System.out.println(network.getName());
        }

        System.out.println("===== Folder =====");
        ManagedEntity[] folders = client.searchByType(Folder.class);
        for (ManagedEntity entity : folders) {
            Folder folder = Folder.class.cast(entity);
            System.out.println(folder.getName());
        }
    }

}
