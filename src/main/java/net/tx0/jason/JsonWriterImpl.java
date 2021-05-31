package net.tx0.jason;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

final class JsonWriterImpl implements JsonWriter {

    // TODO Implementation is inefficient, i.e. outer class access
    // TODO layout is not the prettiest when consisting of small arrays

    private abstract class Context {
        protected int level;

        protected final void writeIndent( Writer writer, int lvl ) throws IOException {
            if ( !layout )
                return;
            for ( int i = 0; i < lvl; i++ )
                writer.write("    ");
        }

        public void writeStartObject() throws IOException {
            throw new JsonException("Object not permitted here");
        }

        public void writeStartArray() throws IOException {
            throw new JsonException("Array not permitted here");
        }

        public void writeMember( String name ) throws IOException {
            throw new JsonException("Attribute not permitted here");
        }

        public void writeScalar( String string ) throws IOException {
            throw new JsonException("Scalar not permitted here");
        }

        public void writeEndObject() throws IOException {
            throw new JsonException("Not within an object");
        }

        public void writeEndArray() throws IOException {
            throw new JsonException("Not within an array");
        }

        public void writeEndText() {
            throw new JsonException("Unbalanced text");
        }

    }

    private class TextContext extends Context {
        int state = 0;

        @Override
        public void writeStartObject() throws IOException {
            if ( state != 0 )
                throw new JsonException("Only one text content permitted");
            writer.write('{');
            if ( layout )
                writer.write('\n');
            state = 1;
        }

        @Override
        public void writeStartArray() throws IOException {
            if ( state != 0 )
                throw new JsonException("Only one text content permitted");
            writer.write('[');
            if ( layout )
                writer.write('\n');
            state = 1;
        }

        public void writeEndText() {
        }

        public void writeScalar( String string ) throws IOException {
            if ( state != 0 )
                throw new JsonException("Only one text content permitted");
            // super.writeScalar throws / text previously needed to be complex
            writer.write(string);
            state = 1;
        }
    }

    private class ObjectContext extends Context {

        int state, index;

        @Override
        public void writeEndObject() throws IOException {
            if ( state == 1 )
                throw new JsonException("Missing member value");
            if ( index > 0 && layout ) {
                writer.write('\n');
            }
            writeIndent(writer, level - 1);
            writer.write('}');
        }

        @Override
        public void writeMember( String name ) throws IOException {
            if ( state == 1 )
                throw new JsonException("Missing member value");
            if ( index > 0 ) {
                writer.write(',');
                if ( layout ) {
                    writer.write('\n');
                }
            }
            writeIndent(writer, level);
            writer.write(toScalar(name));
            writer.write(':');
            if ( layout )
                writer.write(' ');
            state = 1;
            index++;
        }

        @Override
        public void writeStartObject() throws IOException {
            if ( state == 0 )
                throw new JsonException("No value expected");
            writer.write('{');
            if ( layout )
                writer.write('\n');
            state = 0;
        }

        @Override
        public void writeStartArray() throws IOException {
            if ( state == 0 )
                throw new JsonException("No value expected");
            writer.write('[');
            if ( layout )
                writer.write('\n');
            state = 0;
        }

        @Override
        public void writeScalar( String string ) throws IOException {
            if ( state == 0 )
                throw new JsonException("No value expected");
            writer.write(string);
            state = 0;
        }

    }

    private class ArrayContext extends Context {
        int index = 0;

        @Override
        public void writeEndArray() throws IOException {
            if ( index > 0 && layout ) {
                writer.write('\n');
            }
            writeIndent(writer, level - 1);
            writer.write(']');
        }

        @Override
        public void writeStartObject() throws IOException {
            prepareWrite();
            writer.write('{');
            if ( layout )
                writer.write('\n');
            index++;
        }

        @Override
        public void writeStartArray() throws IOException {
            prepareWrite();
            writer.write('[');
            if ( layout )
                writer.write('\n');
            index++;
        }

        @Override
        public void writeScalar( String string ) throws IOException {
            prepareWrite();
            writer.write(string);
            index++;
        }

        private void prepareWrite() throws IOException {
            if ( index > 0 ) {
                writer.write(',');
                if ( layout )
                    writer.write('\n');
            }
            writeIndent(writer, level);
        }
    }

    private final Writer writer;
    private final boolean layout;
    // keeps track of the current context
    private final Deque<Context> nodes = new ArrayDeque<Context>();

    JsonWriterImpl( JsonConfig config, Writer writer ) {
        this.layout = config.isLayout();
        this.writer = writer;
        this.nodes.push(new TextContext());
    }

    private Context currentNode() {
        try {
            return nodes.element();
        } catch ( NoSuchElementException e ) {
            throw new JsonException("Writing beyond text");
        }
    }

    // methods just delegate to the current context

    @Override
    public void writeNull() {
        try {
            currentNode().writeScalar("null");
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }
    }

    @Override
    public void write( String string ) {
        try {
            currentNode().writeScalar(toScalar(string));
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }
    }

    @Override
    public void write( boolean bool ) {
        try {
            currentNode().writeScalar(toScalar(bool));
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }
    }

    @Override
    public void write( Number number ) {
        try {
            currentNode().writeScalar(toScalar(number));
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }
    }

    @Override
    public void writeBeginArray() {
        try {
            currentNode().writeStartArray();
            ArrayContext n = new ArrayContext();
            n.level = nodes.size();
            nodes.push(n);
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }
    }

    @Override
    public void writeEndArray() {
        try {
            currentNode().writeEndArray();
            nodes.pop();
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }
    }

    @Override
    public void writeStartObject() {
        try {
            currentNode().writeStartObject();
            ObjectContext n = new ObjectContext();
            n.level = nodes.size();
            nodes.push(n);
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }
    }

    @Override
    public void writeEndObject() {
        try {
            currentNode().writeEndObject();
            nodes.pop();
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }
    }

    @Override
    public void writeMember( String name ) {
        try {
            currentNode().writeMember(name);
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }
    }

    @Override
    public void writeLayout( String layout ) {

        for ( int i = 0, l = layout.length(); i < l; ++i ) {
            if ( !isWhitespace(layout.charAt(i)) ) {
                throw new JsonException("Layout contains non-whitespace character");
            }
        }

        try {
            writer.write(layout);
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }

    }

    private static boolean isWhitespace( char ch ) {
        switch ( ch ) {
            case ' ':
            case '\t':
            case '\r':
            case '\n':
                return true;
        }
        return false;
    }

    @Override
    public void close() {
        currentNode().writeEndText();
        try {
            writer.flush();
        } catch ( IOException e ) {
            throw JsonException.wrap(e);
        }
    }

    private static final char hexDigit( int c ) {
        if ( c < 10 ) return (char) ( '0' + c );
        return (char) ( 'A' + ( c - 10 ) );
    }

    private static final String toScalar( Number value ) {
        return value.toString();
    }

    private static final String toScalar( boolean value ) {
        return value ? "true" : "false";
    }

    protected static final String toScalar( String value ) {
        StringBuilder writer = new StringBuilder();
        if ( value == null ) {
            writer.append("null");
        } else {
            writer.append('"');
            char c;
            int l = value.length();
            for ( int i = 0; i < l; i++ ) {
                c = value.charAt(i);
                if ( c == '"' || c == '\\' ) {
                    writer.append('\\');
                    writer.append(c);
                } else if ( c < 0x20 ) {
                    writer.append('\\');
                    switch ( c ) {
                        case '\n':
                            writer.append('n');
                            break;
                        case '\r':
                            writer.append('r');
                            break;
                        case '\t':
                            writer.append('t');
                            break;
                        case '\b':
                            writer.append('b');
                            break;
                        case '\f':
                            writer.append('f');
                            break;
                        default:
                            writer.append('u');
                            writer.append(hexDigit(( ( (int) c ) >> 12 ) & 0x0f));
                            writer.append(hexDigit(( ( (int) c ) >> 8 ) & 0x0f));
                            writer.append(hexDigit(( ( (int) c ) >> 4 ) & 0x0f));
                            writer.append(hexDigit(( ( (int) c ) >> 0 ) & 0x0f));
                    }
                } else {
                    writer.append(c);
                }
            }
            writer.append('"');
        }
        return writer.toString();
    }
}
