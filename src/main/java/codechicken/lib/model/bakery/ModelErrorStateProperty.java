package codechicken.lib.model.bakery;

import codechicken.lib.block.property.unlisted.UnlistedPropertyBase;
import codechicken.lib.model.bakery.ModelErrorStateProperty.ErrorState;
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Created by covers1624 on 13/09/2017.
 */
public final class ModelErrorStateProperty extends UnlistedPropertyBase<ErrorState> {

    public static final ModelErrorStateProperty ERROR_STATE = new ModelErrorStateProperty();

    private ModelErrorStateProperty() {
        super("ccl_model_error_state");
    }

    @Override
    public Class<ErrorState> getType() {
        return ErrorState.class;
    }

    @Override
    public String valueToString(ErrorState value) {
        return value.toString();
    }

    public static class ErrorState {

        public static final ErrorState OK = new ErrorState(false);

        private final boolean errored;
        private String reason = "OK";

        public ErrorState(boolean errored) {
            this.errored = errored;
        }

        public ErrorState setReason(String reason) {
            this.reason = reason;
            return this;
        }

        public ErrorState setReason(Throwable e) {
            return setReason(ExceptionUtils.getStackTrace(e));
        }

        public boolean hasErrored() {
            return errored;
        }

        public String getReason() {
            return reason;
        }

        public static ErrorState of(String string) {
            return new ErrorState(true).setReason(string);
        }

        public static ErrorState of(String string, Object... data) {
            return of(String.format(string, data));
        }

        public static ErrorState of(Throwable e) {
            return new ErrorState(true).setReason(e);
        }

        @Override
        public String toString() {
            if (hasErrored()) {
                return getReason();
            }
            return "No Error: OK";
        }
    }
}


