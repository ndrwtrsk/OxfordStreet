package nd.rw.oxfordstreet;

import android.app.Activity;

public interface RecognitionClientUtilizer {

    String getSubscriptionKey();
    Activity getActivity();
    void getFinalResponse(final String response);
}
