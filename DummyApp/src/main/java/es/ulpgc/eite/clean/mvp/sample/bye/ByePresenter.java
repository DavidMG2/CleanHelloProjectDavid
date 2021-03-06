package es.ulpgc.eite.clean.mvp.sample.bye;


import android.content.Context;
import android.util.Log;

import es.ulpgc.eite.clean.mvp.ContextView;
import es.ulpgc.eite.clean.mvp.GenericActivity;
import es.ulpgc.eite.clean.mvp.GenericPresenter;
import es.ulpgc.eite.clean.mvp.sample.app.Mediator;

public class ByePresenter
        extends GenericPresenter
            <Bye.PresenterToView, Bye.PresenterToModel, Bye.ModelToPresenter, ByeModel>
        implements Bye.ViewToPresenter, Bye.ModelToPresenter,
            Bye.HelloToBye, Bye.ByeToHello, Bye.ToBye {

    private boolean toolbarVisible, buttonClicked, textVisible, progressBarVisible;
    private boolean textHelloVisible;
    private String textHello;

    /**
     * Operation called during VIEW creation in {@link GenericActivity#onResume(Class, Object)}
     * Responsible to initialize MODEL.
     * Always call {@link GenericPresenter#onCreate(Class, Object)} to initialize the object
     * Always call  {@link GenericPresenter#setView(ContextView)} to save a PresenterToView reference
     *
     * @param view The current VIEW instance
     */
    @Override
    public void onCreate(Bye.PresenterToView view) {
        super.onCreate(ByeModel.class, this);
        setView(view);
        Log.d(TAG, "calling onCreate()");

        Log.d(TAG, "calling startingScreen()");
        Mediator.Lifecycle mediator = (Mediator.Lifecycle) getApplication();
        mediator.startingScreen(this);

    }

    /**
     * Operation called by VIEW after its reconstruction.
     * Always call {@link GenericPresenter#setView(ContextView)}
     * to save the new instance of PresenterToView
     *
     * @param view The current VIEW instance
     */
    @Override
    public void onResume(Bye.PresenterToView view) {
        setView(view);
        Log.d(TAG, "calling onResume()");

        Mediator.Lifecycle mediator = (Mediator.Lifecycle) getApplication();
        mediator.resumingScreen(this);
    }

    /**
     * Helper method to inform Presenter that a onBackPressed event occurred
     * Called by {@link GenericActivity}
     */
    @Override
    public void onBackPressed() {
        Log.d(TAG, "calling onBackPressed()");

    }

    /**
     * Hook method called when the VIEW is being destroyed or
     * having its configuration changed.
     * Responsible to maintain MVP synchronized with Activity lifecycle.
     * Called by onDestroy methods of the VIEW layer, like: {@link GenericActivity#onDestroy()}
     *
     * @param isChangingConfiguration true: configuration changing & false: being destroyed
     */
    @Override
    public void onDestroy(boolean isChangingConfiguration) {
        super.onDestroy(isChangingConfiguration);

        if (isChangingConfiguration) {
            Log.d(TAG, "calling onChangingConfiguration()");
        } else {
            Log.d(TAG, "calling onDestroy()");
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // Model To Presenter /////////////////////////////////////////////////////////////


    public void onGetMessageTaskFinished(String text) {

        if (isViewRunning()) {
            // pasar el texto a la vista  (aplicar estado)
            getView().setText(text);

            // actualizar estado (fijar estado)
            textVisible = true;

            // actualizar estado (fijar estado)
            progressBarVisible = false;

            checkProgressBarVisibility();
            checkTextVisibility();

        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    // View To Presenter /////////////////////////////////////////////////////////////


    @Override
    public void onSayByeBtnClicked() {

        if (isViewRunning()) {

            textHelloVisible = false;

            if (buttonClicked) {
                getModel().resetGetMessageTask();
            }

            buttonClicked = true;

            if (textVisible) {
                //getView().hideText();
                textVisible = false;
            }

            // actualizar estado (fijar estado)
            progressBarVisible = true;


            checkTextVisibility();
            checkProgressBarVisibility();

            // pedir el texto al modelo asíncronamente
            // al finalizar el modelo llamará a onGetMessageTaskFinished()
            getModel().startGetMessageTask();

        }
    }

    @Override
    public void onBackToHelloBtnClicked() {

        // pedir al mediador que inicie la pantalla de bye
        Mediator.Navigation mediator = (Mediator.Navigation) getApplication();
        mediator.backToHelloScreen(this);
    }


    ///////////////////////////////////////////////////////////////////////////////////
    // State /////////////////////////////////////////////////////////////////////////


    @Override
    public void setToolbarVisibility(boolean visible) {
        toolbarVisible = visible;
    }

    @Override
    public void setTextVisibility(boolean visible) {
        textVisible = visible;
    }


    ///////////////////////////////////////////////////////////////////////////////////
    // To Bye //////////////////////////////////////////////////////////////////////

    @Override
    public void onScreenStarted() {
        Log.d(TAG, "calling onScreenStarted()");
        setCurrentState();

        if (isViewRunning()) {

            if (textHelloVisible) {
                getView().showText();
                getView().setText(textHello);
            }
        }
    }

    @Override
    public void setProgressBarVisibility(boolean visible) {
        progressBarVisible = visible;
    }

    @Override
    public void setButtonClicked(boolean clicked) {
        buttonClicked = clicked;
    }

    @Override
    public void setHelloTextVisibility(boolean visible) {
        textHelloVisible = visible;
    }

    @Override
    public void setHelloText(String text) {
        this.textHello = text;
    }

    @Override
    public void onScreenResumed() {
        Log.d(TAG, "calling onScreenResumed()");

        setCurrentState();

        if (isViewRunning()) {
            if (textHelloVisible) {
                getView().showText();
                getView().setText(textHello);
            } else {

                if (buttonClicked) {
                    getModel().startGetMessageTask();
                }
            }
        }


    }


    ///////////////////////////////////////////////////////////////////////////////////
    // Bye To //////////////////////////////////////////////////////////////////////


    @Override
    public Context getManagedContext() {
        return getActivityContext();
    }

    @Override
    public void destroyView() {
        if (isViewRunning()) {
            getView().finishScreen();
        }
    }

    @Override
    public boolean isToolbarVisible() {
        return toolbarVisible;
    }

    @Override
    public boolean isTextVisible() {
        return textVisible;
    }

    @Override
    public String getText() {
        /// TODO: 15/3/18 Obtener el estado desde el mediador de forma async
        return getModel().getText();
    }

    ///////////////////////////////////////////////////////////////////////////////////



    private void setCurrentState() {
        Log.d(TAG, "calling setCurrentState()");

        if (isViewRunning()) {
            getView().setSayByeLabel(getModel().getSayByeLabel());
            getView().setBackToHelloLabel(getModel().getBackToHelloLabel());
            //getView().setText(getModel().getText());

        }

        checkToolbarVisibility();
        checkTextVisibility();
        checkProgressBarVisibility();

    }


    private void checkProgressBarVisibility() {
        if (isViewRunning()) {
            if (!progressBarVisible) {
                getView().hideProgressBar();
            } else {
                getView().showProgressBar();
            }
        }
    }


    private void checkToolbarVisibility() {
        if (isViewRunning()) {
            if (!toolbarVisible) {
                getView().hideToolbar();
            }
        }
    }

    private void checkTextVisibility() {
        if (isViewRunning()) {
            if (!textVisible) {
                getView().hideText();
            } else {
                getView().showText();
            }
        }
    }


}
