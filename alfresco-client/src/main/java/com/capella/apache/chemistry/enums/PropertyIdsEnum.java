package com.capella.apache.chemistry.enums;

/**
 * Created by Ramesh Rajendran   on 4/24/16.
 */
public enum PropertyIdsEnum {
    SOURCE_NAME("sourceSystem", "ipt:sourceSystem"),
    SERVICE_DELIVERY_ID("serviceDeliveryId", "ipt:serviceDeliveryId"),
    DEBUG_CORRELATION_ID("debugCorrelationId", "ipt:debugCorrelationId"),
    DOCUMENT_TYPE("documentType", "ipt:documentType"),
    DOCUMENT_NAME("documentName", "ipt:documentName"),
    SOURCE_UNIQUE_REFERENCE("sourceUniqueReference", "ipt:sourceUniqueReference");

    private final String localName;
    private final String id;

    PropertyIdsEnum(String localName, String id) {
        this.localName = localName;
        this.id = id;
    }

    public String getLocalName() {
        return localName;
    }

    public String getId() {
        return id;
    }
}
