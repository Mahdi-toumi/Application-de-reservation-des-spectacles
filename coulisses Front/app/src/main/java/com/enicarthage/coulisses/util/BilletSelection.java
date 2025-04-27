package com.enicarthage.coulisses.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.enicarthage.coulisses.models.Billet;

public class BilletSelection implements Parcelable {
    private Billet billet;
    private int quantity;

    public BilletSelection(Billet billet, int quantity) {
        this.billet = billet;
        this.quantity = quantity;
    }

    protected BilletSelection(Parcel in) {
        billet = in.readParcelable(Billet.class.getClassLoader());
        quantity = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(billet, flags);
        dest.writeInt(quantity);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BilletSelection> CREATOR = new Creator<BilletSelection>() {
        @Override
        public BilletSelection createFromParcel(Parcel in) {
            return new BilletSelection(in);
        }

        @Override
        public BilletSelection[] newArray(int size) {
            return new BilletSelection[size];
        }
    };

    // Getters and setters
    public Billet getBillet() {
        return billet;
    }

    public void setBillet(Billet billet) {
        this.billet = billet;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
