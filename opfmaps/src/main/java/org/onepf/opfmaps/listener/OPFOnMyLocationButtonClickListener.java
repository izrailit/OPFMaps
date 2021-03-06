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

package org.onepf.opfmaps.listener;

/**
 * Interface definition for a callback to be invoked when the My Location button is clicked.
 *
 * @author Roman Savin
 * @since 30.07.2015
 */
public interface OPFOnMyLocationButtonClickListener {

    /**
     * Called when the my location button has been clicked.
     *
     * @return {@code true} if the listener has consumed the event, {@code false} otherwise.
     */
    boolean onMyLocationButtonClick();
}
