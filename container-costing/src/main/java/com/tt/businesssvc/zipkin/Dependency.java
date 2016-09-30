package com.tt.businesssvc.zipkin;

/**
 * Created by sivarajm on 9/29/2016.
 */
public class Dependency {

    private String parent, child;
    private long callCount;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getChild() {
        return child;
    }

    public void setChild(String child) {
        this.child = child;
    }

    public long getCallCount() {
        return callCount;
    }

    public void setCallCount(long callCount) {
        this.callCount = callCount;
    }
}
