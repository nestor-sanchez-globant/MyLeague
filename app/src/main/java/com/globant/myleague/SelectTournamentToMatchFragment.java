package com.globant.myleague;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.globant.myleague.adapter.LinkAdapterTournament;
import com.globant.myleague.pojo.Tournaments;
import com.globant.myleague.services.MyLeagueService;
import com.globant.myleague.tools.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created the first version by kaineras on 16/02/15.
 */
public class SelectTournamentToMatchFragment extends ListFragment {


    MyLeagueService.ApiInterface mMyLeagueApiInterface;
    ArrayAdapter<Tournaments> mAdapter;
    Tools tools = new Tools();

    final static String LOG_TAG = SelectTournamentToMatchFragment.class.getSimpleName();

    public SelectTournamentToMatchFragment()
    {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MyLeagueService myLeagueService = new MyLeagueService();
        mMyLeagueApiInterface=myLeagueService.generateServiceInterface();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<Tournaments> tournamentsList = new ArrayList<>();
        mAdapter = new LinkAdapterTournament(getActivity(),R.layout.fragment_item_view_tournament,tournamentsList);
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tournaments selectedTournament = mAdapter.getItem(position);
                String selectedTournamentId = selectedTournament.getId();
                Bundle bundle = new Bundle();
                bundle.putString(MatchesListActivity.TOURNAMENT_ID, selectedTournamentId);
                SelectMatchToFillFragment selectMatchToFillFragment = new SelectMatchToFillFragment();
                selectMatchToFillFragment.setArguments(bundle);
                tools.loadFragment(getFragmentManager(),selectMatchToFillFragment,R.id.rightpane,"YES");
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mMyLeagueApiInterface.getTournaments(new Callback<List<Tournaments>>() {
            @Override
            public void success(List<Tournaments> tournamentses, Response response) {
                if(response.getStatus()==200)
                {
                    mAdapter.clear();
                    mAdapter.addAll(tournamentses);
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    Log.e(LOG_TAG, "Project retrieval status problem: " + response.getReason());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.w(LOG_TAG, "ERROR: downloading " + error.getBody());
            }
        });
    }
}
