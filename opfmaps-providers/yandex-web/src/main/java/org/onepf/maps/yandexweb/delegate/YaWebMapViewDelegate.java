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

package org.onepf.maps.yandexweb.delegate;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.onepf.maps.yandexweb.jsi.JSIOnCameraChangeListener;
import org.onepf.maps.yandexweb.jsi.JSIOnMapReadyCallback;
import org.onepf.maps.yandexweb.jsi.JSIOnMapTypeChangeListener;
import org.onepf.maps.yandexweb.jsi.JSMapStateInjector;
import org.onepf.maps.yandexweb.jsi.JSYandexMap;
import org.onepf.maps.yandexweb.listener.OnCameraChangeListener;
import org.onepf.maps.yandexweb.listener.OnMapReadyCallback;
import org.onepf.maps.yandexweb.listener.OnMapTypeChangeListener;
import org.onepf.maps.yandexweb.model.CameraPosition;
import org.onepf.maps.yandexweb.model.LatLng;
import org.onepf.maps.yandexweb.model.YaWebMapOptions;
import org.onepf.opfmaps.OPFMap;
import org.onepf.opfmaps.delegate.MapViewDelegate;
import org.onepf.opfmaps.listener.OPFOnMapReadyCallback;
import org.onepf.opfmaps.model.OPFMapType;
import org.onepf.opfutils.OPFLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Roman Savin
 * @since 02.09.2015
 */
public class YaWebMapViewDelegate extends WebView
        implements MapViewDelegate, OnMapReadyCallback, OnCameraChangeListener, OnMapTypeChangeListener {

    private static final String MAP_HTML_FILE_NAME = "yandex-map.html";

    private static final String MAP_STATE_BUNDLE_KEY = "org.onepf.maps.yandexweb.delegate.MAP_STATE_BUNDLE_KEY";

    //todo change map state after map changing
    @NonNull
    private MapState mapState;

    @Nullable
    private final YaWebMapOptions options;
    @Nullable
    private OPFOnMapReadyCallback onMapReadyCallback;

    @Nullable
    private Bundle savedInstanceState;
    private boolean needLoad;
    private boolean isCreated;

    public YaWebMapViewDelegate(final Context context) {
        this(context, null);
    }

    public YaWebMapViewDelegate(@NonNull final Context context,
                                @Nullable final YaWebMapOptions options) {
        super(context);
        this.options = options;
        this.mapState = new MapState();
    }

    @Override
    public void getMapAsync(@NonNull final OPFOnMapReadyCallback callback) {
        this.onMapReadyCallback = callback;
        if (isCreated) {
            loadMap();
            needLoad = false;
        } else {
            needLoad = true;
        }
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        addJavascriptInterface(new JSIOnCameraChangeListener(this), JSIOnCameraChangeListener.JS_INTERFACE_NAME);
        addJavascriptInterface(new JSIOnMapTypeChangeListener(this), JSIOnMapTypeChangeListener.JS_INTERFACE_NAME);

        isCreated = true;
        if (needLoad) {
            loadMap();
            needLoad = false;
        }
    }

    @Override
    public void onDestroy() {
        isCreated = false;
        onMapReadyCallback = null;
        savedInstanceState = null;

        removeJavascriptInterface(JSIOnCameraChangeListener.JS_INTERFACE_NAME);
        removeJavascriptInterface(JSIOnMapTypeChangeListener.JS_INTERFACE_NAME);
    }

    @Override
    public void onSaveInstanceState(@Nullable final Bundle outState) {
        if (outState == null) {
            return;
        }

        outState.putParcelable(MAP_STATE_BUNDLE_KEY, mapState);
    }

    @Override
    public void onLowMemory() {
        //nothing
    }

    @Override
    public void onMapReady() {
        if (onMapReadyCallback != null) {
            onMapReadyCallback.onMapReady(new OPFMap(new YaWebMapDelegate(this)));

            removeJavascriptInterface(JSIOnMapReadyCallback.JS_INTERFACE_NAME);
            onMapReadyCallback = null;
        }
    }

    @Override
    public void onCameraChange(@NonNull final CameraPosition cameraPosition) {
        OPFLog.logMethod(cameraPosition);
        mapState.setCenter(cameraPosition.getTarget());
        mapState.setZoomLevel(cameraPosition.getZoom());
    }

    @Override
    public void onTypeChange(@NonNull final OPFMapType type) {
        OPFLog.logMethod(type);
        mapState.setMapType(type);
    }

    @NonNull
    public OPFMapType getMapType() {
        return mapState.getMapType();
    }

    @NonNull
    public LatLng getCenter() {
        return mapState.getCenter();
    }

    public float getZoomLevel() {
        return mapState.getZoomLevel();
    }

    public boolean isMyLocationEnabled() {
        return mapState.isMyLocationEnabled();
    }

    public boolean isMyLocationButtonEnabled() {
        return mapState.isMyLocationButtonEnabled();
    }

    public boolean isScrollGesturesEnabled() {
        return mapState.isScrollGesturesEnabled();
    }

    public boolean isZoomControlsEnabled() {
        return mapState.isZoomControlsEnabled();
    }

    public boolean isZoomGesturesEnabled() {
        return mapState.isZoomGesturesEnabled();
    }

    public void setMapType(@NonNull final OPFMapType mapType) {
        mapState.setMapType(mapType);
        JSYandexMap.setType(this, mapType);
    }

    public void setCenter(@NonNull final LatLng center) {
        mapState.setCenter(center);
        JSYandexMap.setCenter(this, center);
    }

    public void setZoomLevel(final float zoomLevel) {
        mapState.setZoomLevel(zoomLevel);
        JSYandexMap.setZoomLevel(this, zoomLevel);
    }

    public void setMyLocationEnabled(final boolean isMyLocationEnabled) {
        mapState.setIsMyLocationEnabled(isMyLocationEnabled);
        JSYandexMap.setMyLocationEnabled(this, isMyLocationEnabled);
    }

    public void setMyLocationButtonEnabled(final boolean isMyLocationButtonEnabled) {
        mapState.setIsMyLocationButtonEnabled(isMyLocationButtonEnabled);
        JSYandexMap.setMyLocationButtonEnabled(this, isMyLocationButtonEnabled);
    }

    public void setScrollGesturesEnabled(final boolean isScrollGesturesEnabled) {
        mapState.setIsScrollGesturesEnabled(isScrollGesturesEnabled);
        JSYandexMap.setScrollGesturesEnabled(this, isScrollGesturesEnabled);
    }

    public void setZoomControlsEnabled(final boolean isZoomControlsEnabled) {
        mapState.setIsZoomControlsEnabled(isZoomControlsEnabled);
        JSYandexMap.setZoomControlsEnabled(this, isZoomControlsEnabled);
    }

    public void setZoomGesturesEnabled(final boolean isZoomGesturesEnabled) {
        mapState.setIsZoomGesturesEnabled(isZoomGesturesEnabled);
        JSYandexMap.setZoomGesturesEnabled(this, isZoomGesturesEnabled);
    }

    private void loadMap() {
        initMapState();

        addJavascriptInterface(new JSIOnMapReadyCallback(this), JSIOnMapReadyCallback.JS_INTERFACE_NAME);
        setWebViewClient(new WebViewClient());

        final WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(false);
        settings.setDefaultTextEncodingName("UTF-8");
        loadData(JSMapStateInjector.injectMapState(getDataString(), mapState), "text/html", "UTF-8");
    }

    private void initMapState() {
        if (savedInstanceState != null) {
            final MapState savedMapState = savedInstanceState.getParcelable(MAP_STATE_BUNDLE_KEY);
            if (savedMapState != null) {
                mapState = savedMapState;
            }
        } else if (options != null) {
            mapState.setMapType(options.getMapType());

            final CameraPosition cameraPosition = options.getCamera();
            if (cameraPosition != null) {
                mapState.setCenter(cameraPosition.getTarget());
                mapState.setZoomLevel(cameraPosition.getZoom());
            }

            mapState.setIsZoomControlsEnabled(options.getZoomControlsEnabled() == null ? true : options.getZoomControlsEnabled());
            mapState.setIsZoomGesturesEnabled(options.getZoomGesturesEnabled() == null ? true : options.getZoomGesturesEnabled());
            mapState.setIsScrollGesturesEnabled(options.getScrollGesturesEnabled() == null ? true : options.getScrollGesturesEnabled());
        }
    }

    private String getDataString() {
        try {
            final InputStream inputStream = getResources().getAssets().open(MAP_HTML_FILE_NAME);
            final String dataString = convertStreamToString(inputStream);
            inputStream.close();
            return dataString;
        } catch (IOException e) {
            throw new RuntimeException("unable to load asset " + MAP_HTML_FILE_NAME);
        }
    }

    private String convertStreamToString(@NonNull final InputStream is) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        final StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        reader.close();
        return sb.toString();
    }

    public static final class MapState implements Parcelable {

        public static Creator<MapState> CREATOR = new Creator<MapState>() {
            @Override
            public MapState createFromParcel(final Parcel source) {
                return new MapState(source);
            }

            @Override
            public MapState[] newArray(final int size) {
                return new MapState[size];
            }
        };

        private static final float MIN_ZOOM_LEVEL = 3.0f;

        @NonNull
        private OPFMapType mapType;
        @NonNull
        private LatLng center;
        private float zoomLevel;
        private boolean isZoomControlsEnabled;
        private boolean isZoomGesturesEnabled;
        private boolean isScrollGesturesEnabled;
        private boolean isMyLocationEnabled;
        private boolean isMyLocationButtonEnabled;

        public MapState() {
            this.mapType = OPFMapType.NORMAL;
            this.center = new LatLng(0.0, 0.0);
            this.zoomLevel = MIN_ZOOM_LEVEL;
            this.isZoomControlsEnabled = true;
            this.isZoomGesturesEnabled = true;
            this.isScrollGesturesEnabled = true;
            this.isMyLocationEnabled = false;
            this.isMyLocationButtonEnabled = true;
        }

        private MapState(@NonNull final Parcel parcel) {
            this.mapType = OPFMapType.fromId(parcel.readInt());
            this.center = parcel.readParcelable(LatLng.class.getClassLoader());
            this.isZoomControlsEnabled = parcel.readByte() != 0;
            this.isZoomGesturesEnabled = parcel.readByte() != 0;
            this.isScrollGesturesEnabled = parcel.readByte() != 0;
            this.isMyLocationEnabled = parcel.readByte() != 0;
            this.isMyLocationButtonEnabled = parcel.readByte() != 0;
        }

        @NonNull
        public OPFMapType getMapType() {
            return mapType;
        }

        @NonNull
        public LatLng getCenter() {
            return center;
        }

        public float getZoomLevel() {
            return zoomLevel;
        }

        public boolean isZoomControlsEnabled() {
            return isZoomControlsEnabled;
        }

        public boolean isZoomGesturesEnabled() {
            return isZoomGesturesEnabled;
        }

        public boolean isScrollGesturesEnabled() {
            return isScrollGesturesEnabled;
        }

        public boolean isMyLocationEnabled() {
            return isMyLocationEnabled;
        }

        public boolean isMyLocationButtonEnabled() {
            return isMyLocationButtonEnabled;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeInt(mapType.getId());
            dest.writeParcelable(center, flags);
            dest.writeFloat(zoomLevel);
            dest.writeByte((byte) (isZoomControlsEnabled ? 1 : 0));
            dest.writeByte((byte) (isZoomGesturesEnabled ? 1 : 0));
            dest.writeByte((byte) (isScrollGesturesEnabled ? 1 : 0));
            dest.writeByte((byte) (isMyLocationEnabled ? 1 : 0));
            dest.writeByte((byte) (isMyLocationButtonEnabled ? 1 : 0));
        }

        private void setMapType(@NonNull final OPFMapType mapType) {
            this.mapType = mapType;
        }

        private void setCenter(@NonNull final LatLng center) {
            this.center = center;
        }

        private void setZoomLevel(final float zoomLevel) {
            this.zoomLevel = zoomLevel;
        }

        private void setIsZoomControlsEnabled(final boolean isZoomControlsEnabled) {
            this.isZoomControlsEnabled = isZoomControlsEnabled;
        }

        private void setIsZoomGesturesEnabled(final boolean isZoomGesturesEnabled) {
            this.isZoomGesturesEnabled = isZoomGesturesEnabled;
        }

        private void setIsScrollGesturesEnabled(final boolean isScrollGesturesEnabled) {
            this.isScrollGesturesEnabled = isScrollGesturesEnabled;
        }

        private void setIsMyLocationEnabled(final boolean isMyLocationEnabled) {
            this.isMyLocationEnabled = isMyLocationEnabled;
        }

        private void setIsMyLocationButtonEnabled(final boolean isMyLocationButtonEnabled) {
            this.isMyLocationButtonEnabled = isMyLocationButtonEnabled;
        }
    }
}
