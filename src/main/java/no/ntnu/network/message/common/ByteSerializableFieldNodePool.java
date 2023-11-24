package no.ntnu.network.message.common;

/**
 * A serializable data structure for storing a field node pool.
 * A field node pool maps every available field node to their corresponding name.
 * The class does not provide any functionality, and its sole purpose is to simplify the syntax for working with
 * the structure.
 */
public class ByteSerializableFieldNodePool extends ByteSerializableMap<ByteSerializableInteger, ByteSerializableString> {
}
