package org.cpnsim.innerscheduler;

import org.cpnsim.request.Instance;
import org.cpnsim.statemanager.SynState;

import java.util.*;

public class InnerSchedulerRandom extends InnerSchedulerSimple {
    Random random = new Random();

    public InnerSchedulerRandom(int id, int firstPartitionId, int partitionNum) {
        super(id, firstPartitionId, partitionNum);
    }

    @Override
    protected InnerSchedulerResult scheduleInstances(List<Instance> instances, SynState synState) {
        InnerSchedulerResult innerSchedulerResult = new InnerSchedulerResult(this, getDatacenter().getSimulation().clock());

        int hostNum = datacenter.getStatesManager().getHostNum();

        for (Instance instance : instances) {
            int suitId = -1;

            int startHostId = random.nextInt(hostNum);
            for (int i = 0; i < hostNum; i++) {
                int hostId = (startHostId + i) % hostNum;
                if (synState.isSuitable(hostId, instance)) {
                    suitId = hostId;
                    break;
                }
            }

            if (suitId != -1) {
                synState.allocateTmpResource(suitId, instance);
                instance.setExpectedScheduleHostId(suitId);
                innerSchedulerResult.addScheduledInstance(instance);
            } else {
                innerSchedulerResult.addFailedScheduledInstance(instance);
            }
        }

        return innerSchedulerResult;
    }
}
