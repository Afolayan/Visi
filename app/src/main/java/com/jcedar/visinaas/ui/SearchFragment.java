package com.jcedar.visinaas.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcedar.visinaas.R;
import com.jcedar.visinaas.adapter.RecyclerCursorAdapterAll;
import com.jcedar.visinaas.adapter.WrappingLinearLayoutManager;
import com.jcedar.visinaas.provider.DataContract;

public class SearchFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final String _POSITION = "POSITION";

    private Listener mListener;
    RecyclerView recyclerView;
    RecyclerCursorAdapterAll resultsCursorAdapter;
    private TextView tvError;

    public SearchFragment() {
        // Required empty public constructor
    }
    public static AllStudentListFragment newInstance(int position) {
        AllStudentListFragment fragment = new AllStudentListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView;

            rootView =
                    (ViewGroup) inflater.inflate(R.layout.fragment_home1, container, false);

        recyclerView = (RecyclerView) rootView.findViewById( R.id.recyclerview );
        resultsCursorAdapter = new RecyclerCursorAdapterAll( getActivity() );

        recyclerView.setLayoutManager(new WrappingLinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(resultsCursorAdapter);
        tvError = (TextView) rootView.findViewById(R.id.tvErrorMag);

        resultsCursorAdapter.setOnItemClickListener(new RecyclerCursorAdapterAll.OnItemClickListener() {
            @Override
            public void onItemClicked(Cursor data) {

                long Id = data.getLong(
                        data.getColumnIndex(DataContract.Students._ID));
                Log.d(TAG, "selectedId = " + Id + _POSITION);
                mListener.onListItemSelected(Id);


            }
        });

        return rootView;
    }

    public void onButtonPressed(long id) {
        if (mListener != null) {
            mListener.onListItemSelected(id);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            mListener = (Listener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onListItemSelected");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final Intent intent = BaseActivity.fragmentArgumentsToIntent(args);
        Uri studenturi = intent.getData();
        if(studenturi == null){
            studenturi = DataContract.Students.CONTENT_URI;
        }

        Loader<Cursor> cursorLoader = null;

            cursorLoader = new CursorLoader(getActivity(), studenturi,
                    DataContract.Students.PROJECTION_ALL,
                    null, null, DataContract.Students.SORT_ORDER_DEFAULT);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        resultsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void reloadFromArguments(Bundle arguments) {
        Log.d(TAG, "reloading fragment");
        recyclerView.setAdapter(null);

        // Load new arguments
        final Intent intent = BaseActivity.fragmentArgumentsToIntent(arguments);
        Uri studenturi = intent.getData();

        if (studenturi == null) {
            studenturi = DataContract.Students.CONTENT_URI;
        }

        Log.e(TAG, studenturi.toString() +" uri search");
        resultsCursorAdapter = new RecyclerCursorAdapterAll(getActivity());
        recyclerView.setAdapter(resultsCursorAdapter);

        getLoaderManager().restartLoader(1, arguments, this);
    }
    public interface Listener {
        void onListItemSelected(long studentId);
    }
}
