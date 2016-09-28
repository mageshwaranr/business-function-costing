package com.tt.xenon.common;

import static com.vmware.xenon.common.ServiceHost.SERVICE_URI_SUFFIX_AVAILABLE;

/**
 * Created by mageshwaranr on 8/26/2016.
 */
public interface VrbcServiceInfo {
  /**
   * Returns self serviceLink
   *
   * @return
   */
  String serviceLink();

  /**
   * Returns the document id from the document self serviceLink
   * If documentSelfLink is /core/examples/74c34cae-2837-4932-aab8-06aeee2a00d1 and
   * service serviceLink is /core/examples then this method returns 74c34cae-2837-4932-aab8-06aeee2a00d1
   *
   * @param documentSelfLink
   * @return
   */
  default String selfLinkToDocId(String documentSelfLink) {
    return documentSelfLink.replace(serviceLink() + "/", "");
  }

  /**
   * Returns the  documentSelfLink given docId
   * If docId is 74c34cae-2837-4932-aab8-06aeee2a00d1 and
   * service serviceLink is /core/examples then this method returns /core/examples/74c34cae-2837-4932-aab8-06aeee2a00d1
   *
   * @param docId
   * @return
   */
  default String docIdToSelfLink(String docId) {
    return serviceLink() + "/" + docId;
  }


  /**
   * Name of the service
   *
   * @return
   */
  String serviceName();

  default boolean hasAvailability() {
    return false;
  }

  default String[] tags() {
    return new String[] {"VrbcServices"};
  }


  default String healthCheckLink() {
    return serviceLink() + SERVICE_URI_SUFFIX_AVAILABLE;
  }


}
