package net.tx0.jason;

import java.math.BigDecimal;

public abstract class JsonNumber extends JsonScalar {

    public static JsonNumber createNumber( int number ) {
        return new JsonLong( number );
    }

    public static JsonNumber createNumber( long number ) {
        return new JsonLong( number );
    }

    public static JsonNumber createNumber( double number ) {
        return new JsonDouble( number );
    }

    public static JsonNumber createNumber( Number number ) {
        if ( number instanceof Long ) {
            return new JsonLong((Long) number);
        } else if ( number instanceof Integer ) {
            return new JsonLong((Long) number);
        } else if ( number instanceof Double ) {
            return new JsonDouble((Double) number);
        } else if ( number instanceof BigDecimal ) {
            return new JsonBigDecimal((BigDecimal) number);
        } else {
            return new JsonBigDecimal( new BigDecimal( number.toString() ) );
        }
    }

    @Override
    public abstract Number asNumber();

    @Override
    final void write(JsonWriter writer) {
        writer.write(asNumber());
    }

    @Override
    public final JsonValueType getType() {
        return JsonValueType.NUMBER;
    }

    private static class JsonBigDecimal extends JsonNumber {

        private final BigDecimal value;

        JsonBigDecimal( BigDecimal value ) {
            this.value = value;
        }

        @Override
        public Number asNumber() {
            return value;
        }

    }

    private static class JsonLong extends JsonNumber {

        private final long value;

        JsonLong(long value) {
            this.value = value;
        }

        @Override
        public Number asNumber() {
            return value;
        }

        @Override
        public int asInteger() {
            return (int) value;
        }

        @Override
        public long asLong() {
            return value;
        }

        @Override
        public double asDouble() {
            return value;
        }

    }

    private static class JsonDouble extends JsonNumber {

        private final double value;

        JsonDouble(double value) {
            this.value = value;
        }

        @Override
        public Number asNumber() {
            return value;
        }

        @Override
        public int asInteger() {
            return (int) value;
        }

        @Override
        public long asLong() {
            return (long) value;
        }

        @Override
        public double asDouble() {
            return value;
        }

    }

}
