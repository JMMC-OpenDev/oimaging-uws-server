/*******************************************************************************
 * JMMC project ( http://www.jmmc.fr ) - Copyright (C) CNRS.
 ******************************************************************************/
package fr.jmmc.oitools.image;

import java.util.LinkedList;
import java.util.List;

/**
 * This class represents the data model of a Fits standard file containg one or multiple images.
 * @author bourgesl
 */
public final class FitsImageFile {
    /* constants */

    /** Logger associated to image classes */
    private final static java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FitsImageFile.class.getName());

    /* members */
    /** absolute file path */
    private String absoluteFilePath = null;
    /** Storage of fits image references */
    private final List<FitsImage> fitsImages = new LinkedList<FitsImage>();

    /**
     * Public constructor
     */
    public FitsImageFile() {
        super();
    }

    /**
     * Public constructor
     * @param absoluteFilePath absolute file path
     */
    public FitsImageFile(final String absoluteFilePath) {
        super();
        setAbsoluteFilePath(absoluteFilePath);
    }

    /**
     * Return the list of Fits images
     * @return list of Fits images
     */
    public List<FitsImage> getFitsImages() {
        return this.fitsImages;
    }

    /**
     * Return the number of Fits images present in this FitsImageFile structure
     * @return number of Fits images
     */
    public int getImageCount() {
        return this.fitsImages.size();
    }

    /** 
     * Return a short description of FitsImageFile content.
     * @return short description of FitsImageFile content
     */
    @Override
    public String toString() {
        return "FitsImageFile[" + getAbsoluteFilePath() + "](" + getImageCount() + ")\n" + this.fitsImages;
    }

    /*
     * Getter - Setter -----------------------------------------------------------
     */
    /**
     * Get the name of this FitsImageFile file.
     *  @return a string containing the name of the FitsImageFile file.
     */
    public String getName() {
        return getAbsoluteFilePath();
    }

    /**
     * Return the absolute file path
     * @return absolute file path or null if the file does not exist
     */
    public String getAbsoluteFilePath() {
        return absoluteFilePath;
    }

    /**
     * Define the absolute file path
     * @param absoluteFilePath absolute file path
     */
    public void setAbsoluteFilePath(final String absoluteFilePath) {
        this.absoluteFilePath = absoluteFilePath;
    }
}