package org.cpnsim.interscheduler;

import lombok.Getter;
import lombok.Setter;
import org.cloudsimplus.network.topologies.NetworkTopology;
import org.cpnsim.datacenter.Datacenter;
import org.cpnsim.request.Instance;
import org.cpnsim.request.InstanceGroup;
import org.cpnsim.request.UserRequest;

import java.util.*;

public class InterSchedulerSimple implements InterScheduler {
    @Getter
    Datacenter datacenter;
    @Getter
    String name;
    @Getter
    int id;

    @Getter
    double filterSuitableDatacenterCostTime = 0.0;
    @Getter
    double decideReciveGroupResultCostTime = 0.0;
    @Getter
    double decideTargetDatacenterCostTime = 0.0;
    @Getter
    @Setter
    boolean directedSend = false;

    Random random = new Random(1);

    public InterSchedulerSimple() {
        this.id = 0;
    }

    public InterSchedulerSimple(int id, Datacenter datacenter) {
        this.id = id;
        this.name = "InterScheduler" + id;
        this.datacenter = datacenter;
    }

    /**
     * Filter the suitable datacenters for each instance group, considering the network topology.
     * @param instanceGroups the instance group list to be allocated
     * @return the map, which value is the suitable datacenter list for the corresponding key
     */
    @Override
    public Map<InstanceGroup, List<Datacenter>> filterSuitableDatacenter(List<InstanceGroup> instanceGroups) {
        List<Datacenter> allDatacenters = datacenter.getSimulation().getCollaborationManager().getDatacenters(datacenter);
        NetworkTopology networkTopology = datacenter.getSimulation().getNetworkTopology();
        Map<InstanceGroup, List<Datacenter>> instanceGroupAvaiableDatacenters = new HashMap<>();
        for (InstanceGroup instanceGroup : instanceGroups) {
            List<Datacenter> availableDatacenters = getAvailableDatacenters(instanceGroup, allDatacenters, networkTopology);
            instanceGroupAvaiableDatacenters.put(instanceGroup, availableDatacenters);
        }
        interScheduleByNetworkTopology(instanceGroupAvaiableDatacenters, networkTopology);
        this.filterSuitableDatacenterCostTime = 0.2;//TODO 为了模拟没有随机性，先设置为每一个亲和组调度花费0.1ms
        return instanceGroupAvaiableDatacenters;
    }

    /**
     * Simply receive all the instance group.
     * @param instanceGroups    the instance group list to be allocated
     * @return the map which keys are the instance group to be received
     */
    @Override
    public Map<InstanceGroup, Boolean> decideReciveGroupResult(List<InstanceGroup> instanceGroups) {
        //TODO 怎么判断是否接收，如果接收了怎么进行资源预留，目前是全部接收
        Map<InstanceGroup, Boolean> result = new HashMap<>();
        for (InstanceGroup instanceGroup : instanceGroups) {
            if (instanceGroup.getUserRequest().getState() == UserRequest.FAILED) {
                continue;
            }
            result.put(instanceGroup, true);
        }
        this.decideReciveGroupResultCostTime = 0.1;//TODO 为了模拟没有随机性，先设置为每一个亲和组调度花费0.1ms
        return result;
    }

    /**
     * Decide the target datacenter for each instance group.
     * @param instanceGroupSendResultMap the map, the value is the able datacenter map for the corresponding key
     * @param instanceGroups             the instance group list to be allocated
     * @return the map, which value is the target datacenter for the corresponding key
     */
    @Override
    public Map<InstanceGroup, Datacenter> decideTargetDatacenter(Map<InstanceGroup, Map<Datacenter, Integer>> instanceGroupSendResultMap, List<InstanceGroup> instanceGroups) {
        this.decideTargetDatacenterCostTime = 0.0;
        Map<InstanceGroup, Datacenter> result = new HashMap<>();
        for (InstanceGroup instanceGroup : instanceGroups) {
            List<Datacenter> datacenters = instanceGroupSendResultMap.get(instanceGroup).entrySet().stream()
                    .filter(entry -> entry.getValue() == 1)
                    .map(Map.Entry::getKey)
                    .toList();
            if (datacenters.size() == 0) {
                //表示调度失败
                result.put(instanceGroup, null);
            } else {
                //表示调度成功
                result.put(instanceGroup, datacenters.get(random.nextInt(datacenters.size())));
            }
        }
        return result;
    }

    /**
     * Receive the instance group which isn’t employed.
     * @param instanceGroups the instance group list which isn‘t employed
     */
    @Override
    public void receiveNotEmployGroup(List<InstanceGroup> instanceGroups) {
        // 目前不需要做任何处理
    }

    /**
     * Receive the instance group which is employed.
     * @param instanceGroups the instance group list which is employed
     */
    @Override
    public void receiveEmployGroup(List<InstanceGroup> instanceGroups) {
        // 目前不需要做任何处理
    }

    /**
     * Get whether the inter scheduler is directed send.
     * @return  whether the inter scheduler is directed send
     */
    @Override
    public boolean isDirectedSend() {
        return directedSend;
    }

    /**
     * Get available datacenters for the instance group.
     * @param instanceGroup   the instance group to be scheduled
     * @param allDatacenters  the all datacenters to be filtered
     * @param networkTopology the network topology
     * @return  the available datacenters for the instance group
     */
    //TODO 如果前一个亲和组被可能被分配给多个数据中心，那么后一个亲和组在分配的时候应该如何更新资源状态。目前是不考虑
    private List<Datacenter> getAvailableDatacenters(InstanceGroup instanceGroup, List<Datacenter> allDatacenters, NetworkTopology networkTopology) {
        List<Datacenter> availableDatacenters = new ArrayList<>(allDatacenters);
        //根据接入时延要求得到可调度的数据中心
        filterDatacentersByAccessLatency(instanceGroup, availableDatacenters, networkTopology);
        //根据资源抽样信息得到可调度的数据中心
        filterDatacentersByResourceSample(instanceGroup, availableDatacenters);
        return availableDatacenters;
    }

    /**
     * Remove the datacenter which don't meet the condition of access latency.
     * @param instanceGroup   the instance group to be scheduled
     * @param allDatacenters  the all data centers to be filtered
     * @param networkTopology the network topology
     */
    private void filterDatacentersByAccessLatency(InstanceGroup instanceGroup, List<Datacenter> allDatacenters, NetworkTopology networkTopology) {
        allDatacenters.removeIf(datacenter -> instanceGroup.getAccessLatency() < networkTopology.getAcessLatency(this.datacenter, datacenter));
    }

    /**
     * Remove the datacenter which don't meet the condition of Resource.
     * @param instanceGroup   the instance group to be scheduled
     * @param allDatacenters  the all data centers to be filtered
     */
    private void filterDatacentersByResourceSample(InstanceGroup instanceGroup, List<Datacenter> allDatacenters) {
        //首先是粗粒度地筛选总量是否满足
        allDatacenters.removeIf(
                datacenter -> datacenter.getStatesManager().getSimpleState().getCpuAvaiableSum() < instanceGroup.getCpuSum() ||
                        datacenter.getStatesManager().getSimpleState().getRamAvaiableSum() < instanceGroup.getRamSum() ||
                        datacenter.getStatesManager().getSimpleState().getStorageAvaiableSum() < instanceGroup.getStorageSum() ||
                        datacenter.getStatesManager().getSimpleState().getBwAvaiableSum() < instanceGroup.getBwSum()
        );
        //然后细粒度地查看CPU-RAM的组合是否满足
        for (Datacenter datacenter : allDatacenters) {
            Map<Integer, Map<Integer, Integer>> instanceCpuRamNum = new HashMap<>();//记录一下所有Instance的cpu—ram的种类情况
            for (Instance instance : instanceGroup.getInstanceList()) {
                int allocateNum = instanceCpuRamNum.getOrDefault(instance.getCpu(), new HashMap<>()).getOrDefault(instance.getRam(), 0);
                if (datacenter.getStatesManager().getSimpleState().getCpuRamSum(instance.getCpu(), instance.getRam()) - allocateNum <= 0) {
                    //如果该数据中心的资源不足以满足亲和组的资源需求，那么就将其从可调度的数据中心中移除
                    allDatacenters.remove(datacenter);
                    break;
                } else {
                    //如果该数据中心的资源可以满足亲和组的资源需求，那么就记录更新已分配的所有Instance的cpu—ram的种类情况
                    if (instanceCpuRamNum.containsKey(instance.getCpu())) {
                        Map<Integer, Integer> ramNumMap = instanceCpuRamNum.get(instance.getCpu());
                        if (ramNumMap.containsKey(instance.getRam())) {
                            ramNumMap.put(instance.getRam(), ramNumMap.get(instance.getRam()) + 1);
                        } else {
                            ramNumMap.put(instance.getRam(), 1);
                        }
                    } else {
                        Map<Integer, Integer> ramNumMap = new HashMap<>();
                        ramNumMap.put(instance.getRam(), 1);
                        instanceCpuRamNum.put(instance.getCpu(), ramNumMap);
                    }
                }
            }
        }
    }

    /**
     * Filter the datacenter by network topology.
     * @param instanceGroupAvaiableDatacenters the map, which value is the available datacenters for the corresponding key
     * @param networkTopology                  the network topology
     */
    private void interScheduleByNetworkTopology(Map<InstanceGroup, List<Datacenter>> instanceGroupAvaiableDatacenters, NetworkTopology networkTopology) {
        //TODO 根据网络拓扑中的时延和宽带进行筛选得到最优的调度方案
        //TODO 后续可以添加一个回溯算法来简单筛选
    }

    /**
     * Set the datacenter of the inter scheduler.
     * @param datacenter the datacenter to be set
     */
    @Override
    public void setDatacenter(Datacenter datacenter) {
        this.datacenter = datacenter;
        this.name = "InterScheduler" + datacenter.getId();
    }

    /**
     * Set the id of the inter scheduler.
     * @param id the id to set
     */
    @Override
    public void setId(int id) {
        this.id = id;
    }
}
