package nd.rw.oxfordstreet;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.microsoft.projectoxford.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.projectoxford.speechrecognition.RecognitionResult;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MainActivity
        extends AppCompatActivity
        implements RecognitionClientUtilizer{


    private static final String TAG = "MainActvitiy";

    TextView tv_transcript;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tv_transcript = (TextView) findViewById(R.id.transcript);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRecognizer();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void launchRecognizer(){
        String fileName = "whatstheweatherlike.wav";
        RecognitionClient recognitionClient = new RecognitionClient(this);
        RecognitionTask recognitionTask = null;
        try {
            InputStream inputStream = this.getAssets().open(fileName);
            recognitionTask = new RecognitionTask(recognitionClient, inputStream);
            recognitionTask.execute().get(recognitionTask.getTimeToWaitSeconds(), TimeUnit.SECONDS);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "launchRecognizer: File was not found.", e);
        } catch (InterruptedException e) {
            Log.e(TAG, "launchRecognizer: Execution was interrupted.", e);
        } catch (ExecutionException e) {
            Log.e(TAG, "launchRecognizer: Execution met an exception.", e);
        } catch (TimeoutException e) {
            Log.e(TAG, "launchRecognizer: Execution timed out.", e);
        } catch (IOException e) {
            Log.e(TAG, "launchRecognizer: Error on opening input stream", e);
        }
    }

    @Override
    public String getSubscriptionKey() {
        return this.getString(R.string.oxford_speech_api_subscription_key);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void getFinalResponse(String response) {
        tv_transcript.append(response + "\n");
    }
}
