import jcifs.netbios.NbtAddress;
import jcifs.smb.SmbFile;
import java.util.Date;

public class List {

    public static void main( String[] argv ) throws Exception {

        SmbFile file = new SmbFile( argv[0] );

        long t1 = System.currentTimeMillis();
        SmbFile[] files = file.listFiles();
        long t2 = System.currentTimeMillis() - t1;

        for( int i = 0; i < files.length; i++ ) {
            System.out.println( " " + files[i].getURL().toExternalForm()  + " ");
        }
        System.out.println();
        System.out.println( files.length + " files in " + t2 + "ms" );
    }
}
