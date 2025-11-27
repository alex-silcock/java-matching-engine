/* Generated SBE (Simple Binary Encoding) message codec. */
package baseline;

@SuppressWarnings("all")
public enum STPFInstruction
{
    RTO((short)0),

    RRO((short)1),

    RBO((short)2),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((short)255);

    private final short value;

    STPFInstruction(final short value)
    {
        this.value = value;
    }

    /**
     * The raw encoded value in the Java type representation.
     *
     * @return the raw value encoded.
     */
    public short value()
    {
        return value;
    }

    /**
     * Lookup the enum value representing the value.
     *
     * @param value encoded to be looked up.
     * @return the enum value representing the value.
     */
    public static STPFInstruction get(final short value)
    {
        switch (value)
        {
            case 0: return RTO;
            case 1: return RRO;
            case 2: return RBO;
            case 255: return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
