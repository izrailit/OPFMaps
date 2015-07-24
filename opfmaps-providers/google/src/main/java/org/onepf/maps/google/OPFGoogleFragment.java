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

package org.onepf.maps.google;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import org.onepf.opfmaps.OPFMap;
import org.onepf.opfmaps.OPFMapDelegate;
import org.onepf.opfmaps.OPFOnMapClickListener;
import org.onepf.opfmaps.OPFOnMapLoadListener;
import org.onepf.opfmaps.OPFOnMarkerClickListener;
import org.onepf.opfmaps.OPFOnMarkerDragListener;
import org.onepf.opfmaps.OPFTBDInterface;
import org.onepf.opfmaps.model.OPFCircle;
import org.onepf.opfmaps.model.OPFGroundOverlay;
import org.onepf.opfmaps.model.OPFInfoWindowAdapter;
import org.onepf.opfmaps.model.OPFLatLng;
import org.onepf.opfmaps.model.OPFMarker;
import org.onepf.opfmaps.model.OPFPolygon;
import org.onepf.opfmaps.model.OPFPolyline;
import org.onepf.opfmaps.model.OPFTileOverlay;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Created by akarimova on 09.06.15.
 */
public class OPFGoogleFragment extends MapFragment implements OPFMapDelegate, OPFTBDInterface<PolylineOptions, PolygonOptions, CircleOptions, MarkerOptions, LatLng> {
    private static final String EXTRA = "MapOptions";
    private GoogleMap googleMap;
    private Boolean initialized;

    @Override
    public boolean isReady() {
        return initialized != null && initialized;
    }

    public static OPFGoogleFragment newInstance(GoogleMapOptions options) {
        OPFGoogleFragment opfGoogleFragment = new OPFGoogleFragment();
        Bundle bundle = new Bundle(1);
        bundle.putParcelable(EXTRA, options);
        opfGoogleFragment.setArguments(bundle);
        return opfGoogleFragment;
    }

    @Override
    public void addMarker(OPFMarker opfMarker) {
        googleMap.addMarker(marker(opfMarker));
    }

    @Override
    public void addPolyline(OPFPolyline opfPolyline) {
        PolylineOptions polylineOptions = polyline(opfPolyline);
        googleMap.addPolyline(polylineOptions);
    }

    @Override
    public void addCircle(OPFCircle opfCircle) {
        CircleOptions circleOptions = circle(opfCircle);
        googleMap.addCircle(circleOptions);
    }

    @Override
    public void addPolygon(OPFPolygon opfPolygon) {
        PolygonOptions polygon = polygon(opfPolygon);
        googleMap.addPolygon(polygon);
    }

    @Override
    public void zoom(float zoom) {
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    @Override
    public void setInfoWindowAdapter(@NonNull final OPFInfoWindowAdapter adapter) {
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return adapter.getInfoWindow(makeOPFMarker(marker));
            }

            @Override
            public View getInfoContents(Marker marker) {
                return adapter.getInfoContents(makeOPFMarker(marker));
            }
        });
    }

    @Override
    public void setMyLocationEnabled(boolean enabled) {
        googleMap.setMyLocationEnabled(enabled);
    }

    @Override
    public void setTrafficEnabled(boolean enabled) {
        googleMap.setTrafficEnabled(enabled);
    }

    @Override
    public void clear() {
        googleMap.clear();
    }

    @Override
    public void setMapType(OPFMap.MAP_TYPE mapType) {
        int googleMapType;
        switch (mapType) {
            case NORMAL:
                googleMapType = GoogleMap.MAP_TYPE_NORMAL;
                break;
            case HYBRID:
                googleMapType = GoogleMap.MAP_TYPE_HYBRID;
                break;
            case SATELLITE:
                googleMapType = GoogleMap.MAP_TYPE_SATELLITE;
                break;
            case TERRAIN:
                googleMapType = GoogleMap.MAP_TYPE_TERRAIN;
                break;
            case NONE:
                googleMapType = GoogleMap.MAP_TYPE_NONE;
                break;
            case UNKNOWN:
                googleMapType = GoogleMap.MAP_TYPE_NONE;
                break;
            default:
                throw new IllegalStateException("Unknown map type: " + mapType);
        }
        googleMap.setMapType(googleMapType);
    }

    @Override
    public OPFMap.MAP_TYPE getMapType() {
        OPFMap.MAP_TYPE mapType = OPFMap.MAP_TYPE.UNKNOWN;
        int googleMapType = googleMap.getMapType();
        if (googleMapType == GoogleMap.MAP_TYPE_NORMAL) {
            mapType = OPFMap.MAP_TYPE.NORMAL;
        } else if (googleMapType == GoogleMap.MAP_TYPE_HYBRID) {
            mapType = OPFMap.MAP_TYPE.HYBRID;
        } else if (googleMapType == GoogleMap.MAP_TYPE_SATELLITE) {
            mapType = OPFMap.MAP_TYPE.SATELLITE;
        } else if (googleMapType == GoogleMap.MAP_TYPE_TERRAIN) {
            mapType = OPFMap.MAP_TYPE.TERRAIN;
        } else if (googleMapType == GoogleMap.MAP_TYPE_NONE) {
            mapType = OPFMap.MAP_TYPE.NONE;
        }
        return mapType;
    }

    @Override
    public Location getMyLocation() {
        return googleMap.getMyLocation();
    }

    @Override
    public void addGroundOverlay(OPFGroundOverlay opfGroundOverlay) {

    }

    @Override
    public void addTileOverlay(OPFTileOverlay opfTileOverlay) {

    }

    @Override
    public void addPadding(int left, int top, int right, int bottom) {
        googleMap.setPadding(left, top, right, bottom);
    }

    @Override
    public void setIndoorEnabled(boolean enabled) {
        googleMap.setIndoorEnabled(enabled);
    }

    @Override
    public void setBuildingsEnabled(boolean enabled) {
        googleMap.setBuildingsEnabled(enabled);
    }

    @Override
    public float getMaxZoomLevel() {
        return googleMap.getMaxZoomLevel();
    }

    @Override
    public float getMinZoomLevel() {
        return googleMap.getMinZoomLevel();
    }

    @Override
    public void load(final OPFOnMapLoadListener mapLoadedListener) {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (googleMap != null && getActivity() != null) {
                    OPFGoogleFragment.this.googleMap = googleMap;
                    if (mapLoadedListener != null) {
                        mapLoadedListener.onMapLoad();
                    }
                    initialized = true;
                } else {
                    if (mapLoadedListener != null) {
                        mapLoadedListener.onError();
                    }
                    initialized = false;
                }
            }
        });
    }

    @Override
    public void setOnMarkerDragListener(final OPFOnMarkerDragListener onMarkerDragListener) {
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(com.google.android.gms.maps.model.Marker marker) {
                OPFMarker opfMarker = makeOPFMarker(marker);
                onMarkerDragListener.onMarkerDragStart(opfMarker);
            }

            @Override
            public void onMarkerDrag(com.google.android.gms.maps.model.Marker marker) {
                OPFMarker opfMarker = makeOPFMarker(marker);
                onMarkerDragListener.onMarkerDrag(opfMarker);
            }

            @Override
            public void onMarkerDragEnd(com.google.android.gms.maps.model.Marker marker) {
                OPFMarker opfMarker = makeOPFMarker(marker);
                onMarkerDragListener.onMarkerDragEnd(opfMarker);
            }
        });
    }

    private static OPFMarker makeOPFMarker(Marker marker) {
        OPFMarker.Builder markerBuilder = new OPFMarker.Builder();
        markerBuilder.setLatLng(new OPFLatLng(marker.getPosition().latitude, marker.getPosition().longitude))
                .setAlpha(marker.getAlpha())
                .setRotation(marker.getRotation())
                .setTitle(marker.getTitle())
                .setSnippet(marker.getSnippet())
                .setDraggable(marker.isDraggable())
                .setFlat(marker.isFlat())
                .setVisible(marker.isVisible());
        return markerBuilder.build();
    }

    @Override
    public void setOnMarkerClickListener(final OPFOnMarkerClickListener onMarkerClickListener) {
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                OPFMarker opfMarker = makeOPFMarker(marker);
                onMarkerClickListener.onMarkerClick(opfMarker);
                return false; //todo check
            }
        });
    }

    @Override
    public void setOnMapClickListener(final OPFOnMapClickListener onMapClickListener) {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                onMapClickListener.onMapClick(new OPFLatLng(latLng.latitude, latLng.longitude));
            }
        });
    }

    @Override
    public PolylineOptions polyline(OPFPolyline opfPolyline) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.width(opfPolyline.getWidth());
        polylineOptions.geodesic(opfPolyline.isGeodesic());
        polylineOptions.visible(opfPolyline.isVisible());
        polylineOptions.color(opfPolyline.getColor());
        polylineOptions.zIndex(opfPolyline.getzIndex());
        List<OPFLatLng> points = opfPolyline.getPoints();
        for (OPFLatLng opfLatLng : points) {
            LatLng latLng = latLng(opfLatLng);
            polylineOptions.add(latLng);
        }
        return polylineOptions;
    }


    @Override
    public PolygonOptions polygon(OPFPolygon polygon) {
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.geodesic(polygon.isGeodesic());
        polygonOptions.visible(polygon.isVisible());
        polygonOptions.zIndex(polygon.getzIndex());
        polygonOptions.fillColor(polygon.getFillColor());
        polygonOptions.strokeColor(polygon.getStrokeColor());
        polygonOptions.strokeWidth(polygon.getStrokeWidth());
        for (OPFLatLng latLng : polygon.getPoints()) {
            polygonOptions.add(latLng(latLng));
        }
        return polygonOptions;
    }

    @Override
    public CircleOptions circle(OPFCircle opfCircle) {
        CircleOptions circleOptions = new CircleOptions();
        OPFLatLng center = opfCircle.getCenter();
        LatLng latLng = latLng(center);
        circleOptions.center(latLng);
        circleOptions.radius(opfCircle.getRadius());
        circleOptions.zIndex(opfCircle.getzIndex());
        circleOptions.fillColor(opfCircle.getFillColor());
        circleOptions.strokeColor(opfCircle.getStrokeColor());
        circleOptions.strokeWidth(opfCircle.getStrokeWidth());
        circleOptions.visible(opfCircle.isVisible());
        return circleOptions;
    }


    @Override
    public MarkerOptions marker(OPFMarker opfMarker) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng(opfMarker.getLatLng()));
        markerOptions.title(opfMarker.getTitle());
        markerOptions.snippet(opfMarker.getSnippet());
        markerOptions.icon(BitmapDescriptorFactory.fromResource(opfMarker.getIconId()));
        markerOptions.visible(opfMarker.isVisible());
        markerOptions.draggable(opfMarker.isDraggable());
        markerOptions.alpha(opfMarker.getAlpha());
        markerOptions.rotation(opfMarker.getRotation());
//        markerOptions.anchor(opfMarker.getAnchorU(), opfMarker.getAnchorV());
        markerOptions.flat(opfMarker.isFlat());
        return markerOptions;
    }

    @Override
    public LatLng latLng(OPFLatLng opfLatLng) {
        return new LatLng(opfLatLng.getLatitude(), opfLatLng.getLongitude());
    }
}
