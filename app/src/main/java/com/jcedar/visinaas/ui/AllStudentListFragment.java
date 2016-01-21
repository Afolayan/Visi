package com.jcedar.visinaas.ui;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.jcedar.visinaas.R;
import com.jcedar.visinaas.adapter.SearchResultsCursorAdapter;
import com.jcedar.visinaas.adapter.WrappingLinearLayoutManager;
import com.jcedar.visinaas.io.adapters.StudentCursorAdapter;
import com.jcedar.visinaas.provider.DataContract;
import com.jcedar.visinaas.ui.view.SimpleSectionedListAdapter;

public class AllStudentListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected static final String NAIRA = "\u20A6";
    private static final String TAG = AllStudentListFragment.class.getSimpleName();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private int mPosition;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private StudentCursorAdapter mAdapter;
    private SimpleSectionedListAdapter sSectionAdapter;
    private ListView listView;
    private TextView tvError;
    private Bundle mHomeBundle = Bundle.EMPTY;
    private String _POSITION = "position";
    private Listener mCallback;
    static String context;

    RecyclerView recyclerView;
    SearchResultsCursorAdapter resultsCursorAdapter;

    public static AllStudentListFragment newInstance(int position) {
        AllStudentListFragment fragment = new AllStudentListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, position);


        fragment.setArguments(args);
        return fragment;
    }

    public AllStudentListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initialize loader
        getLoaderManager().initLoader(1, null, this);


                Loader loader1 = getLoaderManager().getLoader(1);
                if (loader1 != null && !loader1.isReset()) {
                    getLoaderManager().restartLoader(1, null, this);
                } else {
                    getLoaderManager().initLoader(1, null, this);
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
    context = getActivity().getClass().getSimpleName();

        // setListAdapter(mAdapter);
        /*setListAdapter(sSectionAdapter);*/
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ViewGroup rootView;

        if( !context.equalsIgnoreCase("AllStudentDetailsActivity")) {
             rootView =
                    (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        } else {
            rootView =
                    (ViewGroup) inflater.inflate(R.layout.fragment_home1, container, false);
        }
        recyclerView = (RecyclerView) rootView.findViewById( R.id.recyclerview );
        resultsCursorAdapter = new SearchResultsCursorAdapter( getActivity() );

        recyclerView.setLayoutManager(new WrappingLinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(resultsCursorAdapter);
        tvError = (TextView) rootView.findViewById(R.id.tvErrorMag);

        resultsCursorAdapter.setOnItemClickListener(new SearchResultsCursorAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Cursor data) {

                long Id = data.getLong(
                        data.getColumnIndex(DataContract.Students._ID));
                Log.d(TAG, "selectedId = " + Id + _POSITION);
                // add position to bundle
                //mHomeBundle.putInt(_POSITION, position);
                mCallback.onAllSelected(Id);


            }
        });

        return rootView;

    }

    interface Listener {
        void onAllSelected(long courseId);
        void onFragmentDetached(Fragment fragment);
        void onFragmentAttached(Fragment fragment);
    }


    private void getSOO(Cursor data){
        String nameStr = data.getString(
                data.getColumnIndexOrThrow(DataContract.StudentsChapter.NAME));

        String genderStr = data.getString(
                data.getColumnIndexOrThrow(DataContract.StudentsChapter.GENDER));


        String chapter = data.getString(
                data.getColumnIndexOrThrow(DataContract.StudentsChapter.CHAPTER));


        String emailAdd = data.getString(
                data.getColumnIndexOrThrow(DataContract.StudentsChapter.EMAIL));

        String course = data.getString(
                data.getColumnIndexOrThrow(DataContract.StudentsChapter.COURSE));


        String phone = data.getString(
                data.getColumnIndexOrThrow(DataContract.StudentsChapter.PHONE_NUMBER));

        String dateOfBirth = data.getString(
                data.getColumnIndexOrThrow(DataContract.StudentsChapter.DATE_OF_BIRTH));

        Log.e(TAG, nameStr+" name "+chapter);

        /*details.putString(DataContract.StudentsChapter.NAME, nameStr);
        details.putString(DataContract.StudentsChapter.GENDER, genderStr);
        details.putString(DataContract.StudentsChapter.CHAPTER, chapter);
        details.putString(DataContract.StudentsChapter.EMAIL, emailAdd);
        details.putString(DataContract.StudentsChapter.COURSE, course);
        details.putString(DataContract.StudentsChapter.PHONE_NUMBER, phone);
        details.putString(DataContract.StudentsChapter.DATE_OF_BIRTH, dateOfBirth);
*/
                /*Intent intent = new Intent(getActivity(), Details.class);
                intent.putExtras(details);
                startActivity( intent );*/
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof Listener) {
            mCallback = (Listener) activity;
            mCallback.onFragmentAttached(this);
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement fragments listener");
        }
        activity.getContentResolver().registerContentObserver(
                DataContract.Students.CONTENT_URI, true, mObserver);



    }

    @Override
    public void onDetach() {
        super.onDetach();
        super.onDetach();
        if (getActivity() instanceof Listener) {
            ((Listener) getActivity()).onFragmentDetached(this);
        }
        getActivity().getContentResolver().unregisterContentObserver(mObserver);
    }

    private final ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (!isAdded()) {
                return;
            }
            getLoaderManager().restartLoader(1, null, AllStudentListFragment.this);
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = DataContract.Students.CONTENT_URI;
        CursorLoader   cursorLoader =  new CursorLoader(
                    getActivity(),
                    uri,
                    DataContract.Students.PROJECTION_ALL,
                    null,    // selection
                    null,           // arguments
                    DataContract.Students.NAME + " ASC"
            );
        return cursorLoader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        resultsCursorAdapter.swapCursor(data);

        /*if( data.getCount() == 0) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText(  "No data yet");
        }*/
        /*
        Bundle bundle = new Bundle();
        int count = 0;
        List<SimpleSectionedListAdapter.Section> sections =
                new ArrayList<SimpleSectionedListAdapter.Section>();
        String chapter, dummy="dummy";

        if( data.moveToFirst() ) {
            mAdapter.swapCursor(data);
            mAdapter.notifyDataSetChanged();

            data.moveToFirst();
            while ( !data.isAfterLast()) {

               chapter = data.getString( data.getColumnIndex( DataContract.Students.CHAPTER));

                if( !chapter.equalsIgnoreCase(dummy)){
                    sections.add( new SimpleSectionedListAdapter.Section( data.getPosition(),
                            chapter));
                }

                dummy = chapter;

                long studentId = data.getLong(
                        data.getColumnIndexOrThrow(DataContract.Students._ID));
                SimpleSectionedListAdapter.Section[] sectionArray =
                        new SimpleSectionedListAdapter.Section[sections.size()];
                sSectionAdapter.setSections(sections.toArray(sectionArray));
                bundle.putLong(AllStudentDetailsActivity.ARG_ALL_LIST
                        + Integer.toString(count++), studentId);
                data.moveToNext();
            }
          this.mHomeBundle = bundle;
        } else {
            mAdapter.swapCursor(null);
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Error retrieving data ");

        }*/

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        resultsCursorAdapter.swapCursor(null);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }



}
