package in.curos.cueprompter;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import in.curos.cueprompter.data.Script;
import in.curos.cueprompter.data.ScriptsProvider;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int LEFT_FRAGMENT_CONTAINER = R.id.left_fragment_container;
    private int RIGHT_FRAGMENT_CONTAINER = R.id.right_fragment_container;
    private int MAIN_FRAGMENT_CONTAINER;

    private boolean DUAL_SCREEN_MODE;

    public boolean isDualScreen()
    {
        return DUAL_SCREEN_MODE;
    }

    private boolean HOME = true;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DUAL_SCREEN_MODE = findViewById(RIGHT_FRAGMENT_CONTAINER) != null;

        if (DUAL_SCREEN_MODE) {
            MAIN_FRAGMENT_CONTAINER = RIGHT_FRAGMENT_CONTAINER;
            getSupportLoaderManager().initLoader(0, null, this);
        } else {
            MAIN_FRAGMENT_CONTAINER = LEFT_FRAGMENT_CONTAINER;
        }

        showMainScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DUAL_SCREEN_MODE)
            getSupportLoaderManager().restartLoader(0, null, this);
    }

    public void showMainScreen()
    {
        HOME = true;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(LEFT_FRAGMENT_CONTAINER, new ScriptListFragment())
                .commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(getString(R.string.app_name));
    }

    public void showDetailScreen(String id)
    {
        HOME = false;
        ScriptDetailFragment fragment = new ScriptDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putString("id", id);
        fragment.setArguments(arguments);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(MAIN_FRAGMENT_CONTAINER, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (!HOME && !isDualScreen())
            showMainScreen();
        else
            finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, ScriptsProvider.SCRIPTS_BASE_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        if (data.getCount() >= 1) {
            final Script script = Script.populate(data);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    showDetailScreen(script.getId().toString());
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
