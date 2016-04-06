package com.hetekivi.rasian.Interfaces;

/**
 * Created by Santeri Hetekivi on 5.4.2016.
 */

/**
 * Interface Removable
 * for objects that can remove data.
 */
public interface Removable {

    /**
     * Function Remove
     * for removing data.
     * @param objectToRemove Object that will be removed.
     * @return Success of remove.
     */
    boolean Remove(Object objectToRemove);

    /**
     * Function onRemoveSuccess
     * This gets called when removing has been done and was successful.
     */
    void onRemoveSuccess();

    /**
     * Function onRemoveFailure
     * This gets called when removing has been done and there were errors.
     */
    void onRemoveFailure();
}