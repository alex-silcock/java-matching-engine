/* Generated SBE (Simple Binary Encoding) message codec. */
package baseline;

import org.agrona.DirectBuffer;

@SuppressWarnings("all")
public final class OrderDecoder
{
    public static final int BLOCK_LENGTH = 21;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final String SEMANTIC_VERSION = "";
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final OrderDecoder parentMessage = this;
    private DirectBuffer buffer;
    private int offset;
    private int limit;
    int actingBlockLength;
    int actingVersion;

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

    public DirectBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public OrderDecoder wrap(
        final DirectBuffer buffer,
        final int offset,
        final int actingBlockLength,
        final int actingVersion)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        this.actingBlockLength = actingBlockLength;
        this.actingVersion = actingVersion;
        limit(offset + actingBlockLength);

        return this;
    }

    public OrderDecoder wrapAndApplyHeader(
        final DirectBuffer buffer,
        final int offset,
        final MessageHeaderDecoder headerDecoder)
    {
        headerDecoder.wrap(buffer, offset);

        final int templateId = headerDecoder.templateId();
        if (TEMPLATE_ID != templateId)
        {
            throw new IllegalStateException("Invalid TEMPLATE_ID: " + templateId);
        }

        return wrap(
            buffer,
            offset + MessageHeaderDecoder.ENCODED_LENGTH,
            headerDecoder.blockLength(),
            headerDecoder.version());
    }

    public OrderDecoder sbeRewind()
    {
        return wrap(buffer, offset, actingBlockLength, actingVersion);
    }

    public int sbeDecodedLength()
    {
        final int currentLimit = limit();
        sbeSkip();
        final int decodedLength = encodedLength();
        limit(currentLimit);

        return decodedLength;
    }

    public int actingVersion()
    {
        return actingVersion;
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


    public byte ticker(final int index)
    {
        if (index < 0 || index >= 4)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = offset + 0 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String tickerCharacterEncoding()
    {
        return java.nio.charset.StandardCharsets.US_ASCII.name();
    }

    public int getTicker(final byte[] dst, final int dstOffset)
    {
        final int length = 4;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(offset + 0, dst, dstOffset, length);

        return length;
    }

    public String ticker()
    {
        final byte[] dst = new byte[4];
        buffer.getBytes(offset + 0, dst, 0, 4);

        int end = 0;
        for (; end < 4 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public int getTicker(final Appendable value)
    {
        for (int i = 0; i < 4; ++i)
        {
            final int c = buffer.getByte(offset + 0 + i) & 0xFF;
            if (c == 0)
            {
                return i;
            }

            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException ex)
            {
                throw new java.io.UncheckedIOException(ex);
            }
        }

        return 4;
    }


    public static int qtyId()
    {
        return 2;
    }

    public static int qtySinceVersion()
    {
        return 0;
    }

    public static int qtyEncodingOffset()
    {
        return 4;
    }

    public static int qtyEncodingLength()
    {
        return 8;
    }

    public static String qtyMetaAttribute(final MetaAttribute metaAttribute)
    {
        if (MetaAttribute.PRESENCE == metaAttribute)
        {
            return "required";
        }

        return "";
    }

    public static double qtyNullValue()
    {
        return Double.NaN;
    }

    public static double qtyMinValue()
    {
        return -1.7976931348623157E308d;
    }

    public static double qtyMaxValue()
    {
        return 1.7976931348623157E308d;
    }

    public double qty()
    {
        return buffer.getDouble(offset + 4, BYTE_ORDER);
    }


    public static int sideId()
    {
        return 3;
    }

    public static int sideSinceVersion()
    {
        return 0;
    }

    public static int sideEncodingOffset()
    {
        return 12;
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

    public short sideRaw()
    {
        return ((short)(buffer.getByte(offset + 12) & 0xFF));
    }

    public OrderSide side()
    {
        return OrderSide.get(((short)(buffer.getByte(offset + 12) & 0xFF)));
    }


    public static int priceId()
    {
        return 4;
    }

    public static int priceSinceVersion()
    {
        return 0;
    }

    public static int priceEncodingOffset()
    {
        return 13;
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

    public double price()
    {
        return buffer.getDouble(offset + 13, BYTE_ORDER);
    }


    public String toString()
    {
        if (null == buffer)
        {
            return "";
        }

        final OrderDecoder decoder = new OrderDecoder();
        decoder.wrap(buffer, offset, actingBlockLength, actingVersion);

        return decoder.appendTo(new StringBuilder()).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        if (null == buffer)
        {
            return builder;
        }

        final int originalLimit = limit();
        limit(offset + actingBlockLength);
        builder.append("[Order](sbeTemplateId=");
        builder.append(TEMPLATE_ID);
        builder.append("|sbeSchemaId=");
        builder.append(SCHEMA_ID);
        builder.append("|sbeSchemaVersion=");
        if (parentMessage.actingVersion != SCHEMA_VERSION)
        {
            builder.append(parentMessage.actingVersion);
            builder.append('/');
        }
        builder.append(SCHEMA_VERSION);
        builder.append("|sbeBlockLength=");
        if (actingBlockLength != BLOCK_LENGTH)
        {
            builder.append(actingBlockLength);
            builder.append('/');
        }
        builder.append(BLOCK_LENGTH);
        builder.append("):");
        builder.append("ticker=");
        for (int i = 0; i < tickerLength() && this.ticker(i) > 0; i++)
        {
            builder.append((char)this.ticker(i));
        }
        builder.append('|');
        builder.append("qty=");
        builder.append(this.qty());
        builder.append('|');
        builder.append("side=");
        builder.append(this.side());
        builder.append('|');
        builder.append("price=");
        builder.append(this.price());

        limit(originalLimit);

        return builder;
    }
    
    public OrderDecoder sbeSkip()
    {
        sbeRewind();

        return this;
    }
}
