/*******************************************************************************
 * JMMC project
 *
 * "@(#) $Id: OIFitsLoaderTest.java,v 1.2 2010-06-01 16:03:55 bourgesl Exp $"
 *
 * History
 * -------
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2010/04/28 14:39:20  bourgesl
 * basic test cases for OIValidator Viewer/Validator and new OIFitsLoader
 *
 */
package fr.jmmc.oitools.test;

import fr.jmmc.oitools.model.OIFitsFile;
import fr.jmmc.oitools.model.OIFitsLoader;
import java.io.File;

/**
 * This class provides test cases for the OIFitsLoader class
 * @author bourgesl
 */
public class OIFitsLoaderTest implements TestEnv {

  /**
   * Forbidden constructor
   */
  private OIFitsLoaderTest() {
    super();
  }

  public static void main(String[] args) {
    int n = 0;
    int errors = 0;

    if (false) {
      // Bad File path :
//      final String file = TEST_DIR + "toto";

      // Invalid OI Fits (Fits image) :
//      final String file = TEST_DIR + "other/YSO_disk.fits.gz";

      // Complex visibilities in VISDATA / VISERR (OI_VIS table) :
//      final String file = TEST_DIR + "ASPRO-STAR_1-AMBER-08-OCT-2009T08:17:39.fits";

      // 1 extra byte at the End of file + NaN in vis* data :
      final String file = TEST_DIR + "Mystery-Med_H-AmberVISPHI.oifits.gz";

      // Single Wave Length => NWAVE = 1 => 1D arrays instead of 2D arrays :
//      final String file = TEST_DIR + "2004-data2.fits";

      n++;
      errors += load(file);
    }

    if (true) {
      final File directory = new File(TEST_DIR);
      if (directory.exists() && directory.isDirectory()) {

        final long start = System.nanoTime();

        final File[] files = directory.listFiles();

        for (File f : files) {
          if (f.isFile() && (f.getName().endsWith("fits") || f.getName().endsWith("fits.gz"))) {
            n++;
            errors += load(f.getAbsolutePath());
          }
        }

        System.out.println("dumpDirectory : duration = " + 1e-6d * (System.nanoTime() - start) + " ms.");
      }
    }

    System.out.println("Errors = " + errors + " on " + n + " files.");
  }

  private static int load(final String absFilePath) {
    int error = 0;
    try {
      System.out.println("Loading file : " + absFilePath);

      final long start = System.nanoTime();

      final OIFitsFile oiFitsFile = OIFitsLoader.loadOIFits(absFilePath);

      System.out.println("load : toString : \n" + oiFitsFile.toString());

      if (true) {
        final boolean detailled = false;
        System.out.println("load : XML DESC : \n" + oiFitsFile.getXmlDesc(detailled));
      }

      System.out.println("load : duration = " + 1e-6d * (System.nanoTime() - start) + " ms.");

    } catch (Throwable th) {
      System.out.println("load : IO failure occured while reading file : " + absFilePath);
      th.printStackTrace(System.out);
      if (th.getCause() != null) {
        th.getCause().printStackTrace(System.out);
      }
      error = 1;
    }
    return error;
  }
}
