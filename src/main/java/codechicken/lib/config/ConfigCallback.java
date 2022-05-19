package codechicken.lib.config;

/**
 * Created by covers1624 on 2/5/22.
 */
public interface ConfigCallback<T extends ConfigTag> {

    /**
     * Called when a ConfigTag is modified.
     *
     * @param tag    The tag that was modified. For convenience.
     *               This is identical to the tag which it was registered on.
     * @param reason The reason this callback was fired.
     */
    void onSync(T tag, Reason reason);

    enum Reason {
        /**
         * Callback fired manually across part of the Config tree.
         */
        MANUAL,
        /**
         * Tag was synced S -> C.
         */
        SYNC,
        /**
         * Tag was rolled back on disconnect from server.
         */
        ROLLBACK,
    }
}
