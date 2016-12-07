package net.hjord.shoppinglist.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

/**
 * Created by Hjord on 15/09/2016
 */
public class Product implements Parcelable {

    //Properties
    private int quantity;
    private String name;
    private boolean checked;
    private int positionInList;

    //Getters & setters
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity){this.quantity = quantity;}
    public String getName() {
        return name;
    }
    private void setName(String name){this.name = name;}
    @Exclude
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }


    //Constructors
    public Product(){}

    public Product(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    //Stuff for putting the class in a bundle, for keeping data at rotation
    protected Product(Parcel in) {
        quantity = in.readInt();
        name = in.readString();
        checked = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(quantity);
        dest.writeString(name);
        dest.writeByte((byte) (checked ? 1 : 0));
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}
