package org.cpnsim.datacenter;

import org.cpnsim.core.SimEntity;
import org.cpnsim.intrascheduler.IntraScheduler;
import org.cpnsim.interscheduler.InterScheduler;
import org.cpnsim.request.InstanceGroup;
import org.cpnsim.statemanager.StatesManager;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Set;

/**
 * An interface to be implemented by each class that represents a datacenter.
 * It extends {@link SimEntity} and {@link DatacenterPrice}.
 * The mainly components of a datacenter are the {@link InterScheduler}, {@link LoadBalance}, {@link IntraScheduler}, {@link ConflictHandler}, and {@link StatesManager}.
 *
 * @author Jiawen Liu
 * @since LGDCloudSim 1.0
 */
public interface Datacenter extends SimEntity, DatacenterPrice {
    /**
     * the NULL datacenter.
     **/
    public static final Datacenter NULL = new DatacenterNull();

    /** Get the collaborationIds to which the datacenter belongs. **/
    Set<Integer> getCollaborationIds();

    /** Set the {@link InterScheduler}. **/
    Datacenter setInterScheduler(InterScheduler interScheduler);

    /**
     * Set the {@link IntraScheduler}.
     **/
    Datacenter setIntraSchedulers(List<IntraScheduler> intraSchedulers);

    /** Get the {@link InterScheduler}. **/
    List<IntraScheduler> getIntraSchedulers();

    /** Set the {@link LoadBalance}. **/
    Datacenter setLoadBalance(LoadBalance loadBalance);

    /** Get the {@link LoadBalance}. **/
    LoadBalance getLoadBalance();

    /**
     * Set the {@link ConflictHandler}.
     **/
    Datacenter setConflictHandler(ConflictHandler conflictHandler);

    /**
     * Get the {@link ConflictHandler}.
     **/
    ConflictHandler getConflictHandler();

    /**
     * Set the {@link StatesManager}.
     **/
    Datacenter setStatesManager(StatesManager statesManager);

    /**
     * Get the {@link StatesManager}.
     **/
    StatesManager getStatesManager();

    /**
     * Set whether the inter-architecture is centralized.
     * If there is a centralized inter-scheduler in the datacenter, the inter-architecture is centralized.
     *
     * @param centralizedInterScheduleFlag whether the inter-architecture is centralized.
     * @return the datacenter.
     */
    Datacenter setCentralizedInterScheduleFlag(boolean centralizedInterScheduleFlag);

    /**
     * Get whether the inter-architecture is centralized.
     **/
    boolean isCentralizedInterSchedule();

    /**
     * Get the estimated TCO of the instance group.
     * @param instanceGroup the instance group.
     * @return the estimated TCO.
     */
    double getEstimatedTCO(InstanceGroup instanceGroup);

    /**
     * Get the instance queue of the datacenter.
     * The instance queue is used to store the instances which are waiting loadbalancer to distribute them to the intra-schedulers.
     * @return the estimated TCO.
     */
    InstanceQueue getInstanceQueue();

    /**
     * Get the region of the datacenter.
     * @return the region of the datacenter.
     */
    String getRegion();

    /**
     * Set the region of the datacenter.
     * @param region the region of the datacenter.
     * @return the datacenter.
     */
    Datacenter setRegion(String region);

    /**
     * Get the location of the datacenter.
     * @return the location of the datacenter.
     */
    Point2D getLocation();

    /**
     * Set the location of the datacenter.
     * @param latitude the latitude of the datacenter.
     * @param longitude the longitude of the datacenter.
     * @return the datacenter.
     */
    Datacenter setLocation(double latitude, double longitude);

    /**
     * Get the architecture of the datacenter.
     * @return the architecture of the datacenter.
     */
    String getArchitecture();

    /**
     * Set the architecture of the datacenter.
     * @param architecture the architecture of the datacenter.
     * @return the datacenter.
     */
    Datacenter setArchitecture(String architecture);
}
