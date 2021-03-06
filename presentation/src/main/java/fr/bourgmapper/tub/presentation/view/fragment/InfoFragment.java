package fr.bourgmapper.tub.presentation.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.OnClick;
import fr.bourgmapper.tub.presentation.R;
import fr.bourgmapper.tub.presentation.internal.di.HasComponent;
import fr.bourgmapper.tub.presentation.internal.di.components.CoreFragmentComponent;
import fr.bourgmapper.tub.presentation.internal.di.components.DaggerCoreFragmentComponent;
import fr.bourgmapper.tub.presentation.listener.MainNavigationListener;
import fr.bourgmapper.tub.presentation.presenter.InfoFragmentPresenter;

/**
 * Fragment that shows Info.
 */
public class InfoFragment extends BaseFragment implements HasComponent<CoreFragmentComponent> {

    @Inject
    InfoFragmentPresenter infoFragmentPresenter;

    private CoreFragmentComponent coreFragmentComponent;
    private MainNavigationListener mainNavigationListener;

    public InfoFragment() {
        setRetainInstance(true);
    }
    
    public static InfoFragment newInstance() {

        Bundle args = new Bundle();
        
        InfoFragment fragment = new InfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof MainNavigationListener) {
            this.mainNavigationListener = (MainNavigationListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initializeInjector();
        this.coreFragmentComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.infoFragmentPresenter.setView(this);
        if (savedInstanceState == null) {
            //Add loading if needed
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        this.infoFragmentPresenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.infoFragmentPresenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.infoFragmentPresenter.destroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mainNavigationListener = null;
    }

    @OnClick({R.id.fragment_info_top_bar_bus, R.id.fragment_info_top_bar_stops})
    public void onTopBarItemSelected(Button button) {
        switch (button.getId()) {
            case R.id.fragment_info_top_bar_bus:
                this.mainNavigationListener.onLinesButtonSelected();
                break;
            case R.id.fragment_info_top_bar_stops:
                this.mainNavigationListener.onStopsButtonSelected();
                break;
        }
    }

    private void initializeInjector() {
        this.coreFragmentComponent = DaggerCoreFragmentComponent.builder()
                .applicationComponent(getApplicationComponent())
                .fragmentModule(getFragmentModule())
                .build();
    }

    @Override
    public CoreFragmentComponent getComponent() {
        return coreFragmentComponent;
    }

}
