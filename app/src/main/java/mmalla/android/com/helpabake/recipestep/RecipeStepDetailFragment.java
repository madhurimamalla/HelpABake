package mmalla.android.com.helpabake.recipestep;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import mmalla.android.com.helpabake.R;
import mmalla.android.com.helpabake.recipe.Recipe;
import mmalla.android.com.helpabake.roomdatabase.RecipesDatabase;
import mmalla.android.com.helpabake.videoplayer.VideoPlayerFragment;
import timber.log.Timber;

public class RecipeStepDetailFragment extends Fragment {

    public static final String RECIPE_EXTRA_INTENT = "RECIPE_EXTRA_INTENT";
    public static final String RECIPE_PARCELABLE = "RECIPE_PARCELABLE";
    public static final String RECIPE_STEP = "RECIPE_STEP";
    public static final String TWO_PANE = "TWO_PANE";
    public Boolean mTwoPane;

    RecipesDatabase recipesDatabase;

    public RecipeStepDetailFragment() {
        /**
         * Mandatory empty constructor for the fragment
         */
    }

    @Nullable
    @BindView(R.id.recipe_step_detail_textview)
    public TextView mRecipeStepDesc;

    @BindView(R.id.video_not_available_textview)
    public TextView mVideoNotAvailable;

    @BindView(R.id.button_to_previous_step)
    public Button mPreviousButton;

    @BindView(R.id.button_to_next_step)
    public Button mNextButton;

    Recipe recipe;
    RecipeStep recipeStep;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recipe_step_detail_fragment, container, false);

        /**
         * Bind the views using ButterKnife
         */
        ButterKnife.bind(this, rootView);

        recipesDatabase = RecipesDatabase.getDatabase(getContext());

        if (getArguments() != null) {
            recipeStep = getArguments().getParcelable(RECIPE_STEP);
            recipe = getArguments().getParcelable(RECIPE_EXTRA_INTENT);
            mTwoPane = getArguments().getBoolean(TWO_PANE);
        }

        Timber.d("Recipe name: " + recipe.getName());
        Timber.d("Recipe step clicked was: " + recipeStep.getShortDescription());
        Toast.makeText(getActivity(), "recipeStep: " + recipeStep.getShortDescription(), Toast.LENGTH_SHORT);

        int next = 0;
        int previous = 0;
        final int position = getThePositionOfRecipeStep(recipeStep, recipe);
        if (position == -1) {
            Timber.d("This recipe step wasn't found in the recipe");
        } else {
            /**
             * Handle the beginning and end of the list first.
             */
            if (position == 0) {
                next = 1;
                previous = recipe.getSteps().size() - 1;
            } else if (position == recipe.getSteps().size() - 1) {
                next = 0;
                previous = recipe.getSteps().size() - 2;
            } else {
                next = position + 1;
                previous = position - 1;
            }
        }

        playTheVideoIfAnyAndRecipeStepDesc(recipeStep, position, recipe);

        if (mTwoPane == true) {
            /**
             * Two buttons code will be here
             */
            mNextButton.setVisibility(View.VISIBLE);
            mPreviousButton.setVisibility(View.VISIBLE);
            final int finalNext = next;
            mNextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Timber.d("It's inside the click on previous button");
                    // playTheVideoIfAnyAndRecipeStepDesc(recipeStep, finalNext, recipe);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(RECIPE_EXTRA_INTENT, recipe);
                    bundle.putParcelable(RECIPE_STEP, recipe.getSteps().get(finalNext));
                    RecipeStepDetailFragment recipeStepDetailFragment = new RecipeStepDetailFragment();
                    recipeStepDetailFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.recipe_step_detail_fragment, recipeStepDetailFragment).commit();
                }
            });
            final int finalPrevious = previous;
            mPreviousButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Timber.d("It's inside the click on next button");
                    // playTheVideoIfAnyAndRecipeStepDesc(recipeStep, finalPrevious, recipe);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(RECIPE_EXTRA_INTENT, recipe);
                    bundle.putParcelable(RECIPE_STEP, recipe.getSteps().get(finalPrevious));
                    RecipeStepDetailFragment recipeStepDetailFragment = new RecipeStepDetailFragment();
                    recipeStepDetailFragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.recipe_step_detail_fragment, recipeStepDetailFragment).commit();
                }
            });
        }
        return rootView;
    }

    /**
     * @param recipeStep
     * @param recipe
     * @return
     */
    public int getThePositionOfRecipeStep(RecipeStep recipeStep, Recipe recipe) {
        for (int i = 0; i < recipe.getSteps().size(); i++) {
            Timber.d(recipeStep.getShortDescription() + "  vs  " + recipe.getSteps().get(i).getShortDescription());
            if (recipeStep.getShortDescription().equals(recipe.getSteps().get(i).getShortDescription())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param recipeStep
     * @param position
     * @param recipe
     */
    public void playTheVideoIfAnyAndRecipeStepDesc(RecipeStep recipeStep, int position, Recipe recipe) {


        if (recipeStep.getVideoURL() == null || recipeStep.getVideoURL().trim().isEmpty()) {
            mVideoNotAvailable.setVisibility(View.VISIBLE);
        } else {
            mVideoNotAvailable.setVisibility(View.INVISIBLE);

            Bundle bundleUpVideoRelated = new Bundle();
            bundleUpVideoRelated.putParcelable(RECIPE_PARCELABLE, recipe);
            bundleUpVideoRelated.putParcelable(RECIPE_STEP, recipe.getSteps().get(position));
            VideoPlayerFragment videoPlayerFragment = new VideoPlayerFragment();
            videoPlayerFragment.setArguments(bundleUpVideoRelated);
            getFragmentManager().beginTransaction().replace(R.id.container_for_video, videoPlayerFragment).commit();
        }
        /*
         * Setting the text to show the Complete Recipe Description
         */
        mRecipeStepDesc.setText(recipe.getSteps().get(position).getDescription());
    }
}