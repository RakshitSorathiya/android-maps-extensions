/*
 * Copyright (C) 2013 Maciej Górski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.androidmapsextensions.impl;

import com.androidmapsextensions.GoogleMap.OnPolylineClickListener;
import com.androidmapsextensions.Polyline;
import com.androidmapsextensions.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PolylineManager {

    private final IGoogleMap factory;

    private final Map<com.google.android.gms.maps.model.Polyline, Polyline> polylines;

    public PolylineManager(IGoogleMap factory) {
        this.factory = factory;
        this.polylines = new HashMap<>();
    }

    public Polyline addPolyline(PolylineOptions polylineOptions) {
        Polyline polyline = createPolyline(polylineOptions.real);
        polyline.setData(polylineOptions.getData());
        return polyline;
    }

    private Polyline createPolyline(com.google.android.gms.maps.model.PolylineOptions polylineOptions) {
        com.google.android.gms.maps.model.Polyline real = factory.addPolyline(polylineOptions);
        Polyline polyline = new DelegatingPolyline(real, this);
        polylines.put(real, polyline);
        return polyline;
    }

    public void clear() {
        polylines.clear();
    }

    public List<Polyline> getPolylines() {
        return new ArrayList<Polyline>(polylines.values());
    }

    public void onRemove(com.google.android.gms.maps.model.Polyline real) {
        polylines.remove(real);
    }

    public void setOnPolylineClickListener(OnPolylineClickListener onPolylineClickListener) {
        com.google.android.gms.maps.GoogleMap.OnPolylineClickListener realOnPolylineClickListener = null;
        if (onPolylineClickListener != null) {
            realOnPolylineClickListener = new DelegatingOnPolylineClickListener(onPolylineClickListener);
        }
        factory.setOnPolylineClickListener(realOnPolylineClickListener);
    }

    private class DelegatingOnPolylineClickListener implements com.google.android.gms.maps.GoogleMap.OnPolylineClickListener {

        private final OnPolylineClickListener onPolylineClickListener;

        public DelegatingOnPolylineClickListener(OnPolylineClickListener onPolylineClickListener) {
            this.onPolylineClickListener = onPolylineClickListener;
        }

        @Override
        public void onPolylineClick(com.google.android.gms.maps.model.Polyline polyline) {
            onPolylineClickListener.onPolylineClick(polylines.get(polyline));
        }
    }
}
