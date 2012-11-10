package apps.jrupac.WordFinder;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import apps.jrupac.WordFinder.tools.Solver;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends ListActivity {

    public static transient final String TAG = "ResultActivity";

    public static final String QUERY_TEXT = "query_text";
    public static final String STARTS_WITH_TEXT = "starts_with_text";
    public static final String CONTAINS_EXACT_TEXT = "contains_exact_text";
    public static final String ENDS_WITH_TEXT = "ends_with_text";

    private View progressLayout = null;
    private Solver solver = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.results);
        Solver.waitUntilReady();

        Bundle args = getIntent().getExtras();
        String query = args.getString(QUERY_TEXT);
        String startsWith = args.getString(STARTS_WITH_TEXT);
        String containsExact = args.getString(CONTAINS_EXACT_TEXT);
        String endsWith = args.getString(ENDS_WITH_TEXT);

        solver = new Solver();
        progressLayout = findViewById(R.id.progress_layout);
        showSolution(query, startsWith, containsExact, endsWith);
    }

    private void showSolution(String query, String startsWith, String containsExact,
                              String endsWith) {
        List<Pair<String, Integer>> solution = solver.solve(query, startsWith, containsExact, endsWith);
        List<String> listItems = new ArrayList<String>();

        for (Pair<String, Integer> elem : solution) {
            StringBuilder sb = new StringBuilder();
            sb.append(elem.first);
            sb.append(" ");
            sb.append(elem.second);
            sb.append("\n");
            listItems.add(sb.toString());
        }

        progressLayout.setVisibility(View.GONE);

        Log.i(TAG, "Done computing result");
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems));
    }
}