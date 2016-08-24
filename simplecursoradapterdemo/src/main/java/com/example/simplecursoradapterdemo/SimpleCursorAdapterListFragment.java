package com.example.simplecursoradapterdemo;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.ListView;

/**
 * Created by chenkun on 8/23/2016.
 */
public class SimpleCursorAdapterListFragment extends Fragment implements
        AdapterView.OnItemClickListener, SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = SimpleCursorAdapterListFragment.class.getSimpleName();
    private static final String ARG_COUNT = "arg_count";
    private static final int QUERY_CONTACTS = 0;
    private static final String[] FROM_COLUMNS = {
            ContactsContract.Contacts._ID,
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };
    private static final int[] TO_IDS = {
            R.id.id,
            R.id.content
    };

    private ListView mContactListView;
    private SimpleCursorAdapter mSimpleCursorAdapter;
    private SearchView mSearchEdit;
    private Uri mContactUri = ContactsContract.Contacts.CONTENT_URI;
    private static final String[] PROJECTION = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.LOOKUP_KEY,
            Build.VERSION.SDK_INT
                    >= Build.VERSION_CODES.HONEYCOMB ?
                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
                    ContactsContract.Contacts.DISPLAY_NAME
    };
    private static final int CONTACT_ID_INDEX = 0;
    private static final int LOOKUP_KEY_INDEX = 1;
//    private static final String SELECTION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ?
//            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + " LIKE ?" :
//            ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?";
//    private String mSelectString = "";
//    private String[] mSelectArgs = {mSelectString};

    private long mContactId;
    private String mContactKey;
    private Uri mContactItemUri;

    private int mCount = -1;

    public SimpleCursorAdapterListFragment() {
    }

    public static SimpleCursorAdapterListFragment newInstance(int count) {
        SimpleCursorAdapterListFragment fragment = new SimpleCursorAdapterListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, count);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContactListView = (ListView) getActivity().findViewById(R.id.list);
        mSearchEdit = (SearchView) getActivity().findViewById(R.id.search_edit);
        //TODO:this is stupid.
//        Cursor cursor = getActivity().managedQuery(mContactUri, PROJECTION, null, null, null);
//        Log.i(TAG, " cursor:" + cursor);
//        if (cursor != null && cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            Log.i(TAG, " id:" + cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)) + ", name"
//                    + cursor.getString(cursor.getColumnIndex(Build.VERSION.SDK_INT
//                    >= Build.VERSION_CODES.HONEYCOMB ?
//                    ContactsContract.Contacts.DISPLAY_NAME_PRIMARY :
//                    ContactsContract.Contacts.DISPLAY_NAME)));
//        }

        mContactListView.setAdapter(mSimpleCursorAdapter = new SearchCursorLoaderAdapter(getActivity(),
                R.layout.fragment_item, null, FROM_COLUMNS, TO_IDS, 0));
        //FIXME: this is just a demonstration
       /*mSimpleCursorAdapter.setFilterQueryProvider(new SearchQueryFileter(getActivity()));*/
        mContactListView.setOnItemClickListener(this);
        mContactListView.setTextFilterEnabled(true);
        setUpSearchView();
        //use loader load data
        getLoaderManager().destroyLoader(QUERY_CONTACTS);
        getLoaderManager().initLoader(QUERY_CONTACTS, null, this);
    }

    private void setUpSearchView() {
        mSearchEdit.setIconifiedByDefault(false);
        mSearchEdit.setOnQueryTextListener(this);
        mSearchEdit.setSubmitButtonEnabled(false);
        mSearchEdit.setQueryHint("select people");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mCount = savedInstanceState.getInt(ARG_COUNT);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
        cursor.moveToPosition(position);
        mContactId = cursor.getLong(CONTACT_ID_INDEX);
        mContactKey = cursor.getString(LOOKUP_KEY_INDEX);
        mContactItemUri = ContactsContract.Contacts.getLookupUri(mContactId, mContactKey);
        Log.i(TAG, " mContactId:" + mContactId + ", mContactKey:" + mContactKey + ", mContactItemUri:" + mContactItemUri);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mContactListView.clearTextFilter();
        } else {
            mContactListView.setFilterText(newText.toString());
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), mContactUri, PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mSimpleCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mSimpleCursorAdapter.swapCursor(null);
    }

    class SearchCursorLoaderAdapter extends SimpleCursorAdapter {
        private Context mContext;

        public SearchCursorLoaderAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
            this.mContext = context;
        }

        public SearchCursorLoaderAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
            super(context, layout, c, from, to, flags);
            this.mContext = context;
        }

        @Override
        public Filter getFilter() {
            //you can also define your own Filter
            return super.getFilter()/*new MyFilter()*/;
        }

        @Override
        public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
            Log.i(TAG, "runQueryOnBackgroundThread:" + constraint);
            FilterQueryProvider filter = getFilterQueryProvider();
            if (filter != null) {
                return filter.runQuery(constraint);
            }
            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            if (!TextUtils.isEmpty(constraint)) {
                uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI
                        , Uri.encode(constraint.toString()));
            }
            return mContext.getContentResolver().query(uri, PROJECTION, null, null, null);
        }

        /**
         * a custom filter rules
         */
        private class MyFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.i(TAG, "MyFilter performFiltering:" + constraint);
                FilterResults filterResults = new FilterResults();
                Uri uri = ContactsContract.Contacts.CONTENT_URI;
                if (!TextUtils.isEmpty(constraint)) {
                    uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI
                            , Uri.encode(constraint.toString()));
                }
                //TODO:this should not be run in the UI thread
                Cursor cursor = mContext.getContentResolver().query(uri, PROJECTION, null, null, null);
                if (cursor != null) {
                    filterResults.values = cursor;
                    filterResults.count = cursor.getCount();
                } else {
                    filterResults.values = null;
                    filterResults.count = 0;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    swapCursor((Cursor) results.values);
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    }

    /**
     * we can also define the filter conditions so
     */
    class SearchQueryFileter implements FilterQueryProvider {
        private Context mContext;
        public SearchQueryFileter(Context context) {
            mContext = context;
        }
        @Override
        public Cursor runQuery(CharSequence constraint) {
            Log.i(TAG, "runQuery:" + constraint);
            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            if (!TextUtils.isEmpty(constraint)) {
                uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI
                        , Uri.encode(constraint.toString()));
            }
            //TODO:this should not be run in the UI thread
            return mContext.getContentResolver().query(uri, PROJECTION, null, null, null);
        }
    }
}
