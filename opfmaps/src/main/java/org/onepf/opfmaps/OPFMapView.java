/*
 * Copyright 2012-2015 One Platform Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onepf.opfmaps;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import org.onepf.opfmaps.delegate.MapViewDelegate;
import org.onepf.opfmaps.factory.DelegatesAbstractFactory;
import org.onepf.opfmaps.listener.OPFOnMapReadyCallback;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Represents a {@link View} for displaying a map.
 * Life cycle events of the parent {@link android.app.Activity} or {@link android.app.Fragment}
 * must be forwarded to the {@code View} by calling the corresponding methods in this class:
 * <p/>
 * 1. {@link #onCreate(Bundle)}
 * 2. {@link #onResume()}
 * 3. {@link #onPause()}
 * 4. {@link #onDestroy()}
 * 5. {@link #onSaveInstanceState(Bundle)}
 * 6. {@link #onLowMemory()}
 * <p/>
 * For automatic life cycle management, use a {@link OPFMapFragment} or {@link OPFSupportMapFragment} instead.
 *
 * @author Roman Savin
 * @since 07.08.2015
 */
public class OPFMapView extends FrameLayout implements MapViewDelegate {

    @NonNull
    private final MapViewDelegate delegate;

    public OPFMapView(final Context context) {
        super(context);
        this.delegate = OPFMapHelper.getInstance().getDelegatesFactory().createMapViewDelegate(context);
        addView((View) delegate, MATCH_PARENT, MATCH_PARENT);
    }

    public OPFMapView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        this.delegate = createDelegateFromAttributes(context, attrs);
        addView((View) delegate, MATCH_PARENT, MATCH_PARENT);
    }

    public OPFMapView(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.delegate = createDelegateFromAttributes(context, attrs);
        addView((View) delegate, MATCH_PARENT, MATCH_PARENT);
    }

    public OPFMapView(@NonNull final Context context, @NonNull final OPFMapOptions options) {
        super(context);
        this.delegate = OPFMapHelper.getInstance().getDelegatesFactory().createMapViewDelegate(context, options);
        addView((View) delegate, MATCH_PARENT, MATCH_PARENT);
    }

    /**
     * Get an {@link OPFMap} asynchronously with a callback after it is ready to be used.
     * This method must be called from the main thread. The callback will be invoked on the main thread.
     * @param callback The callback to invoke when the map is ready to use.
     */
    @MainThread
    @Override
    public void getMapAsync(@NonNull final OPFOnMapReadyCallback callback) {
        delegate.getMapAsync(callback);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        delegate.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        delegate.onResume();
    }

    @Override
    public void onPause() {
        delegate.onPause();
    }

    @Override
    public void onDestroy() {
        delegate.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@Nullable final Bundle outState) {
        delegate.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        delegate.onLowMemory();
    }

    @NonNull
    private MapViewDelegate createDelegateFromAttributes(final Context context, final AttributeSet attrs) {
        final DelegatesAbstractFactory factory = OPFMapHelper.getInstance().getDelegatesFactory();
        final OPFMapOptions options = OPFMapOptions.createFromAttributes(context, attrs);
        if (options != null) {
            return factory.createMapViewDelegate(context, options);
        }
        return factory.createMapViewDelegate(context);
    }
}
