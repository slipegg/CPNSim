package org.cpnsim.core;

import org.cpnsim.datacenter.*;
import org.cpnsim.intrascheduler.*;
import org.cpnsim.interscheduler.*;
import org.cpnsim.statemanager.PredictionManager;
import org.cpnsim.statemanager.PredictionManagerSimple;

/**
 * A simple factory that implements the {@link Factory} interface.
 *
 * @author Jiawen Liu
 * @since LGDCloudSim 1.0
 */
public class FactorySimple implements Factory {
    @Override
    public IntraScheduler getIntraScheduler(String type, int id, int firstPartitionId, int partitionNum) {
        return switch (type) {
            case "simple", "Simple" -> new IntraSchedulerSimple(id, firstPartitionId, partitionNum);
            case "leastRequested" -> new IntraSchedulerLeastRequested(id, firstPartitionId, partitionNum);
            case "randomScore" -> new IntraSchedulerRandomScore(id, firstPartitionId, partitionNum);
            case "randomScoreByPartitionSynOrder" ->
                    new IntraSchedulerRandomScoreByPartitionSynOrder(id, firstPartitionId, partitionNum);
            case "random" -> new IntraSchedulerRandom(id, firstPartitionId, partitionNum);
            case "partitionRandom" -> new IntraSchedulerPartitionRandom(id, firstPartitionId, partitionNum);
            case "minHostOn" -> new IntraSchedulerMinHostOn(id, firstPartitionId, partitionNum);
            case "FirstFit" -> new IntraSchedulerFirstFit(id, firstPartitionId, partitionNum);
            case "multiLevel" -> new IntraSchedulerPartitionMultiLevel(id, firstPartitionId, partitionNum);
            case "fixedPartitionRandom" -> new IntraSchedulerFixedPartitionRandom(id, firstPartitionId, partitionNum);
            default -> null;
        };
    }

    @Override
    public PredictionManager getPredictionManager(String type) {
        return switch (type) {
            case "simple", "Simple" -> new PredictionManagerSimple();
            default -> null;
        };
    }

    @Override
    public InterScheduler getInterScheduler(String type, int id, Simulation simulation, int collaborationId, int target, boolean isSupportForward) {
        return switch (type) {
            case "simple", "Simple" ->
                    new InterSchedulerSimple(id, simulation, collaborationId, target, isSupportForward);
            case "leastRequested" ->
                    new InterSchedulerLeastRequested(id, simulation, collaborationId, target, isSupportForward);
            case "round" -> new InterSchedulerRound(id, simulation, collaborationId, target, isSupportForward);
            case "direct", "Direct" -> new InterSchedulerDirect(id, simulation, collaborationId);
            case "minTCODirect" -> new InterSchedulerMinTCODirect(id, simulation, collaborationId);
            case "consult", "Consult" -> new InterSchedulerConsult(id, simulation, collaborationId);
            case "heuristic", "Heuristic" -> new InterSchedulerHeuristic(id, simulation, collaborationId, target, isSupportForward);
            case "hfrs", "HFRS" -> new InterSchedulerHFRS(id, simulation, collaborationId, target, isSupportForward);
            case "rfhs", "RFHS" -> new InterSchedulerRFHS(id, simulation, collaborationId, target, isSupportForward);
            case "random", "Random" -> new InterSchedulerRandom(id, simulation, collaborationId, target, isSupportForward);
            default -> null;
        };
    }

    @Override
    public LoadBalance getLoadBalance(String type) {
        return switch (type) {
            case "round", "Round" -> new LoadBalanceRound();
            case "batch", "Batch" -> new LoadBalanceBatch();
            default -> null;
        };
    }

    @Override
    public ConflictHandler getResourceAllocateSelector(String type) {
        return switch (type) {
            case "simple", "Simple" -> new ConflictHandlerSimple();
            default -> null;
        };
    }
}
