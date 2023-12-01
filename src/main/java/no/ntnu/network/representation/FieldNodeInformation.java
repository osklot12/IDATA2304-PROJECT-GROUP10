package no.ntnu.network.representation;

import no.ntnu.fieldnode.device.DeviceClass;

import java.util.Map;

/**
 * A record storing essential information about a field node.
 * The record is designed as an efficient and immutable data carrier for information about a specific field node.
 *
 * @param fnst the field node system table
 * @param fnsm the field node status map
 * @param name the name of the field node
 */
public record FieldNodeInformation(Map<Integer, DeviceClass> fnst, Map<Integer, Integer> fnsm, String name) {
    public FieldNodeInformation {
        if (fnst == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeInformation, because fnst is null.");
        }

        if (fnsm == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeInformation, because fnsm is null.");
        }

        if (name == null) {
            throw new IllegalArgumentException("Cannot create FieldNodeInformation, because name is null.");
        }
    }
}
