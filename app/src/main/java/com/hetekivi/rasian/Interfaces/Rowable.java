package com.hetekivi.rasian.Interfaces;

import com.hetekivi.rasian.Data.RSS.Data;

import java.util.List;

/**
 * Created by Santeri Hetekivi on 2.4.2016.
 */

/**
 * Interface Rowable
 * for objects that can produce Data objects rows.
 */
public interface Rowable
{
    /**
     * Function Rows
     * for making and returning Data rows.
     * @return List of rows as Data objects.
     */
    List<Data> Rows();

    /**
     * Function onRowsGotten
     * is called when rows have been gotten.
     * @param rows All rows.
     */
    void onRowsGotten(List<Data> rows);
}
