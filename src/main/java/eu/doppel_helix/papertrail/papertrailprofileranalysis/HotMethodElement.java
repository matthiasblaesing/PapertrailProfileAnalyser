package eu.doppel_helix.papertrail.papertrailprofileranalysis;

import java.util.Objects;

public class HotMethodElement {

    private String location;
    private long selfTime;
    private long totalTime;

    public HotMethodElement() {
    }

    public HotMethodElement(String location) {
        this.location = location;
    }

    public HotMethodElement(String location, long selfTime, long totalTime) {
        this.location = location;
        this.selfTime = selfTime;
        this.totalTime = totalTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getSelfTime() {
        return selfTime;
    }

    public void setSelfTime(long selfTime) {
        this.selfTime = selfTime;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.location);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HotMethodElement other = (HotMethodElement) obj;
        if (!Objects.equals(this.location, other.location)) {
            return false;
        }
        return true;
    }

}
