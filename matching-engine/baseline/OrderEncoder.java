/* Generated SBE (Simple Binary Encoding) message codec. */
package baseline;

import org.agrona.MutableDirectBuffer;

@SuppressWarnings("all")
public final class OrderEncoder
{
    public static final int BLOCK_LENGTH = 45;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "";
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final OrderEncoder parentMessage = this;
    private MutableDirectBuffer buffer;
    private int offset;
    private int limit;

    public int sbeBlockLength()
    {
        return BLOCK_LENGTH;
    }

    public int sbeTemplateId()
    {
        return TEMPLATE_ID;
    }

    public int sbeSchemaId()
    {
        return SCHEMA_ID;
    }

    public int sbeSchemaVersion()
    {
        return SCHEMA_VERSION;
    }

    public String sbeSemanticType()
    {
        return "";
    }

    public MutableDirectBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public OrderEncoder wrap(final MutableDirectBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);

        return this;
    }

    public OrderEncoder wrapAndApplyHeader(
        final MutableDirectBuffer buffer, final int offset, final MessageHeaderEncoder headerEncoder)
    {
        headerEncoder
            .wrap(buffer, offset)
            .blockLength(BLOCK_LENGTH)
            .templateId(TEMPLATE_ID)
            .schemaId(SCHEMA_ID)
            .version(SCHEMA_VERSION);

        return wrap(buffer, offset + MessageHeaderEncoder.ENCODED_LENGTH);
    }

    public int encodedLength()
    {
        return limit - offset;
    }

    public int limit()
    {
        return limit;
    }

    public void limit(final int limit)
    {
        this.limit = limit;
    }

    public static int tickerId()
    {
        return 1;
    }

    public static int tickerSinceVersion()
    {
        return 0;
    }

    public static int tickerEncodingOffset()
    {
        return 0;
    }

    public static int tickerEncodingLength()
    {
        return 4;
    }

    public static String tickerMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static byte tickerNullValue()
    {
        return (byte)0;
    }

    public static byte tickerMinValue()
    {
        return (byte)32;
    }

    public static byte tickerMaxValue()
    {
        return (byte)126;
    }

    public static int tickerLength()
    {
        return 4;
    }


    public OrderEncoder ticker(final int index, final byte value)
    {
        if (index < 0 || index >= 4)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 0 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }
    public OrderEncoder putTicker(final byte value0, final byte value1, final byte value2, final byte value3)
    {
        buffer.putByte(offset + 0, value0);
        buffer.putByte(offset + 1, value1);
        buffer.putByte(offset + 2, value2);
        buffer.putByte(offset + 3, value3);

        return this;
    }

    public static String tickerCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.US_ASCII.name();
    }

    public OrderEncoder putTicker(final byte[] src, final int srcOffset)
    {
        final int length = 4;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(offset + 0, src, srcOffset, length);

        return this;
    }

    public OrderEncoder ticker(final String src)
    {
        final int length = 4;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(offset + 0, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(offset + 0 + start, (byte)0);
        }

        return this;
    }

    public OrderEncoder ticker(final CharSequence src)
    {
        final int length = 4;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(offset + 0, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(offset + 0 + start, (byte)0);
        }

        return this;
    }

    public static int sizeId()
    {
        return 2;
    }

    public static int sizeSinceVersion()
    {
        return 0;
    }

    public static int sizeEncodingOffset()
    {
        return 4;
    }

    public static int sizeEncodingLength()
    {
        return 8;
    }

    public static String sizeMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static double sizeNullValue()
    {
        return Double.NaN;
    }

    public static double sizeMinValue()
    {
        return -1.7976931348623157E308d;
    }

    public static double sizeMaxValue()
    {
        return 1.7976931348623157E308d;
    }

    public OrderEncoder size(final double value)
    {
        buffer.putDouble(offset + 4, value, BYTE_ORDER);
        return this;
    }


    public static int orderTimeId()
    {
        return 3;
    }

    public static int orderTimeSinceVersion()
    {
        return 0;
    }

    public static int orderTimeEncodingOffset()
    {
        return 12;
    }

    public static int orderTimeEncodingLength()
    {
        return 8;
    }

    public static String orderTimeMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long orderTimeNullValue()
    {
        return 0xffffffffffffffffL;
    }

    public static long orderTimeMinValue()
    {
        return 0x0L;
    }

    public static long orderTimeMaxValue()
    {
        return 0xfffffffffffffffeL;
    }

    public OrderEncoder orderTime(final long value)
    {
        buffer.putLong(offset + 12, value, BYTE_ORDER);
        return this;
    }


    public static int tradeIdId()
    {
        return 4;
    }

    public static int tradeIdSinceVersion()
    {
        return 0;
    }

    public static int tradeIdEncodingOffset()
    {
        return 20;
    }

    public static int tradeIdEncodingLength()
    {
        return 8;
    }

    public static String tradeIdMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    private final TradeIdEncoder tradeId = new TradeIdEncoder();

    public TradeIdEncoder tradeId()
    {
        tradeId.wrap(buffer, offset + 20);
        return tradeId;
    }

    public static int sideId()
    {
        return 5;
    }

    public static int sideSinceVersion()
    {
        return 0;
    }

    public static int sideEncodingOffset()
    {
        return 28;
    }

    public static int sideEncodingLength()
    {
        return 1;
    }

    public static String sideMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public OrderEncoder side(final OrderSide value)
    {
        buffer.putByte(offset + 28, (byte)value.value());
        return this;
    }

    public static int priceId()
    {
        return 6;
    }

    public static int priceSinceVersion()
    {
        return 0;
    }

    public static int priceEncodingOffset()
    {
        return 29;
    }

    public static int priceEncodingLength()
    {
        return 8;
    }

    public static String priceMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static double priceNullValue()
    {
        return Double.NaN;
    }

    public static double priceMinValue()
    {
        return -1.7976931348623157E308d;
    }

    public static double priceMaxValue()
    {
        return 1.7976931348623157E308d;
    }

    public OrderEncoder price(final double value)
    {
        buffer.putDouble(offset + 29, value, BYTE_ORDER);
        return this;
    }


    public static int orderReceivedTimeId()
    {
        return 7;
    }

    public static int orderReceivedTimeSinceVersion()
    {
        return 0;
    }

    public static int orderReceivedTimeEncodingOffset()
    {
        return 37;
    }

    public static int orderReceivedTimeEncodingLength()
    {
        return 8;
    }

    public static String orderReceivedTimeMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static long orderReceivedTimeNullValue()
    {
        return 0xffffffffffffffffL;
    }

    public static long orderReceivedTimeMinValue()
    {
        return 0x0L;
    }

    public static long orderReceivedTimeMaxValue()
    {
        return 0xfffffffffffffffeL;
    }

    public OrderEncoder orderReceivedTime(final long value)
    {
        buffer.putLong(offset + 37, value, BYTE_ORDER);
        return this;
    }


    public String toString()
    {
        if (null == buffer)
        {
            return "";
        }

        return appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        if (null == buffer)
        {
            return builder;
        }

        final OrderDecoder decoder = new OrderDecoder();
        decoder.wrap(buffer, offset, BLOCK_LENGTH, SCHEMA_VERSION);

        return decoder.appendTo(builder);
    }
}
