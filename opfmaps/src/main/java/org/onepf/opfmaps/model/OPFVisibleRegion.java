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

package org.onepf.opfmaps.model;

import android.os.Parcel;
import android.support.annotation.NonNull;

import org.onepf.opfmaps.OPFMapHelper;
import org.onepf.opfmaps.delegate.model.VisibleRegionDelegate;

/**
 * Contains the four points defining the four-sided polygon that is visible in a map's camera.
 * This polygon can be a trapezoid instead of a rectangle, because a camera can have tilt.
 * If the camera is directly over the center of the camera, the shape is rectangular, but if the camera is tilted,
 * the shape will appear to be a trapezoid whose smallest side is closest to the point of view.
 *
 * @author Roman Savin
 * @since 06.08.2015
 */
public class OPFVisibleRegion implements VisibleRegionDelegate {

    public static final Creator<OPFVisibleRegion> CREATOR = new Creator<OPFVisibleRegion>() {
        @Override
        public OPFVisibleRegion createFromParcel(final Parcel source) {
            return new OPFVisibleRegion(source);
        }

        @Override
        public OPFVisibleRegion[] newArray(final int size) {
            return new OPFVisibleRegion[size];
        }
    };

    @NonNull
    private final VisibleRegionDelegate delegate;

    public OPFVisibleRegion(@NonNull final OPFLatLng nearLeft,
                            @NonNull final OPFLatLng nearRight,
                            @NonNull final OPFLatLng farLeft,
                            @NonNull final OPFLatLng farRight,
                            @NonNull final OPFLatLngBounds latLngBounds) {
        this.delegate = OPFMapHelper.getInstance().getDelegatesFactory()
                .createVisibleRegionDelegate(nearLeft, nearRight, farLeft, farRight, latLngBounds);
    }

    public OPFVisibleRegion(@NonNull final VisibleRegionDelegate delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private OPFVisibleRegion(@NonNull final Parcel parcel) {
        try {
            this.delegate = parcel.readParcelable(Class.forName(parcel.readString()).getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the {@link OPFLatLng} object that defines the far left corner of the camera.
     *
     * @return The {@link OPFLatLng} object that defines the far left corner of the camera.
     */
    @Override
    @NonNull
    public OPFLatLng getFarLeft() {
        return delegate.getFarLeft();
    }

    /**
     * Returns the {@link OPFLatLng} object that defines the far right corner of the camera.
     *
     * @return The {@link OPFLatLng} object that defines the far right corner of the camera.
     */
    @Override
    @NonNull
    public OPFLatLng getFarRight() {
        return delegate.getFarRight();
    }

    /**
     * Returns the smallest bounding box that includes the visible region defined in this class.
     *
     * @return The smallest bounding box that includes the visible region defined in this class.
     */
    @Override
    @NonNull
    public OPFLatLngBounds getLatLngBounds() {
        return delegate.getLatLngBounds();
    }

    /**
     * Returns the {@link OPFLatLng} object that defines the bottom left corner of the camera.
     *
     * @return The {@link OPFLatLng} object that defines the bottom left corner of the camera.
     */
    @Override
    @NonNull
    public OPFLatLng getNearLeft() {
        return delegate.getNearLeft();
    }

    /**
     * Returns the {@link OPFLatLng} object that defines the bottom right corner of the camera.
     *
     * @return The {@link OPFLatLng} object that defines the bottom right corner of the camera.
     */
    @Override
    @NonNull
    public OPFLatLng getNearRight() {
        return delegate.getNearRight();
    }

    @Override
    public int describeContents() {
        return delegate.describeContents();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(delegate.getClass().getCanonicalName());
        dest.writeParcelable(delegate, flags);
    }

    @Override
    public boolean equals(final Object other) {
        return other != null
                && (other == this || other instanceof OPFVisibleRegion
                && delegate.equals(((OPFVisibleRegion) other).delegate));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
