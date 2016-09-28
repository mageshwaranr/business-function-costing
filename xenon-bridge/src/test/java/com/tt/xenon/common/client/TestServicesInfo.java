package com.tt.xenon.common.client;

import com.tt.xenon.common.VrbcServiceInfo;

/**
 * Created by mageshwaranr on 9/2/2016.
 */
public enum TestServicesInfo implements VrbcServiceInfo {

  DUMMY_SERVICE("DummySerivce", "/vrbc/test/dummy/service"),
  SYNC_MOCK("SynchronousMock", "/vrbc/xenon/util/test");

  private String name, link;

  TestServicesInfo(String name, String link) {
    this.name = name;
    this.link = link;
  }

  @Override
  public String serviceLink() {
    return link;
  }

  @Override
  public String serviceName() {
    return name;
  }


}
