package apps.jrupac.WordFinder;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import apps.jrupac.WordFinder.tools.Solver;
import apps.jrupac.WordFinder.utils.Utils;

import java.io.IOException;
import java.io.InputStream;

public class QueryActivity extends Activity {

    public transient final String TAG = Utils.TAG(this);

    private Button submitButton;

    private final Thread initContextThread = new Thread(new Runnable() {
        @Override
        public void run() {
            InputStream dictStream = null;
            try {
                AssetManager am = getAssets();
                dictStream = am.open("words.txt");
                Solver.initContext(dictStream);
            } catch (IOException e) {
                Log.e(TAG, "Could not open file", e);
                Toast.makeText(QueryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            } finally {
                if (dictStream != null) {
                    try {
                        dictStream.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Failed to close stream.", e);
                    }
                }

                QueryActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        submitButton.setText("Submit!");
                        submitButton.setEnabled(true);
                    }
                });
            }
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final EditText queryField = (EditText) findViewById(R.id.query_field);
        final EditText startsWithField = (EditText) findViewById(R.id.startsWith_field);
        final EditText containsField = (EditText) findViewById(R.id.containsExact_field);
        final EditText endsWithField = (EditText) findViewById(R.id.endsWith_field);

        submitButton = (Button) findViewById(R.id.submit_form);

        submitButton.setText("Loading...");
        submitButton.setEnabled(false);

        initContextThread.start();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = queryField.getText().toString().trim();
                String startsWith = startsWithField.getText().toString().trim();
                String containsExact = containsField.getText().toString().trim();
                String endsWith = endsWithField.getText().toString().trim();

                Intent myIntent = new Intent(QueryActivity.this, ResultActivity.class);
                Bundle args = new Bundle();
                args.putString(ResultActivity.QUERY_TEXT, query);
                args.putString(ResultActivity.STARTS_WITH_TEXT, startsWith);
                args.putString(ResultActivity.CONTAINS_EXACT_TEXT, containsExact);
                args.putString(ResultActivity.ENDS_WITH_TEXT, endsWith);

                myIntent.putExtras(args);
                Log.i(TAG, "Starting ResultActivity");
                startActivity(myIntent);
            }
        });
    }
}
