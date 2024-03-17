package org.cpnsim.interscheduler;

import lombok.Getter;
import lombok.Setter;
import org.cpnsim.datacenter.Datacenter;
import org.cpnsim.request.Instance;
import org.cpnsim.request.InstanceGroup;
import org.cpnsim.request.UserRequest;

import java.util.*;

/**
 * The scheduling result of the inter-scheduler.
 * It contains the scheduling result of the instance groups and the failed instance groups.
 * It also contains the outdated user requests which are exceeded the schedule delay limit.
 *
 * @author Jiawen Liu
 * @since LGDCloudSim 1.0
 */
@Getter
@Setter
public class InterSchedulerResult {
    /**
     * The collaboration id of the inter-scheduler.
     * It is used for the centralized inter-scheduler of the collaboration zone in CIS.
     */
    private int collaborationId;

    /**
     * The target of the inter-scheduler.
     */
    private int target;

    /**
     * Whether the scheduled instance group results support forward again.
     */
    private Boolean isSupportForward;

    /**
     * The scheduling result map of the instance groups.
     * For the host-target schedule result, the schedule result has been stored in every instance's {@link Instance#getExpectedScheduleHostId()}.
     * TODO needs to be changed from List to Set later to speed up the determination of inclusion.
     */
    private Map<Datacenter, List<InstanceGroup>> scheduledResultMap;

    /**
     * The failed instance groups.
     */
    private List<InstanceGroup> failedInstanceGroups;

    /**
     * The number of the instance groups.
     */
    private int instanceGroupNum;

    /**
     * The outdated user requests which are exceeded the schedule delay limit.
     */
    private Set<UserRequest> outDatedUserRequests;

    /**
     * Create a new inter-scheduler result.
     *
     * @param collaborationId  the collaboration id of the inter-scheduler.
     * @param target           the target of the inter-scheduler.
     * @param isSupportForward whether the scheduled instance group results support forward again.
     * @param allDatacenters   all the datacenters in the collaboration zone.
     */
    public InterSchedulerResult(int collaborationId, int target, Boolean isSupportForward, List<Datacenter> allDatacenters) {
        this.collaborationId = collaborationId;
        this.target = target;
        this.isSupportForward = isSupportForward;
        this.failedInstanceGroups = new ArrayList<>();
        initDcResultMap(allDatacenters);
    }

    /**
     * Create a new inter-scheduler result.
     * IsSupportForward is set to false by default.
     * @param collaborationId the collaboration id of the inter-scheduler.
     * @param target the target of the inter-scheduler.
     * @param allDatacenters all the datacenters in the collaboration zone.
     */
    public InterSchedulerResult(int collaborationId, int target, List<Datacenter> allDatacenters) {
        this(collaborationId, target, false, allDatacenters);
    }

    /**
     * Add the scheduled instance group to the scheduling result map.
     * @param instanceGroup the scheduled instance group.
     * @param datacenter the datacenter where the instance group is scheduled.
     */
    public void addDcResult(InstanceGroup instanceGroup, Datacenter datacenter) {
        this.scheduledResultMap.get(datacenter).add(instanceGroup);
        this.instanceGroupNum++;
    }

    /**
     * Add the failed instance group to the failed instance groups.
     * @param instanceGroup the failed instance group.
     */
    public void addFailedInstanceGroup(InstanceGroup instanceGroup) {
        this.failedInstanceGroups.add(instanceGroup);
        this.instanceGroupNum++;
    }

    /**
     * Initialize the scheduling result map.
     * @param datacenters the datacenters.
     */
    private void initDcResultMap(List<Datacenter> datacenters) {
        this.scheduledResultMap = new HashMap<>();
        for (Datacenter datacenter : datacenters) {
            this.scheduledResultMap.put(datacenter, new ArrayList<>());
        }
    }

    /**
     * Get the scheduled datacenter of the instance group.
     * @param instanceGroup the instance group.
     * @return the scheduled datacenter of the instance group.
     */
    public Datacenter getScheduledDatacenter(InstanceGroup instanceGroup) {
        for (Map.Entry<Datacenter, List<InstanceGroup>> scheduledResult : scheduledResultMap.entrySet()) {
            if (scheduledResult.getValue().contains(instanceGroup)) {
                return scheduledResult.getKey();
            }
        }
        return Datacenter.NULL;
    }
}
