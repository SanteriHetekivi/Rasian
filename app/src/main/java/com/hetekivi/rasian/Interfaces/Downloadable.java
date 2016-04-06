package com.hetekivi.rasian.Interfaces;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

/**
 * Interface Downloadable
 * for objects that can download their data.
 */
public interface Downloadable
{
    /**
     * Function Download
     * for downloading object.
     * @return Success of download.
     */
    boolean Download();

    /**
     * Function onDownloadSuccess
     * This gets called when download has been done and was successful.
     */
    void onDownloadSuccess();

    /**
     * Function onDownloadFailure
     * This gets called when download has been done and there were errors.
     */
    void onDownloadFailure();
}
