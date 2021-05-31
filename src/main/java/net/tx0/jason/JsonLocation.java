package net.tx0.jason;

public class JsonLocation {

    private final long position;
    private final int line;
    private final int column;

    JsonLocation( long position, int line, int column ) {
        this.position = position;
        this.line = line;
        this.column = column;
    }

    /**
     * The character offset into the document.
     */
    public long getPosition() {
        return position;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return line + ":" + column;
    }

}
