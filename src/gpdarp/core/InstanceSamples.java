package gpdarp.core;

import java.util.ArrayList;
import java.util.List;

/**
 * A set of instance samples. It includes
 *  - a base instance, which contains all the state information for the problem,
 *  - a list of random seeds.
 * One random seed corresponds to a sampled instance.
 * The sampled instances are generated on-the-fly to save space.
 */

// TODO: Is this class necessary?
public class InstanceSamples {
    private Instance baseInstance;
    private List<Long> seeds;

    public InstanceSamples(Instance baseInstance, List<Long> seeds) {
        this.baseInstance = baseInstance;
        this.seeds = seeds;
    }

    public InstanceSamples(Instance baseInstance) {
        this(baseInstance, new ArrayList<>());
    }

    public Instance getBaseInstance() {
        return baseInstance;
    }

    public void setBaseInstance(Instance baseInstance) {
        this.baseInstance = baseInstance;
    }

    public List<Long> getSeeds() {
        return seeds;
    }

    public void setSeeds(List<Long> seeds) {
        this.seeds = seeds;
    }

    public void setSeed(int index, long seed) {
        seeds.set(index, seed);
    }

    public long getSeed(int index) {
        return seeds.get(index);
    }

    public void addSeed(long seed) {
        seeds.add(seed);
    }
}
