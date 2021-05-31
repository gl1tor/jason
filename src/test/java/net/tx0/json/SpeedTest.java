package net.tx0.json;

import net.tx0.jason.Json;
import net.tx0.jason.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SpeedTest {

    public static void main( String[] args ) throws IOException {

        File file = new File("dump-20160920.json");
        long length = file.length();
        long t0 = System.currentTimeMillis();

        FileReader fileReader = new FileReader(file.getPath());
        try {
            JsonReader reader = Json.createReader(null, fileReader);
            while ( reader.hasNext() ) {
                reader.next();
            }
        } finally {
            fileReader.close();
        }

        long t1 = System.currentTimeMillis();

        System.out.printf( "Input file (%dMB) read in %.1fs (%.3fMB/s)\n", length>>20, (t1-t0)/1000.0, (length>>20) / ((t1-t0)/1000.0) );

    }

}
