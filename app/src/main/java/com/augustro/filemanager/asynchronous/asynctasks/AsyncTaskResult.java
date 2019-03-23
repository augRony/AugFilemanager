

package com.augustro.filemanager.asynchronous.asynctasks;

/**
 * Container for AsyncTask results. Allow either result object or exception to be contained.
 *
 * @param <T> Result type
 */

public class AsyncTaskResult<T> {
    public final T result;
    public final Throwable exception;

    public AsyncTaskResult(T result){
        this.result = result;
        this.exception = null;
    }

    public AsyncTaskResult(Throwable exception){
        this.result = null;
        this.exception = exception;
    }

    /**
     * Callback interface for use in {@link android.os.AsyncTask}. Think Promise callback in JS.
     */
    public interface Callback<T> {

        /**
         * Implement logic on what to do with the result here.
         */
        void onResult(T result);
    }
}
