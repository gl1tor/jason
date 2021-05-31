package net.tx0.jason;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

final class JsonScanner {

	static final int EOF = -1;
	static final int TOKEN_STRING = -2;
	static final int TOKEN_INTEGER = -3;
	static final int TOKEN_FRACTIONAL_NUMBER = -4;
	static final int TOKEN_TRUE = -5;
	static final int TOKEN_FALSE = -6;
	static final int TOKEN_NULL = -7;

	private static final int BUFFER_SIZE = 1 << 10;

	// using a StringBuilder allows to store unescaped text right away,
	// accessing the output buffer might be faster though.
	private final StringBuilder text = new StringBuilder();

	private final Charset defaultCharset;
	private int startLine, startColumn;
	private long startPosition;

	private int line = 1, column = 1;
	private long position;

	private InputStream inputStream;
	private Reader reader;
	private CharsetDecoder decoder;

	private ByteBuffer input;
	private CharBuffer output;
	private boolean eof;

	JsonScanner( JsonConfig config, InputStream inputStream ) {

		this.defaultCharset = config.getCharset();
		this.inputStream = inputStream;

		input = ByteBuffer.allocate(BUFFER_SIZE);
		output = CharBuffer.allocate(BUFFER_SIZE);

		input.flip();
		output.flip();
	}

	JsonScanner( JsonConfig config, Reader reader ) {

		this.defaultCharset = null;
		this.reader = reader;

		output = CharBuffer.allocate(BUFFER_SIZE);
		output.flip();
	}

	private boolean fillInputBuffer() throws IOException {

		int len;

		// input in read-mode

		input.compact();

		len = inputStream.read( input.array(), input.position(), input.remaining() );

		if ( len == EOF ) {
			input.flip();
			return false;
		}

		input.position( input.position() + len );
		input.flip();

		return true;
	}

	private boolean setupDecoder() throws IOException {

		int p, l;
		Charset cs;

		if ( !fillInputBuffer() )
			return false;

		l = input.remaining();
		p = input.position();

		if ( l > 1 && input.get( p ) == 0 ) {
			if ( input.get( p+1 ) == 0 )
				cs = Charset.forName( "UTF-32BE" );
			else
				cs = Charset.forName( "UTF-16BE" );
		} else if ( l > 2 && input.get( p ) != 0 && input.get( p+1 ) == 0 ) {
			if ( input.get( p+2 ) == 0 )
				cs = Charset.forName( "UTF-32LE" );
			else
				cs = Charset.forName( "UTF-16LE" );
		} else {
			cs = defaultCharset;
		}

		decoder = cs.newDecoder();

		return true;
	}

	private boolean fillOutputBuffer() throws IOException {

		if ( output.hasRemaining() )
			return true;

		// input in read-mode, output in read-mode

		output.compact();

		if ( reader != null ) {

			int r;

			r = reader.read( output.array(), output.position(), output.limit() );
			if ( r == -1 ) {
				output.flip();
				return false;
			}
			output.position( output.position() + r );

		} else {

			if ( !input.hasRemaining() ) {
				if ( decoder == null ) {
					if ( !setupDecoder() ) {
						output.flip();
						return false;
					}
				} else {
					eof = !fillInputBuffer();
				}
			}

			CoderResult cr;

			cr = decoder.decode(input, output, eof);

			if ( output.position() == 0 && cr.isUnderflow() && !eof ) {
				eof = !fillInputBuffer();
				cr = decoder.decode(input, output, eof);
			}

			if ( !cr.isUnderflow() )
				cr.throwException();

			if ( eof ) {
				cr = decoder.flush(output);

				if ( !cr.isUnderflow() )
					cr.throwException();
			}
		}

		output.flip();

		if ( !output.hasRemaining() ) {
			this.eof = true;
			return false;
		}

		return true;
	}

	private int peek() throws IOException {

		if ( eof || !fillOutputBuffer() )
			return EOF;

		return output.get( output.position() );
	}

	private int read() throws IOException {

		if ( eof || !fillOutputBuffer() )
			return EOF;

		int ch = output.get();

		if ( ch != EOF ) {
			position++;
			if ( ch == '\n' ) {
				line++;
				column = 0;
			}
			column++;
		}

		return ch;
	}

	void close() {
	}

	String text() {
		return text.toString();
	}
	
	private final JsonException error( String message ) {
		return new JsonSyntaxException( message, location() );
	}

	final JsonLocation location() {
		return new JsonLocation( startPosition, startLine, startColumn );
	}

	final int next() throws IOException {

		int c;

		text.setLength( 0 );

		// skip space
		do {

			startPosition = position;
			startLine = line;
			startColumn = column;

			c = read();

		} while ( c == '\n' || c == '\r' || c == ' ' || c == '\t' );

		if ( c == EOF )
			return EOF;

		switch ( c ) {
			case ',':
			case ':':
			case '{':
			case '}':
			case '[':
			case ']':
				text.appendCodePoint(c);
				return c;

			case '"':
				scanString();
				return TOKEN_STRING;

			// no leading zeros
			case '0':
				text.appendCodePoint(c);
				c = peek();
				if ( c == '.' ) {
					text.appendCodePoint( read() );
					return scanNumberFraction();
				} else if ( c == 'e' ) {
					text.appendCodePoint( read() );
					return scanNumberExponent();
				}
				return TOKEN_INTEGER;

			case '-':
				text.appendCodePoint(c);
				c = peek();
				if ( c == '0' ) {
					text.appendCodePoint( read() );
					c = peek();
					if ( c == '.' ) {
						text.appendCodePoint( read() );
						return scanNumberFraction();
					} else if ( c == 'e' ) {
						text.appendCodePoint( read() );
						return scanNumberExponent();
					}
					return TOKEN_INTEGER;
				} else if ( c >= '1' && c <= '9' ) {
					text.appendCodePoint( read() );
					return scanNumber();
				}
				throw error("invalid number (negative without integer part)");

			case '1':case '2': case '3': case '4':
			case '5':case '6':case '7': case '8': case '9':
				text.appendCodePoint(c);
				return scanNumber();

			case 't':
				if ( ( c = read() ) == 'r' )
					if ( ( c = read() ) == 'u' )
						if ( ( c = read() ) == 'e' )
							return TOKEN_TRUE;
				throw error( "Illegal content" );
			case 'f':
				if ( ( c = read() ) == 'a' )
					if ( ( c = read() ) == 'l' )
						if ( ( c = read() ) == 's' )
							if ( ( c = read() ) == 'e' )
								return TOKEN_FALSE;
				throw error( "Illegal content" );
			case 'n':
				if ( ( c = read() ) == 'u' )
					if ( ( c = read() ) == 'l' )
						if ( ( c = read() ) == 'l' )
							return TOKEN_NULL;
				throw error( "Illegal content" );

			default:
				throw error( "Illegal content '" + (char) c + "'" );
		}
	}

	private final int scanNumber() throws IOException {
		int c;
		do {
			c = peek();
			if ( !( c >= '0' && c <= '9' ) ) {
				if ( c == '.' ) {
					text.appendCodePoint( read() );
					return scanNumberFraction();
				}
				if ( c == 'e' || c == 'E' ) {
					text.appendCodePoint( read() );
					return scanNumberExponent();
				}
				return TOKEN_INTEGER;
			}
			text.appendCodePoint( read() ); 
		} while ( true );
	}

	private final int scanNumberFraction() throws IOException {
		
		int c;
		
		c = peek();
		
		if ( c < '0' || c > '9' )
			throw error( "Fractional digits expected" );
		
		do {
			text.appendCodePoint( read() );
			c = peek();
		} while ( c >= '0' && c <= '9' );
		
		if ( c == 'e' || c == 'E' ) {
			text.appendCodePoint( read() );
			return scanNumberExponent();
		}
		
		return TOKEN_FRACTIONAL_NUMBER;
	}

	private final int scanNumberExponent() throws IOException {
		
		int c;
		
		c = peek();
		
		if ( c == '-' || c == '+' ) {
			text.appendCodePoint( read() );
			c = peek();
		}
		
		if ( c < '0' || c > '9' )
			throw error( "Exponential digits expected" );

		do {
			text.appendCodePoint( read() );
			c = peek();
		} while ( c >= '0' && c <= '9' );
		
		return TOKEN_FRACTIONAL_NUMBER;
	}

	private final void scanString() throws IOException {
		int c;
		c = read();
		while ( c != '"' && c != -1 ) {
			if ( c == '\\' ) {
				c = read();
				switch ( c ) {
					case '"': text.append( '"' ); break;
					case '\\': text.append( '\\' ); break;
					case '/': text.append( '/' ); break;
					case 'b': text.append( '\b' ); break;
					case 'f': text.append( '\f' ); break;
					case 'n': text.append( '\n' ); break;
					case 'r': text.append( '\r' ); break;
					case 't': text.append( '\t' ); break;
					case 'u':
					{
						int v = 0;
						v = xdigit() << 12;
						v += xdigit() << 8;
						v += xdigit() << 4;
						v += xdigit();
						text.appendCodePoint( v ); 
						break;
					}
					default:
						throw error( "Illegal escape character" );
						
				}
			} else if ( c < 0x20 ) {
				throw error( "Control character in string" );
			} else {
				text.appendCodePoint( c );
			}
			c = read();
		}
		if ( c == -1 )
			throw error( "Unterminated string" );
	}

	private int xdigit() throws IOException {
		int c = read();
		if ( c >= '0' && c <= '9' ) return c - '0';
		if ( c >= 'a' && c <= 'f' ) return 10 + c - 'a';
		if ( c >= 'A' && c <= 'F' ) return 10 + c - 'A';
		throw error( "Illegal content" );
	}
	
}
