package ratuple.general.exceptions;

import java.util.function.Supplier;

public class ExceptionWrapper {

    public static <Return> Return wrap(Supplier<Return> call){
        return call.get();
    }

    public static GeneralRuntimeException wrap(Exception ex){
        return new GeneralRuntimeException(ex);
    }
}
