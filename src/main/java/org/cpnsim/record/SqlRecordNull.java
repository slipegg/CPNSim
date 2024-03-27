package org.cpnsim.record;

import org.cpnsim.datacenter.Datacenter;
import org.cpnsim.request.Instance;
import org.cpnsim.request.InstanceGroup;
import org.cpnsim.request.UserRequest;

import java.util.List;
import java.util.Map;

/**
 * It is an implementation of the SqlRecord interface.
 * If you do not want to record the simulation information, you can use this class.
 *
 * @author Jiawen Liu
 * @since LGDCloudSim 1.0
 */
public class SqlRecordNull implements SqlRecord {
    public SqlRecordNull() {
    }

    @Override
    public String getDbPath() {
        return null;
    }

    @Override
    public void setDbPath(String dbPath) {

    }

    @Override
    public void recordUserRequestsSubmitInfo(List<UserRequest> userRequests) {

    }

    @Override
    public void recordUserRequestFinishInfo(UserRequest userRequest) {

    }

    @Override
    public void recordInstanceGroupsReceivedInfo(List instanceGroups) {

    }

    @Override
    public void recordInstanceGroupFinishInfo(InstanceGroup instanceGroup) {

    }

    @Override
    public void recordInstanceGroupAllInfo(InstanceGroup instanceGroup) {

    }

    @Override
    public void recordInstanceGroupGraphAllocateInfo(int srcDcId, int srcInstanceGroupId, int dstDcId, int dstInstanceGroupId, double bw, double startTime) {

    }

    @Override
    public void recordInstanceGroupsGraph(List<InstanceGroup> instanceGroups) {

    }

    @Override
    public void recordInstanceGroupGraphReleaseInfo(int srcDcId, int dstDcId, double finishTime) {

    }

    @Override
    public void recordInstancesCreateInfo(Map<Integer, List<Instance>> instances) {
    }

    @Override
    public void recordInstancesCreateInfo(List<InstanceGroup> instanceGroups) {

    }

    @Override
    public void recordInstancesFinishInfo(List<Instance> instances) {

    }

    @Override
    public void recordInstancesAllInfo(List<Instance> instances) {
    }

    @Override
    public void recordConflict(double time, int sum) {

    }

    @Override
    public void recordDatacentersInfo(List<Datacenter> datacenters) {

    }

    @Override
    public void recordDatacentersInfo(Datacenter datacenters) {

    }

    @Override
    public void close() {

    }
}
