package mmalla.android.com.helpabake.ingredient;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mmalla.android.com.helpabake.R;

public class IngredientsListFragment extends Fragment{

    private ArrayList listOfIngredients;
    public static final String INGREDIENTS_LIST = "INGREDIENTS_LIST";

    public IngredientsListFragment() {
        /**
         * Empty Constructor
         */
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        listOfIngredients = new ArrayList<Ingredient>();

        if(getArguments()!=null){
            listOfIngredients = getArguments().getParcelableArrayList(INGREDIENTS_LIST);
        }

        final View rootView = inflater.inflate(R.layout.ingredients_list_fragment, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.list_recyclerview);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(new IngredientsListAdapter(listOfIngredients, getContext()));
        return rootView;
    }
}
