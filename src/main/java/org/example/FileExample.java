package org.example;

import ch.qos.logback.classic.Level;
import org.cpnsim.core.CloudSim;
import org.cpnsim.core.Factory;
import org.cpnsim.core.FactorySimple;
import org.cpnsim.core.Simulation;
import org.cpnsim.network.NetworkTopology;
import org.cpnsim.network.NetworkTopologySimple;
import org.cpnsim.network.RandomDelayDynamicModel;
import org.cpnsim.util.Log;
import org.cpnsim.datacenter.InitDatacenter;
import org.cpnsim.record.MemoryRecord;
import org.cpnsim.user.UserRequestManager;
import org.cpnsim.user.UserRequestManagerCsv;
import org.cpnsim.user.UserSimple;

public class FileExample {
    Simulation cpnSim;
    Factory factory;
    UserSimple user;
    UserRequestManager userRequestManager;
    String REGION_DELAY_FILE = "./src/main/resources/regionDelay.csv";
    String AREA_DELAY_FILE = "./src/main/resources/areaDelay.csv";
    String DATACENTER_BW_FILE = "./src/main/resources/DatacenterBwConfig.csv";
    //    String DATACENTER_CONFIG_FILE = "./src/main/resources/experiment/interFrame/centerInterToHostSchedule/DatacentersConfig.json";
//    String USER_REQUEST_FILE = "./src/main/resources/experiment/interFrame/centerInterToHostSchedule/generateRequestParameter.csv";
//    String DATACENTER_CONFIG_FILE = "./src/main/resources/experiment/interFrame/centerInterToDcNoForwardSchedule/DatacentersConfig.json";
//    String USER_REQUEST_FILE = "./src/main/resources/experiment/interFrame/centerInterToDcNoForwardSchedule/generateRequestParameter.csv";
    String DATACENTER_CONFIG_FILE = "./src/main/resources/experiment/interFrame/dcInterToSelfAndForward/DatacentersConfig.json";
    String USER_REQUEST_FILE = "./src/main/resources/experiment/interFrame/dcInterToSelfAndForward/generateRequestParameter.csv";
//    String DATACENTER_CONFIG_FILE = "./src/main/resources/experiment/interFrame/centerToDcAndForward/DatacentersConfig.json";
//    String USER_REQUEST_FILE = "./src/main/resources/experiment/interFrame/centerToDcAndForward/generateRequestParameter.csv";

    public static void main(String[] args) {
        FileExample fileExample = new FileExample();
    }

    private FileExample() {
        double start = System.currentTimeMillis();
        Log.setLevel(Level.INFO);
        cpnSim = new CloudSim();
        factory = new FactorySimple();
        initUser();
        initDatacenters();
        initNetwork();
        double endInit = System.currentTimeMillis();
        cpnSim.start();
        double end = System.currentTimeMillis();
        System.out.println("\n运行情况：");
        System.out.println("初始化耗时：" + (endInit - start) / 1000 + "s");
        System.out.println("模拟运行耗时：" + (end - endInit) / 1000 + "s");
        System.out.println("模拟总耗时：" + (end - start) / 1000 + "s");
        System.out.println("运行过程占用最大内存: " + MemoryRecord.getMaxUsedMemory() / 1000000 + " Mb");
        System.out.println("运行结果保存路径:" + cpnSim.getSqlRecord().getDbPath());
    }


    private void initUser() {
        userRequestManager = new UserRequestManagerCsv(USER_REQUEST_FILE);
        user = new UserSimple(cpnSim, userRequestManager);
    }

    private void initDatacenters() {
        InitDatacenter.initDatacenters(cpnSim, factory, DATACENTER_CONFIG_FILE);
    }

    private void initNetwork() {
        NetworkTopology networkTopology = new NetworkTopologySimple(REGION_DELAY_FILE, AREA_DELAY_FILE, DATACENTER_BW_FILE);
        networkTopology.setDelayDynamicModel(new RandomDelayDynamicModel());
        cpnSim.setNetworkTopology(networkTopology);
    }
}
