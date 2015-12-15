package nd.rw.oxfordstreet;

import android.os.AsyncTask;

import com.microsoft.projectoxford.speechrecognition.Contract;
import com.microsoft.projectoxford.speechrecognition.DataRecognitionClient;
import com.microsoft.projectoxford.speechrecognition.SpeechRecognitionMode;

import java.io.IOException;
import java.io.InputStream;

public class RecognitionTask extends AsyncTask<Void, Void, Void> {

    //region Fields

    private RecognitionClient recognitionClient;
    private int timeToWaitSeconds;
    InputStream fileStream;

    //endregion Fields

    public RecognitionTask(RecognitionClient recognitionClient, InputStream fileStream) {
        this.recognitionClient = recognitionClient;
        timeToWaitSeconds = 200;
        this.fileStream = fileStream;
    }

    public int getTimeToWaitSeconds() {
        return timeToWaitSeconds;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            // Note for wave files, we can just send data from the file right to the server.
            // In the case you are not an audio file in wave format, and instead you have just
            // raw data (for example audio coming over bluetooth), then before sending up any
            // audio data, you must first send up an SpeechAudioFormat descriptor to describe
            // the layout and format of your raw audio data via DataRecognitionClient's sendAudioFormat() method.
            // String filename = recoMode == SpeechRecognitionMode.ShortPhrase ? "whatstheweatherlike.wav" : "batman.wav";
            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            do {
                // Get  Audio data to send into byte buffer.
                bytesRead = fileStream.read(buffer);

                if (bytesRead > -1) {
                    // Send of audio data to service.
                    recognitionClient.getRecognitionClient().sendAudio(buffer, bytesRead);
                }
            } while (bytesRead > 0);
        }
        catch(IOException ex) {
            Contract.fail();
        }
        finally {
            recognitionClient.getRecognitionClient().endAudio();
        }

        return null;
    }
}
