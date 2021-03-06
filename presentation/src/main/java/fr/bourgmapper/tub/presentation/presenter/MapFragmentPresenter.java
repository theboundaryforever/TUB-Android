package fr.bourgmapper.tub.presentation.presenter;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import fr.bourgmapper.tub.domain.Line;
import fr.bourgmapper.tub.domain.Stop;
import fr.bourgmapper.tub.domain.exception.DefaultErrorBundle;
import fr.bourgmapper.tub.domain.exception.ErrorBundle;
import fr.bourgmapper.tub.domain.interactor.DefaultObserver;
import fr.bourgmapper.tub.domain.interactor.GetLineDetails;
import fr.bourgmapper.tub.domain.interactor.GetLineList;
import fr.bourgmapper.tub.domain.interactor.GetStopDetails;
import fr.bourgmapper.tub.domain.interactor.GetStopList;
import fr.bourgmapper.tub.presentation.exception.ErrorMessageFactory;
import fr.bourgmapper.tub.presentation.internal.di.PerFragment;
import fr.bourgmapper.tub.presentation.mapper.LineModelDataMapper;
import fr.bourgmapper.tub.presentation.mapper.StopModelDataMapper;
import fr.bourgmapper.tub.presentation.model.LineModel;
import fr.bourgmapper.tub.presentation.model.StopModel;
import fr.bourgmapper.tub.presentation.view.MainMapView;

/**
 * {@link Presenter} that controls communication between views and models of the presentation
 * layer.
 */
@PerFragment
public class MapFragmentPresenter implements Presenter {
    private static String TAG = "MapFragmentPresenter";

    private final GetLineList getLineListUseCase;
    private final GetLineDetails getLineDetailsUseCase;
    private final LineModelDataMapper lineModelDataMapper;

    private final GetStopList getStopListUseCase;
    private final GetStopDetails getStopDetailsUseCase;
    private final StopModelDataMapper stopModelDataMapper;

    private MainMapView mainMapView;

    @Inject
    MapFragmentPresenter(GetLineList getLineListUseCase, GetLineDetails getLineDetailsUseCase,
                         GetStopList getStopListUseCase, GetStopDetails getStopDetailsUseCase,
                         LineModelDataMapper lineModelDataMapper, StopModelDataMapper stopModelDataMapper) {
        this.getLineListUseCase = getLineListUseCase;
        this.getLineDetailsUseCase = getLineDetailsUseCase;
        this.lineModelDataMapper = lineModelDataMapper;
        this.getStopListUseCase = getStopListUseCase;
        this.getStopDetailsUseCase = getStopDetailsUseCase;
        this.stopModelDataMapper = stopModelDataMapper;
    }

    public void setView(@NonNull MainMapView view) {
        this.mainMapView = view;
    }


    @Override
    public void resume() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void destroy() {
        this.mainMapView = null;
        this.getLineListUseCase.dispose();
        this.getLineDetailsUseCase.dispose();
        this.getStopListUseCase.dispose();
        this.getStopDetailsUseCase.dispose();
    }

    /**
     * Initializes the presenter by showing/hiding proper views
     * and retrieving stop list and line list.
     */
    public void initialize() {
        loadStopList();
        loadLineList();
        mainMapView.moveCamera(new LatLng(46.205539, 5.227177), 13f);
    }

    /**
     * Loads all stops.
     */
    private void loadStopList() {
        this.hideViewRetryStopList();
        this.showViewLoadingStopList();
        this.getStopList();
    }

    /**
     * Loads all lines.
     */
    private void loadLineList() {
        this.hideViewRetryLineList();
        this.showViewLoadingLineList();
        this.getLineList();
    }

    private void showViewLoadingStopList() {
        this.mainMapView.showLoadingStopList();
    }

    private void hideViewLoadingStopList() {
        this.mainMapView.hideLoadingStopList();
    }

    private void showViewRetryStopList() {
        this.mainMapView.showRetryStopList();
    }

    private void hideViewRetryStopList() {
        this.mainMapView.hideRetryStopList();
    }

    private void showErrorMessageStopList(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.mainMapView.context(),
                errorBundle.getException());
        this.mainMapView.showErrorStopList(errorMessage);
    }

    private void showStopsCollectionInView(Collection<Stop> stopCollection) {
        final Collection<StopModel> stopModelsCollection =
                this.stopModelDataMapper.transform(stopCollection);
        this.mainMapView.renderStopList(stopModelsCollection);
    }


    private void showViewLoadingLineList() {
        this.mainMapView.showLoadingLineList();
    }

    private void hideViewLoadingLineList() {
        this.mainMapView.hideLoadingLineList();
    }

    private void showViewRetryLineList() {
        this.mainMapView.showRetryLineList();
    }

    private void hideViewRetryLineList() {
        this.mainMapView.hideRetryLineList();
    }

    private void showErrorMessageLineList(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(this.mainMapView.context(),
                errorBundle.getException());
        this.mainMapView.showErrorLineList(errorMessage);
    }

    private void showLinesCollectionInView(Collection<Line> lineCollection) {
        final Collection<LineModel> lineModelsCollection =
                this.lineModelDataMapper.transform(lineCollection);
        this.mainMapView.renderLineList(lineModelsCollection);
    }

    private void getStopList() {
        this.getStopListUseCase.execute(new StopListObserver(), null);
    }

    private void getLineList() {
        this.getLineListUseCase.execute(new LineListObserver(), null);
    }

    private final class StopListObserver extends DefaultObserver<List<Stop>> {

        @Override
        public void onComplete() {
            MapFragmentPresenter.this.hideViewLoadingStopList();
        }

        @Override
        public void onError(Throwable e) {
            MapFragmentPresenter.this.hideViewLoadingStopList();
            MapFragmentPresenter.this.showErrorMessageStopList(new DefaultErrorBundle((Exception) e));
            MapFragmentPresenter.this.showViewRetryStopList();
        }

        @Override
        public void onNext(List<Stop> stops) {
            MapFragmentPresenter.this.showStopsCollectionInView(stops);
        }
    }

    private final class LineListObserver extends DefaultObserver<List<Line>> {

        @Override
        public void onComplete() {
            MapFragmentPresenter.this.hideViewLoadingLineList();
        }

        @Override
        public void onError(Throwable e) {
            MapFragmentPresenter.this.hideViewLoadingLineList();
            MapFragmentPresenter.this.showErrorMessageLineList(new DefaultErrorBundle((Exception) e));
            MapFragmentPresenter.this.showViewRetryLineList();
        }

        @Override
        public void onNext(List<Line> lines) {
            MapFragmentPresenter.this.showLinesCollectionInView(lines);
        }
    }
}