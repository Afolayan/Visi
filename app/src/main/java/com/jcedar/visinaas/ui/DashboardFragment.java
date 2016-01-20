package com.jcedar.visinaas.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jcedar.visinaas.R;
import com.jcedar.visinaas.ui.view.SimpleSectionedListAdapter;
import com.jcedar.visinaas.io.adapters.StudentCursorAdapter;
import com.jcedar.visinaas.provider.DataContract;

public class DashboardFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected static final String NAIRA = "\u20A6";
    private static final String TAG = DashboardFragment.class.getSimpleName();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mPosition;
    private String mParam2;
    public static final String STUDENT_POSITION = "position";

    private StudentCursorAdapter mAdapter;
    private SimpleSectionedListAdapter sSectionAdapter;
    private ListView listView;
    private TextView tvError;
    private Bundle mStudentBundle = Bundle.EMPTY;
    private Listener mCallback;


    public static DashboardFragment newInstance(int position) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);

        fragment.setArguments(args);
        return fragment;
    }

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initialize loader
        // Initialize loader

            getLoaderManager().initLoader(0, null, this);
           Loader loader = getLoaderManager().getLoader(0);
            if (loader != null && !loader.isReset()) {
                getLoaderManager().restartLoader(0, null, this);
            } else {
                getLoaderManager().initLoader(0, null, this);
            }





    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPosition = getArguments().getInt(ARG_PARAM1);
        }

        mAdapter = new StudentCursorAdapter(getActivity(), null,
                R.layout.list_n_item_student);

        sSectionAdapter = new SimpleSectionedListAdapter(getActivity(),
                R.layout.list_group_header, mAdapter);
        //setListAdapter(mAdapter);
        setListAdapter(sSectionAdapter);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView =
                (ViewGroup) inflater.inflate(R.layout.fragment_dash, container, false);
        //Initialize all necessary views in this method
        Log.e("Fragment", "Dashboard started");


        tvError = (TextView) rootView.findViewById(R.id.tvErrorMag);
        listView = (ListView) rootView.findViewById(android.R.id.list);

        listView.setItemsCanFocus(true);
        listView.setCacheColorHint(getResources().getColor(
                R.color.white));
        listView.setVerticalScrollBarEnabled(true);
        listView.setDividerHeight(0);


        return rootView;
    }

    public ListView getListView() {
        return listView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (Listener) activity;
            mCallback.onFragmentAttached(this);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        DashboardObserver observer = new DashboardObserver(new Handler());
        // start watching for changes
        observer.observe();

        // where we do our work
        updateDashboard();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof Listener) {
            ((Listener) getActivity()).onFragmentDetached(this);
        }
        getActivity().getContentResolver().unregisterContentObserver(mObserver);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.StudentsChapter.CONTENT_URI;
        CursorLoader cursorLoader = null;

           /* String chapter = AccountUtils.getUserChapter(getActivity());

            if (chapter != null) {
                String selection = DataContract.Students.CHAPTER + "=? ";
                String[]selectionArgs = new String[]{chapter};

                cursorLoader = new CursorLoader(
                        getActivity(),
                        uri,
                        DataContract.Students.PROJECTION_ALL,
                        selection,    // selection
                        selectionArgs,           // arguments
                        DataContract.Students.CHAPTER + " ASC"
                );
            }*/


        return new CursorLoader(
                getActivity(),
                uri,
                DataContract.StudentsChapter.PROJECTION_ALL,
                null,
                null,
                null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Bundle bundle = new Bundle();
        int count = 0;

       /* if( data.getCount() == 0) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(  "No data yet");
        }
*/

        if( data.moveToFirst() ) {
            mAdapter.swapCursor(data);
            mAdapter.notifyDataSetChanged();

            data.moveToFirst();
            while ( !data.isAfterLast()) {

                long studentId = data.getLong(
                        data.getColumnIndexOrThrow(DataContract.Students._ID));

                bundle.putLong(StudentDetailsActivity.ARG_STUDENT_LIST
                        + Integer.toString(count++), studentId);
                data.moveToNext();
            }
           this.mStudentBundle = bundle;
        } else {
            mAdapter.swapCursor(null);
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Error retrieving data");

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final Cursor cursor = (Cursor) sSectionAdapter.getItem(position);
        if (cursor != null) {
            long Id = cursor.getLong(
                    cursor.getColumnIndex(DataContract.Students._ID));
            Log.d(TAG, "selectedId = " + Id + STUDENT_POSITION);
            // add position to bundle
            mStudentBundle.putInt(STUDENT_POSITION, position);
            mCallback.onSchoolSelected(Id, mStudentBundle);
        }

    }

    interface Listener {
        void onSchoolSelected(long courseId, Bundle data);
        void onFragmentAttached(ListFragment fragment);
        void onFragmentDetached(ListFragment fragment);
    }



    //Anonymous inner class to handle watching Uris
    class DashboardObserver extends ContentObserver {
        DashboardObserver(Handler handler) {
            super(handler);
        }

        void observe() {
            ContentResolver resolver = getActivity().getContentResolver();
        }

        @Override
        public void onChange(boolean selfChange) {
            updateDashboard();
        }
    }

    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (!isAdded()) {
                return;
            }
            getLoaderManager().restartLoader(0, null, DashboardFragment.this);
        }
    };

    private void updateDashboard() {
        // do work
        try {
            getLoaderManager().restartLoader(0, null, this);
        } catch (Exception e) {
            Log.e(TAG, "" + e);

        }

    }

}
