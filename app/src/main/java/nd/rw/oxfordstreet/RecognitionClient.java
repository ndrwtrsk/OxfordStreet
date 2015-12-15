package nd.rw.oxfordstreet;

import android.app.Activity;
import android.util.Log;
import android.widget.EditText;

import com.microsoft.projectoxford.speechrecognition.DataRecognitionClient;
import com.microsoft.projectoxford.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.projectoxford.speechrecognition.RecognitionResult;
import com.microsoft.projectoxford.speechrecognition.RecognitionStatus;
import com.microsoft.projectoxford.speechrecognition.SpeechRecognitionMode;
import com.microsoft.projectoxford.speechrecognition.SpeechRecognitionServiceFactory;

public class RecognitionClient implements ISpeechRecognitionServerEvents{

    private static final String TAG = "RecognitionClient";

    public enum FinalResponseStatus { NotReceived, OK, Timeout }
    //region Fields

    private DataRecognitionClient recognitionClient;
    private FinalResponseStatus isReceivedResponse = FinalResponseStatus.NotReceived;
    private RecognitionClientUtilizer utilizer;
    private String language = "en-us";
    private SpeechRecognitionMode speechRecognitionMode = SpeechRecognitionMode.LongDictation;

    //endregion Fields


    public RecognitionClient(RecognitionClientUtilizer utilizer) {
        this.utilizer = utilizer;
        String subscriptionKey = utilizer.getSubscriptionKey();
        Activity activity = utilizer.getActivity();
        recognitionClient = SpeechRecognitionServiceFactory.createDataClient(activity,
                speechRecognitionMode, language, this, subscriptionKey);
    }

    public DataRecognitionClient getRecognitionClient() {
        return recognitionClient;
    }

    public SpeechRecognitionMode getSpeechRecognitionMode() {
        return speechRecognitionMode;
    }

    //region ISpeechRecognitionServerEvents Methods

    @Override
    public void onPartialResponseReceived(String response) {
        Log.d(TAG, "onPartialResponseReceived() called with: " + "response = [" + response + "]");
    }

    @Override
    public void onFinalResponseReceived(RecognitionResult response) {
        Log.d(TAG, "onFinalResponseReceived() called with: " + "response = [" + response + "]");
        boolean isFinalDicationMessage = (response.RecognitionStatus == RecognitionStatus.EndOfDictation ||
                        response.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout);

        if (!isFinalDicationMessage) {
            Log.d(TAG, "***** Final NBEST Results *****\n");
            for (int i = 0; i < response.Results.length; i++) {
                Log.d(TAG, " Confidence=" + response.Results[i].Confidence +
                        " DisplayText=\"" + response.Results[i].DisplayText + "\"\n");
            }
            String finalMessage = response.Results[0].DisplayText;
            utilizer.getFinalResponse(finalMessage);
        }
    }

    @Override
    public void onIntentReceived(String payload) {

    }

    @Override
    public void onError(int errorCode, String response) {
        Log.e(TAG, "onError() called with: " + "errorCode = [" + errorCode + "], response = [" + response + "]");
    }

    @Override
    public void onAudioEvent(boolean b) {
        Log.d(TAG, "onAudioEvent() called with: " + "b = [" + b + "]");
    }

    //endregion ISpeechRecognitionServerEvents Methods

}
