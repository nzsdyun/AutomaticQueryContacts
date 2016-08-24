package com.example.simplecursoradapterdemo;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.simplecursoradapterdemo.dummy.DummyContent;

public class MainActivity extends AppCompatActivity implements SimpleCursorAdapterFragment
        .OnListFragmentInteractionListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(android.R.id.content, SimpleCursorAdapterListFragment.newInstance(1)).commit();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(android.R.id.content, SimpleCursorAdapterFragment.newInstance(5)).commit();
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.i(TAG, "onListFragmentInteraction item:" + item.toString());
    }
}
