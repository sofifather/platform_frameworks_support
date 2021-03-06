/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.car.cluster.navigation;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.util.Preconditions;
import androidx.versionedparcelable.ParcelField;
import androidx.versionedparcelable.ParcelUtils;
import androidx.versionedparcelable.VersionedParcelable;
import androidx.versionedparcelable.VersionedParcelize;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Navigation state data to be displayed on the instrument cluster of a car. This is composed by:
 * <ul>
 * <li>a list of destinations.
 * <li>the immediate step or steps in order to drive towards those destinations.
 * </ul>
 * This information can converted it to/from a {@link Parcelable} by using {@link #toParcelable()}
 * and {@link #fromParcelable(Parcelable)}, in order to be used in IPC (see {@link Parcel}).
 */
@VersionedParcelize
public final class NavigationState implements VersionedParcelable {
    @ParcelField(1)
    List<Step> mSteps;
    @ParcelField(2)
    List<Destination> mDestinations;

    /**
     * Used by {@link VersionedParcelable}

     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    NavigationState() {
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    NavigationState(@NonNull List<Step> steps, @NonNull List<Destination> destinations) {
        mSteps = new ArrayList<>(steps);
        mDestinations = new ArrayList<>(destinations);
    }

    /**
     * Builder for creating a {@link NavigationState}
     */
    public static final class Builder {
        List<Step> mSteps = new ArrayList<>();
        List<Destination> mDestinations = new ArrayList<>();

        /**
         * Add a navigation step. Steps should be provided in order of execution. It is up to the
         * producer to decide how many steps in advance will be provided.
         *
         * @return this object for chaining
         */
        @NonNull
        public Builder addStep(@NonNull Step step) {
            mSteps.add(Preconditions.checkNotNull(step));
            return this;
        }

        /**
         * Add a destination or intermediate stop in the navigation. Destinations should be provided
         * from nearest to furthest.
         *
         * @return this object for chaining
         */
        @NonNull
        public Builder addDestination(@NonNull Destination destination) {
            mDestinations.add(Preconditions.checkNotNull(destination));
            return this;
        }

        /**
         * Returns a {@link NavigationState} built with the provided information.
         */
        @NonNull
        public NavigationState build() {
            return new NavigationState(mSteps, mDestinations);
        }
    }

    /**
     * Returns the navigation steps, in order of execution. It is up to the producer to decide how
     * many steps in advance will be provided. This list should not be mutated.
     */
    @NonNull
    public List<Step> getSteps() {
        return Common.nonNullOrEmpty(mSteps);
    }

    /**
     * Returns the destination and intermediate stops in the navigation, sorted from nearest to
     * furthest. This list should not be mutated.
     */
    @NonNull
    public List<Destination> getDestinations() {
        return Common.nonNullOrEmpty(mDestinations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NavigationState that = (NavigationState) o;
        return Objects.equals(getSteps(), that.getSteps())
                && Objects.equals(getDestinations(), that.getDestinations());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSteps(), getDestinations());
    }

    @Override
    public String toString() {
        return String.format("{steps: %s, destinations: %s}", mSteps, mDestinations);
    }

    /**
     * Returns this {@link NavigationState} as a {@link Parcelable}
     */
    @NonNull
    public Parcelable toParcelable() {
        return ParcelUtils.toParcelable(this);
    }

    /**
     * Creates a {@link NavigationState} based on data stored in the given {@link Parcelable}
     */
    public static NavigationState fromParcelable(@Nullable Parcelable parcelable) {
        return parcelable != null ? ParcelUtils.fromParcelable(parcelable) : new NavigationState();
    }
}
