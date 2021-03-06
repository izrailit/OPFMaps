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

package org.onepf.opfmaps.yandexweb.delegate.model;

import android.support.annotation.NonNull;

import org.onepf.opfmaps.yandexweb.model.BitmapDescriptor;
import org.onepf.opfmaps.delegate.model.BitmapDescriptorDelegate;

/**
 * @author Roman Savin
 * @since 02.09.2015
 */
public final class YaWebBitmapDescriptorDelegate implements BitmapDescriptorDelegate<BitmapDescriptor> {

    @NonNull
    private final BitmapDescriptor bitmapDescriptor;

    public YaWebBitmapDescriptorDelegate(@NonNull final BitmapDescriptor bitmapDescriptor) {
        this.bitmapDescriptor = bitmapDescriptor;
    }

    @NonNull
    @Override
    public BitmapDescriptor getBitmapDescriptor() {
        return bitmapDescriptor;
    }

    @Override
    public boolean equals(final Object other) {
        return other != null
                && (other == this || other instanceof YaWebBitmapDescriptorDelegate
                && bitmapDescriptor.equals(((YaWebBitmapDescriptorDelegate) other).bitmapDescriptor));
    }

    @Override
    public int hashCode() {
        return bitmapDescriptor.hashCode();
    }

    @Override
    public String toString() {
        return bitmapDescriptor.toString();
    }
}
